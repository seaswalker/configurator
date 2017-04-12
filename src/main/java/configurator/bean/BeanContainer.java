package configurator.bean;

import configurator.bean.annotation.Component;
import configurator.bean.annotation.Destroy;
import configurator.bean.annotation.Init;
import configurator.bean.annotation.Value;
import configurator.bean.converter.*;
import configurator.bean.exception.CircularReferenceException;
import configurator.conf.Source;
import configurator.inject.Injector;
import org.reflections.ReflectionUtils;
import configurator.util.Util;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link Component}容器.
 *
 * @author skywalker
 */
public final class BeanContainer {

    private final Source source;
    private final Map<String, BeanWrapper> nameMap = new HashMap<>();
    private final Map<Class, BeanWrapper> classMap = new HashMap<>();
    private final Object monitor = new Object();
    /**
     * @see Injector#allowCircularReference(boolean)
     */
    private final boolean allowCircularReference;

    private volatile boolean closed = false;

    public BeanContainer(Source source, boolean allowCircularReference) {
        this.source = source;
        this.allowCircularReference = allowCircularReference;
        registerTypeConvertersInternal();
    }

    /**
     * 注册默认的{@link TypeConverter}.
     */
    private void registerTypeConvertersInternal() {
        register(BooleanConverter.class, Scope.SINGLETOM);
        register(ByteConverter.class, Scope.SINGLETOM);
        register(DoubleConverter.class, Scope.SINGLETOM);
        register(FloatConverter.class, Scope.SINGLETOM);
        register(IntConverter.class, Scope.SINGLETOM);
        register(LongConverter.class, Scope.SINGLETOM);
        register(ShortConverter.class, Scope.SINGLETOM);
        register(StringConverter.class, Scope.SINGLETOM);
    }

    /**
     * 向Bean容器注册.注意:
     * <p>beanClass上必须有{@link Component}注解.</p>
     *
     * @param beanClass {@linkplain Class}
     * @throws IllegalStateException 如果注册失败
     */
    public void register(Class beanClass) {
        assertNotClosed();
        Objects.requireNonNull(beanClass);
        Component component = (Component) beanClass.getAnnotation(Component.class);
        if (component == null) {
            throw new IllegalStateException("Class '" + beanClass.getName() + "' must be marked by @Component.");
        }
        String beanName = component.name();
        Scope scope = component.scope();
        doRegister(beanClass, scope, beanName);
    }

    /**
     * 允许向容器注册未被{@link Component}标注的bean.
     *
     * @param beanClass {@link Class}
     * @param scope     {@link Scope}
     */
    public void register(Class beanClass, Scope scope) {
        assertNotClosed();
        Objects.requireNonNull(beanClass);
        Objects.requireNonNull(scope);
        doRegister(beanClass, scope, null);
    }

    /**
     * 允许向容器注册未被{@link Component}标注的bean.
     *
     * @param beanClass {@link Class}
     * @param scope     {@link Scope}
     * @param name      bean名称
     */
    public void register(Class beanClass, Scope scope, String name) {
        assertNotClosed();
        Objects.requireNonNull(beanClass);
        Objects.requireNonNull(scope);
        if (Util.isEmpty(name)) {
            throw new IllegalArgumentException("Parameter name can't be null or empty.");
        }
        doRegister(beanClass, scope, name);
    }

    /**
     * 真正的执行注册操作.
     *
     * @see #register(Class, Scope, String)
     */
    private void doRegister(Class beanClass, Scope scope, String beanName) {
        synchronized (monitor) {
            if (classMap.containsKey(beanClass)) {
                throw new IllegalStateException("Class '" + beanClass.getName() + "' has been registered already.");
            }
            if (Util.isEmpty(beanName)) {
                beanName = getBeanName(beanClass);
            }
            if (nameMap.containsKey(beanName)) {
                throw new IllegalStateException("Bean name '" + beanName + "' has been registered already.");
            }
            BeanWrapper beanWrapper = newBeanWrapper(beanName, scope, beanClass);
            nameMap.put(beanName, beanWrapper);
            classMap.put(beanClass, beanWrapper);
        }
    }

