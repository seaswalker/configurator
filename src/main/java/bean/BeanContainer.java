package bean;

import conf.Source;
import conf.XmlSource;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;

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
    private final boolean isXMLBased;
    private final Map<String, BeanWrapper> nameMap = new HashMap<>();
    private final Map<Class, BeanWrapper> classMap = new HashMap<>();
    //是否启用配置注入
    private final boolean isConfEnabled;
    //是否启用依赖注入
    private final boolean isIocEnabled;
    private final Object monitor = new Object();

    public BeanContainer(Source source, boolean isConfEnabled, boolean isIocEnabled) {
        this.source = source;
        this.isXMLBased = (source instanceof XmlSource);
        this.isConfEnabled = isConfEnabled;
        this.isIocEnabled = isIocEnabled;
    }

    /**
     * 向Bean容器注册.
     *
     * @param beanClass {@linkplain Class}
     * @throws IllegalStateException 如果注册失败
     */
    public void register(Class beanClass) {
        synchronized (monitor) {
            if (classMap.containsKey(beanClass)) {
                throw new IllegalStateException("Class '" + beanClass.getName() + "' has been registered already.");
            }
            Component component = (Component) beanClass.getAnnotation(Component.class);
            if (component == null) {
                throw new IllegalStateException("Class '" + beanClass.getName() + "' must be marked by @Component.");
            }
            String beanName = component.name();
            Scope scope = component.scope();
            if (StringUtils.isEmpty(beanName)) {
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
     */
    public Object get(String beanName) {
        Object result = null;
        synchronized (monitor) {
            BeanWrapper wrapper = nameMap.get(beanName);
            if (wrapper != null) {
                result = wrapper.getTarget();
                if (result == null || wrapper.getScope() == Scope.PROTOTYPE) {
                    result = createBean(wrapper.getTargetClass());
                    if (result != null && wrapper.getScope() == Scope.SINGLETOM) {
                        wrapper.setTarget(result);
                    }
                }
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
        List<BeanWrapper> candidates = new ArrayList<>();
        T result = null;
        synchronized (monitor) {
            for (Map.Entry<Class, BeanWrapper> entry : classMap.entrySet()) {
                if (beanClass.isAssignableFrom(entry.getKey())) {
                    candidates.add(entry.getValue());
                }
            }
            int size = candidates.size();
            if (size > 1) {
                throw new IllegalStateException("Given bean class has one more candidates: " + getCandidatesInfo(candidates) + ".");
            }
            if (size == 1) {
                BeanWrapper beanWrapper = candidates.get(0);
                result = (T) beanWrapper.getTarget();
                if (result == null || beanWrapper.getScope() == Scope.PROTOTYPE) {
                    result = (T) createBean(beanWrapper.getTargetClass());
                    if (result != null && beanWrapper.getScope() == Scope.SINGLETOM) {
                        beanWrapper.setTarget(result);
                    }
                }
            }
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
     * 创建bean实例.
     */
    private Object createBean(Class beanClass) {
        Object instance = newInstance(beanClass);
        if (instance != null) {
            if (isConfEnabled) {
                injectConfs(instance, beanClass);
            }
            if (isIocEnabled) {
                injectDependencies(instance, beanClass);
            }
            if (instance instanceof BeanContainerAware) {
                BeanContainerAware aware = (BeanContainerAware) instance;
                aware.setBeanContainer(this);
            }
        }
        return instance;
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
     */
    private Object newInstance(Class beanClass) {
        Object instance = null;
        try {
            instance = beanClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Construct bean failed, maybe there is no default constructor.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Construct bean failed, maybe there is no public constructor.", e);
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
     */
    private Object resolveConfByValue(AccessibleObject object, String name, Class clazz, Type type) {
        Object result;
        Value value = object.getAnnotation(Value.class);
        String key = value.key(), attr = null;
        if (StringUtils.isEmpty(key)) {
            key = name;
            if (StringUtils.isEmpty(key)) {
                throw new IllegalStateException("Key must be confirmed.");
            }
        }
        if (isXMLBased) {
            attr = value.attr();
        }
        if (isRequireAll(key)) {
            if (!isEligibleMap(clazz, type)) {
                throw new IllegalStateException("Inject all configurations for " + object + " failed, type Map<String,String> required.");
            }
            result = source.getAll();
        } else {
            if (clazz == String.class) {
                if (isRequireAttr(attr)) {
                    result = XmlSource.class.cast(source).getAttribute(key, attr);
                } else {
                    result = source.get(key);
                }
            } else if (clazz == int.class) {
                if (isRequireAttr(attr)) {
                    result = XmlSource.class.cast(source).getAttributeAsInt(key, attr);
                } else {
                    result = source.getInt(key);
                }
            } else if (clazz == long.class) {
                if (isRequireAttr(attr)) {
                    result = XmlSource.class.cast(source).getAttributeAsLong(key, attr);
                } else {
                    result = source.getLong(key);
                }
            } else if (clazz == boolean.class) {
                if (isRequireAttr(attr)) {
                    result = XmlSource.class.cast(source).getAttributeAsBoolean(key, attr);
                } else {
                    result = source.getBoolean(key);
                }
            } else if (clazz == double.class) {
                if (isRequireAttr(attr)) {
                    result = XmlSource.class.cast(source).getAttributeAsDouble(key, attr);
                } else {
                    result = source.getDouble(key);
                }
            } else if (clazz == String[].class) {
                String separator = value.separator();
                if (isRequireAttr(attr)) {
                    checkSeparator(separator, object);
                    result = XmlSource.class.cast(source).getAttributeAsStringArray(key, attr, separator);
                } else {
                    result = StringUtils.isEmpty(separator) ? source.getStringArray(key) :
                            source.getStringArray(key, separator);
                }
            } else {
                throw new IllegalStateException("Unsupported target type " + clazz.getSimpleName() + " for AccessibleObject " + object + ".");
            }
        }
        return result;
    }

    /**
     * 检查分隔符是否合法.
     *
     * @throws IllegalStateException 如果不合法
     */
    private void checkSeparator(String separator, AccessibleObject object) {
        if (StringUtils.isEmpty(separator)) {
            throw new IllegalStateException("Must specify separator for String array, target: " + object + ".");
        }
    }

    /**
     * 字段是否需要{@link Source}的所有配置.
     */
    private boolean isRequireAll(String key) {
        return (key.equals("*"));
    }

    /**
     * 判断是否需要注入属性，仅对{@link XmlSource}有效.
     */
    private boolean isRequireAttr(String attr) {
        return (isXMLBased && StringUtils.isNotEmpty(attr));
    }

    /**
     * 给定的字段是否是{@link Map}，且泛型满足{@link Source}.getAll()方法的返回值.
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
            Object dependency = null;
            if (type != Object.class && StringUtils.isEmpty(name)) {
                //by type
                if (!fieldClass.isAssignableFrom(type)) {
                    throw new IllegalStateException("Class " + type.getName() + " can't be casted to " + fieldClass.getName());
                }
                dependency = get(type);
            } else {
                //by name
                String fieldName = (StringUtils.isEmpty(name) ? field.getName() : name);
                dependency = get(fieldName);
                if (dependency == null && type != Object.class) {
                    if (!fieldClass.isAssignableFrom(type)) {
                        throw new IllegalStateException("Class " + type.getName() + " can't be casted to " + fieldClass.getName());
                    }
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
            Object dependency = null;
            Parameter[] parameters = method.getParameters();
            if (parameters.length != 1) {
                throw new IllegalStateException("We support one parameter only, method: " + method.toString() + ".");
            }
            Parameter parameter = parameters[0];
            Class parameterClass = parameter.getType();
            if (type != Object.class && StringUtils.isEmpty(resourceName)) {
                //by type
                if (!parameterClass.isAssignableFrom(type)) {
                    throw new IllegalStateException("Class " + type.getName() + " can't be casted to " + parameterClass.getName());
                }
                dependency = get(type);
            } else {
                //by name
                String name = (StringUtils.isEmpty(resourceName) ? parameter.getName() : resourceName);
                dependency = get(name);
                if (dependency == null && type != Object.class) {
                    if (!parameterClass.isAssignableFrom(type)) {
                        throw new IllegalStateException("Class " + type.getName() + " can't be casted to " + parameterClass.getName());
                    }
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
     * 反射执行{@link Method}，提供统一的异常捕获处理.
     *
     * @throws IllegalStateException 如果调用失败
     */
    private void invokeMethod(Method method, Object instance, Object... params) {
        try {
            method.setAccessible(true);
            method.invoke(instance, params);
        } catch (Exception e) {
            throw new IllegalStateException("Inject to method '" + method + "' failed.", e);
        }
    }

}