    /**
     * 根据bean名称寻找bean实例，lazy-init.
     *
     * @return {@link Object}
     * @throws IllegalArgumentException 如果参数为null或empty
     */
    public Object get(String beanName) {
        assertNotClosed();
        if (Util.isEmpty(beanName)) {
            throw new IllegalArgumentException("Param beanName can't be null or empty.");
        }
        Object result = null;
        synchronized (monitor) {
            BeanWrapper wrapper = nameMap.get(beanName);
            if (wrapper != null) {
                result = loadBean(wrapper);
            }
        }
        return result;
    }

    /**
     * 根据{@link Class}获取bean实例.
     *
     * @throws IllegalStateException 如果发现多个候选者
     */
    public <T> T get(Class<T> beanClass) {
        assertNotClosed();
        Objects.requireNonNull(beanClass);
        List<BeanWrapper> candidates = new ArrayList<>();
        T result = null;
        synchronized (monitor) {
            for (Map.Entry<Class, BeanWrapper> entry : classMap.entrySet()) {
                if (beanClass.isAssignableFrom(entry.getKey())) {
                    candidates.add(entry.getValue());
                }
            }
            BeanWrapper beanWrapper = findEligibleCandidate(candidates, beanClass);
            if (beanWrapper != null) {
                result = (T) loadBean(beanWrapper);
            }
        }
        return result;
    }

    /**
     * 在候选者中寻找最合适的一个，逻辑:
     * <ul>
     * <li>如果candidates为空，那么直接返回null.</li>
     * <li>如果candidates大小为1，那么返回第一个.</li>
     * <li>如果candidates大小大于1，那么寻找targetClass与requiredType相等(==)的候选者.</li>
     * </ul>
     *
     * @throws IllegalStateException 如果发现多个候选者
     */
    private <T> BeanWrapper findEligibleCandidate(List<BeanWrapper> candidates, Class<T> requiredType) {
        int size = candidates.size();
        BeanWrapper result = null;
        if (size == 1) {
            result = candidates.get(0);
        } else if (size > 1) {
            for (BeanWrapper candidate : candidates) {
                if (candidate.getTargetClass() == requiredType) {
                    result = candidate;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalStateException("Given bean class has one more candidates: " +
                        getCandidatesInfo(candidates) + ".");
            }
        }
        return result;
    }

    /**
     * 获得所有clazz类型的bean.
     *
     * @param clazz {@link Class} 类型
     * @param <T>   clazz的真实类型
     * @return {@link List}, 如果没有找到任何bean，那么返回空链表，而不是null
     */
    public <T> List<T> getBeansWithType(Class<T> clazz) {
        assertNotClosed();
        Objects.requireNonNull(clazz);
        List<T> beans = new LinkedList<T>();
        synchronized (monitor) {
            classMap.forEach((key, value) -> {
                if (clazz.isAssignableFrom(key)) {
                    beans.add((T) loadBean(value));
                }
            });
        }
        return beans;
    }

    /**
     * 初始化{@link BeanWrapper}.注意:
     * <p>此方法一定是在持有锁的前提下执行的.</p>
     *
     * @param beanWrapper {@linkplain BeanWrapper} 非空
     * @return 初始化的bean实例
     * @throws CircularReferenceException 如果满足以下条件:
     *                                    <ul>
     *                                    <li>{@link #allowCircularReference}设为false.</li>
     *                                    <li>bean尚未完成初始化(构造器).</li>
     *                                    <li>bean的{@link Scope}不为{@linkplain Scope#SINGLETOM}.</li>
     *                                    </ul>
     */
    private Object loadBean(BeanWrapper beanWrapper) {
        Object result = beanWrapper.getTarget();
        if (beanWrapper.isCurrentlyInCreation()) {
            if (!allowCircularReference || (result == null) || beanWrapper.getScope() != Scope.SINGLETOM) {
                throw new CircularReferenceException("Circular reference bean '" + beanWrapper.getBeanName() + "'.");
            }
            return result;
        }
        if (result == null || beanWrapper.getScope() == Scope.PROTOTYPE) {
            beanWrapper.setCurrentlyInCreation(true);
            result = createBean(beanWrapper);
            if (result != null && beanWrapper.getScope() == Scope.SINGLETOM) {
                beanWrapper.setTarget(result);
            }
            beanWrapper.setCurrentlyInCreation(false);
        }
        return result;
    }

    /**
     * 将{@link BeanWrapper}的targetClass属性拼为字符串.
     */
    private String getCandidatesInfo(List<BeanWrapper> candidates) {
        List<String> classes = candidates.stream().map(beanWrapper -> beanWrapper.getTargetClass().getName()).
                collect(Collectors.toList());
        return ("[" + String.join(",", classes) + "}");
    }

    /**
     * 如果当前容器已关闭，那么抛出{@link IllegalStateException}.
     */
    private void assertNotClosed() {
        if (closed) {
            throw new IllegalStateException("The container is closed.");
        }
    }

    /**
     * 创建bean实例.
     */
    private Object createBean(BeanWrapper beanWrapper) {
        Class beanClass = beanWrapper.getTargetClass();
        Object instance = newInstance(beanClass);
        if (instance != null) {
            if (allowCircularReference && beanWrapper.getScope() == Scope.SINGLETOM) {
                //earlyReference
                beanWrapper.setTarget(instance);
            }
            if (source != null) {
                injectConfs(instance, beanClass);
            }
            injectDependencies(instance, beanClass);
            if (instance instanceof BeanContainerAware) {
                BeanContainerAware aware = (BeanContainerAware) instance;
                aware.setBeanContainer(this);
            }
            invokeInitMethodsIfNecessary(beanClass, instance);
        }
        return instance;
    }

    /**
     * 销毁beanClass对应的bean，注意，不同于{@link #get(Class)}，本方法不会销毁
     * 当前类的子类的bean.
     *
     * @param destroyHint 如果为false，那么容器将不会尝试调用销毁方法
     */
    public void detachBean(Class<?> beanClass, boolean destroyHint) {
        assertNotClosed();
        Objects.requireNonNull(beanClass);
        BeanWrapper beanWrapper;
        synchronized (monitor) {
            beanWrapper = classMap.remove(beanClass);
            nameMap.remove(beanWrapper.getBeanName());
        }
        if (beanWrapper != null && destroyHint) {
            invokeDestroyMethodsIfNecessary(beanWrapper);
        }
    }

    /**
     * @see #detachBean(Class, boolean)
     */
    public void detachBean(Class<?> beanClass) {
        detachBean(beanClass, true);
    }

    /**
     * 销毁name对应的bean.
     *
     * @param destroyHint 如果为false，那么容器将不会尝试调用销毁方法
     */
    public void detachBean(String name, boolean destroyHint) {
        assertNotClosed();
        if (Util.isEmpty(name)) {
            throw new IllegalArgumentException("Parameter 'name' can't be null or empty.");
        }
        BeanWrapper beanWrapper;
        synchronized (monitor) {
            beanWrapper = nameMap.remove(name);
            classMap.remove(beanWrapper.getTargetClass());
        }
        if (beanWrapper != null && destroyHint) {
            invokeDestroyMethodsIfNecessary(beanWrapper);
        }
    }

    /**
     * @see #detachBean(String, boolean)
     */
    public void detachBean(String name) {
        detachBean(name, true);
    }

    /**
     * 如果bean中定义了{@link configurator.bean.annotation.Init}方法，调用之.
     *
     * @param beanClass {@link Class} bean的类型
     * @param instance  bean实例
     */
    private void invokeInitMethodsIfNecessary(Class beanClass, Object instance) {
        Set<Method> methods = ReflectionUtils.getMethods(beanClass, ReflectionUtils.withAnnotation(Init.class));
        if (methods.size() > 0) {
            List<Method> sorted = new ArrayList<>(methods);
            //倒序排列
            sorted.sort((o1, o2) -> o2.getAnnotation(Init.class).order() - o1.getAnnotation(Init.class).order());
            sorted.forEach(method -> {
                Parameter[] parameters = method.getParameters();
                Object[] args = parameters.length > 0 ? resolveArgs(parameters) : new Object[0];
                invokeMethod(method, instance, args);
            });
        }
    }

    /**
     * 根据方法的参数列表解析得到相应的参数.
     * <ul>
     * <li>如果参数被{@link Value}标注，那么进行配置注入.</li>
     * <li>是基本类型，抛出{@link IllegalStateException}.</li>
     * <li>根据类型去容器查找.</li>
     * <li>最终还是没有找到，抛出{@link IllegalStateException}.</li>
     * </ul>
     *
     * @param parameters 参数列表，长度大于零
     * @return 解析得到参数数组
     */
    private Object[] resolveArgs(Parameter[] parameters) {
        int i = 0, l = parameters.length;
        Object[] result = new Object[l];
        for (; i < l; i++) {
            Parameter parameter = parameters[i];
            Object value = resolveArg(parameter);
            if (value == null) {
                throw new IllegalStateException("Can't find eligible value for parameter: " + parameter + ".");
            }
            result[i] = resolveArg(parameter);
        }
        return result;
    }

    /**
     * 参数解析.
     *
     * @param parameter {@link Parameter}
     * @return 参数值
     * @see #resolveArgs(Parameter[])
     */
    private Object resolveArg(Parameter parameter) {
        Value value = parameter.getAnnotation(Value.class);
        Object result = null;
        //jdk1.8 javac -parameters参数
        String name = (parameter.isNamePresent() ? parameter.getName() : null);
        if (value != null) {
            result = resolveConfByValue(parameter, name, parameter.getType(), parameter.getParameterizedType());
        } else {
            Class type = parameter.getType();
            if (type.isPrimitive()) {
                throw new IllegalStateException("Can't inject to primitive type: " + parameter + ".");
            }
            if (name != null) {
                result = get(name);
            }
            if (result == null) {
                result = get(type);
            }
        }
        return result;
    }

    /**
     * 生成bean的名称.
     *
     * @param beanClass {@link Class} bean的类型
     */
    private String getBeanName(Class beanClass) {
        String name = beanClass.getSimpleName();
        return (name.substring(0, 1).toLowerCase() + name.substring(1));
    }

    /**
     * 构造{@link BeanWrapper}.
     */
    private BeanWrapper newBeanWrapper(String beanName, Scope scope, Class beanClass) {
        BeanWrapper wrapper = new BeanWrapper();
        wrapper.setBeanName(beanName);
        wrapper.setScope(scope);
        wrapper.setTargetClass(beanClass);
        return wrapper;
    }

    /**
     * 使用给定的{@linkplain Class}的无参构造器构造一个对象.
     *
     * @param beanClass {@link Class} bean类型
     * @throws IllegalStateException 如果构造失败
     */
    private Object newInstance(Class beanClass) {
        Object instance;
        try {
            Constructor[] constructors = beanClass.getConstructors();
            int length = constructors.length;
            if (length == 0) {
                throw new IllegalStateException("There are no public constructors in " + beanClass.getName() + ".");
            }
            if (length > 1) {
                throw new IllegalStateException("There are more than one public constructors in " + beanClass.getName() + ".");
            }
            Constructor constructor = constructors[0];
            Parameter[] parameters = constructor.getParameters();
            Object[] args = resolveArgs(parameters);
            instance = constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Construct bean failed, maybe there is no default constructor.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Construct bean failed, maybe there is no public constructor.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Exception occurred when constructing bean " + beanClass.getName() + ".", e);
        }
        return instance;
    }

    /**
     * 对bean中所有标注了{@link Value}的{@link java.lang.reflect.Field}和{@link java.lang.reflect.Method}进行注入.
     */
    private void injectConfs(Object instance, Class beanClass) {
        injectConfByField(instance, beanClass);
        injectConfByMethod(instance, beanClass);
    }

    /**
     * 对标注在{@link Field}上的{@link Value}进行注入.
     */
    private void injectConfByField(Object instance, Class beanClass) {
        Set<Field> fields = ReflectionUtils.getAllFields(beanClass, ReflectionUtils.withAnnotation(Value.class));
        for (Field field : fields) {
            Object result = resolveConfByValue(field, field.getName(), field.getType(), field.getGenericType());
            if (result != null) {
                setFieldValue(field, instance, result);
            }
        }
    }

    /**
     * 对标注在{@link java.lang.reflect.Method}上的{@link Value}进行注入.
     */
    private void injectConfByMethod(Object instance, Class beanClass) {
        Set<Method> methods = ReflectionUtils.getAllMethods(beanClass, ReflectionUtils.withAnnotation(Value.class));
        for (Method method : methods) {
            int count = method.getParameterCount();
            if (count != 1) {
                throw new IllegalStateException("Unsupported method parameter count: " + count + ", method: "
                        + method.toString() + ".");
            }
            Class[] methodClasses = method.getParameterTypes();
            Type[] methodTypes = method.getGenericParameterTypes();
            Object result = resolveConfByValue(method, resolveSetterMethodName(method), methodClasses[0], methodTypes[0]);
            if (result != null) {
                invokeMethod(method, instance, result);
            }
        }
    }

    /**
     * 根据setter方法名得到key.
     *
     * @param method {@link Method}
     */
    private String resolveSetterMethodName(Method method) {
        String name = null;
        String methodName = method.getName();
        if (methodName.startsWith("set") && methodName.length() > 3) {
            name = methodName.substring(3, methodName.length()).toLowerCase();
        }
        return name;
    }

    /**
     * 根据{@link Value}得到配置的值.
     *
     * @param object {@link AnnotatedElement} 被{@link Value}标注的元素
     * @param name   如果{@link Value#key()}没有设置，默认用以进行查找的名字，比如{@link Field}的名称
     * @param clazz  object的真实类型
     * @param type   object的泛型类型
     */
    private Object resolveConfByValue(AnnotatedElement object, String name, Class clazz, Type type) {
        Object result;
        Value value = object.getAnnotation(Value.class);
        String key = value.key();
        if (Util.isEmpty(key)) {
            key = name;
            if (Util.isEmpty(key)) {
                throw new IllegalStateException("Key must be confirmed: " + object + ".");
            }
        }
        if (needFind(key)) {
            if (!isEligibleMap(clazz, type)) {
                throw new IllegalStateException("Inject all configurations for " + object +
                        " failed, type Map<String,String> required.");
            }
            result = source.find(handlePrefix(key));
        } else {
            if (!source.contains(key)) {
                String defaultValue = value.defaultValue();
                if (Util.isEmpty(defaultValue)) {
                    throw new IllegalStateException("No key: " + key + " and defaultValue found.");
                }
                result = convertTo(defaultValue, clazz);
            } else if (clazz == String[].class) {
                String separator = value.separator();
                result = Util.isEmpty(separator) ? source.getStringArray(key) :
                        source.getStringArray(key, separator);
            } else {
                result = convertTo(source.get(key), clazz);
            }
        }
        return result;
    }

    /**
     * 将给定的值转换为指定的类型.
     *
     * @param value        待转换的值
     * @param requiredType {@linkplain Class} 需要的类型
     * @return 转换后的值
     * @throws IllegalStateException 如果没有合适的{@link TypeConverter}可用
     */
    private Object convertTo(String value, Class requiredType) {
        List<TypeConverter> converters = getBeansWithType(TypeConverter.class);
        for (TypeConverter converter : converters) {
            if (converter.support(requiredType)) {
                Object result = converter.convert(value);
                if (result != null) {
                    return result;
                }
            }
        }
        throw new IllegalStateException("No eligible TypeConverter found for type: " + requiredType.getName() + ".");
    }

    /**
     * 是否需要进行前缀搜索.
     *
     * @return true, 如果需要
     */
    private boolean needFind(String key) {
        String[] parts = key.split("\\.");
        return (parts[parts.length - 1].equals("*"));
    }

    /**
     * 将(a.b.*)处理为(a.b).
     */
    private String handlePrefix(String key) {
        String[] parts = key.split("\\.");
        if (parts.length == 1) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                sb.append(parts[i]).append(".");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
    }

    /**
     * 给定的字段是否是{@link Map}，且泛型满足{@link Source}.find()方法的返回值.
     *
     * @return true，如果满足
     */
    private boolean isEligibleMap(Class clazz, Type type) {
        if (Map.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                Type[] types = pt.getActualTypeArguments();
                if (types.length != 2) {
                    return false;
                }
                if (!(types[0] instanceof Class) || !(types[1] instanceof Class)) {
                    return false;
                }
                return (Class.class.cast(types[0]).isAssignableFrom(String.class) &&
                        Class.class.cast(types[1]).isAssignableFrom(String.class));
            } else {
                //raw type
                return true;
            }
        }
        return false;
    }

    /**
     * 为所有标注了{@link javax.annotation.Resource}的{@link Field}和{@link Method}进行依赖注入.
     */
    private void injectDependencies(Object instance, Class beanClass) {
        injectDependenciesByField(instance, beanClass);
        injectDependenciesByMethod(instance, beanClass);
    }

    /**
     * 为标注了{@link Resource}的{@link Field}进行依赖注入.注入的逻辑和Spring保持一致:
     * <br>
     * 1. 如果设置了name属性，那么按照name注入.
     * <br>
     * 2. 如果设置了type属性，那么按type注入.
     * <br>
     * 3. 如果同时设置了name和type属性，那么先进行按name注入，如果没有找到，再按type注入.
     * <br>
     * 4. 如果都没有设置，那么将按name注入.
     *
     * @throws IllegalStateException 如果没有找到或找到多个候选者
     */
    private void injectDependenciesByField(Object instance, Class beanClass) {
        Set<Field> fields = ReflectionUtils.getAllFields(beanClass, ReflectionUtils.withAnnotation(Resource.class));
        for (Field field : fields) {
            Resource resource = field.getAnnotation(Resource.class);
            String name = resource.name();
            Class type = resource.type();
            Class fieldClass = field.getType();
            Object dependency;
            if (type != Object.class && Util.isEmpty(name)) {
                //by type
                assertAssignable(fieldClass, type);
                dependency = get(type);
            } else {
                //by name
                String fieldName = (Util.isEmpty(name) ? field.getName() : name);
                dependency = get(fieldName);
                if (dependency == null && type != Object.class) {
                    assertAssignable(fieldClass, type);
                    dependency = get(type);
                }
            }
            if (dependency != null) {
                setFieldValue(field, instance, dependency);
            } else {
                throw new IllegalStateException("Can't find a candidate for field: " + field.toString() + ".");
            }
        }
    }

    /**
     * 设置{@link Field}的值，提供统一的异常捕获处理.
     */
    private void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Inject to field '" + field.toString() + "' failed, maybe it's not accessible.", e);
        }
    }

    /**
     * 为标注了{@link Resource}的{@link Method}进行依赖注入，注入的逻辑同injectDependenciesByField()，注意以下两点:
     * <br>
     * 1. 如果没有设置name属性，那么将按照第一个参数的参数名进行注入.
     * <br>
     * 2. 仅支持目标方法有一个参数.
     *
     * @throws IllegalStateException 如果没有找到或找到多个候选者或参数不唯一
     */
    private void injectDependenciesByMethod(Object instance, Class beanClass) {
        Set<Method> methods = ReflectionUtils.getAllMethods(beanClass, ReflectionUtils.withAnnotation(Resource.class));
        for (Method method : methods) {
            Resource resource = method.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Class type = resource.type();
            Object dependency;
            Parameter[] parameters = method.getParameters();
            if (parameters.length != 1) {
                throw new IllegalStateException("We support one parameter only, method: " + method.toString() + ".");
            }
            Parameter parameter = parameters[0];
            Class<?> parameterClass = parameter.getType();
            if (type != Object.class && Util.isEmpty(resourceName)) {
                //by type
                assertAssignable(parameterClass, type);
                dependency = get(type);
            } else {
                //by name
                String name = (Util.isEmpty(resourceName) ? parameter.getName() : resourceName);
                dependency = get(name);
                if (dependency == null && type != Object.class) {
                    assertAssignable(parameterClass, type);
                    dependency = get(type);
                }
            }
            if (dependency != null) {
                invokeMethod(method, instance, dependency);
            } else {
                throw new IllegalStateException("Can't find a candidate for method: " + method.toString() + ".");
            }
        }
    }

    /**
     * 检查superClass是否是subClass的父类或本类.
     *
     * @throws IllegalStateException 如果不是
     */
    private void assertAssignable(Class<?> superClass, Class<?> subClass) {
        if (!superClass.isAssignableFrom(subClass)) {
            throw new IllegalStateException("Class " + subClass.getName() + " can't be casted to " +
                    superClass.getName());
        }
    }

    /**
     * 反射执行{@link Method}，提供统一的异常捕获处理.
     *
     * @throws IllegalStateException 如果调用失败
     */
    private void invokeMethod(Method method, Object instance, Object... params) {
        try {
            method.setAccessible(true);
            method.invoke(instance, params);
        } catch (Exception e) {
            throw new IllegalStateException("Invoke method '" + method + "' failed.", e);
        }
    }

    /**
     * 关闭容器，如果bean中含有{@link configurator.bean.annotation.Destroy}方法，那么调用之.
     */
    public void close() {
        assertNotClosed();
        closed = true;
        synchronized (monitor) {
            classMap.forEach((key, value) -> {
                invokeDestroyMethodsIfNecessary(value);
            });
        }
    }

    /**
     * 如果bean满足以下两个条件，那么将调用其{@link Destroy}方法.
     * <ul>
     * <li>1. scope为{@link Scope#SINGLETOM}.</li>
     * <li>2. {@link BeanWrapper#getTarget()}不为null，即bean已经初始化.</li>
     * </ul>
     */
    private void invokeDestroyMethodsIfNecessary(BeanWrapper beanWrapper) {
        if (beanWrapper.getScope() == Scope.SINGLETOM && beanWrapper.getTarget() != null) {
            Set<Method> methods = ReflectionUtils.getMethods(beanWrapper.getTargetClass(),
                    ReflectionUtils.withAnnotation(Destroy.class));
            if (methods.size() > 0) {
                final Object[] emptyParams = new Object[0];
                List<Method> sorted = new ArrayList<>(methods);
                sorted.sort((o1, o2) -> o2.getAnnotation(Destroy.class).order() -
                        o1.getAnnotation(Destroy.class).order());
                sorted.forEach(method -> {
                    if (method.getParameterCount() > 0) {
                        throw new IllegalStateException("Destruction method '" + method.getName() + "' in class '" +
                                beanWrapper.getTargetClass().getName() + "' can't have parameters.");
                    }
                    invokeMethod(method, beanWrapper.getTarget(), emptyParams);
                });
            }
        }
    }

}