package bean;

import conf.ConfSource;
import conf.XMLConfSource;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * {@link Component}容器.
 *
 * @author skywalker
 */
public class BeanContainer {

    private final ConfSource confSource;
    private final boolean isXMLBased;
    private final Map<String, BeanWrapper> nameMap = new HashMap<>();
    private final Map<Class, BeanWrapper> classMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BeanContainer(ConfSource confSource) {
        this.confSource = confSource;
        this.isXMLBased = (confSource instanceof XMLConfSource);
    }

    /**
     * 向Bean容器注册.
     *
     * @param beanClass {@linkplain Class}
     */
    public void register(Class beanClass) {
        if (classMap.containsKey(beanClass)) {
            logger.error("Class {} has been registered already.", beanClass.getName());
            return;
        }
        Component component = (Component) beanClass.getAnnotation(Component.class);
        if (component == null) {
            logger.error("Class {} must be marked by @Component.", beanClass.getName());
            return;
        }
        String beanName = component.name();
        Scope scope = component.scope();
        if (beanName.equals("")) {
            beanName = getBeanName(beanClass);
        }
        if (nameMap.containsKey(beanName)) {
            logger.error("Bean name {} has been registered already.", beanName);
            return;
        }
        BeanWrapper beanWrapper = newBeanWrapper(beanName, scope, beanClass);
        nameMap.put(beanName, beanWrapper);
        classMap.put(beanClass, beanWrapper);
    }

    /**
     * 根据bean名称寻找bean实例，lazy-init.
     *
     * @return {@link Object}
     */
    public Object get(String beanName) {
        BeanWrapper wrapper = nameMap.get(beanName);
        Object result = null;
        if (wrapper != null) {
            result = wrapper.getTarget();
            if (result == null || wrapper.getScope() == Scope.PROTOTYPE) {
                result = createBean(wrapper.getTargetClass());
                if (result != null && wrapper.getScope() == Scope.SINGLETOM) {
                    wrapper.setTarget(result);
                }
            }
        }
        return result;
    }

    /**
     * 创建bean实例.
     */
    private Object createBean(Class beanClass) {
        Object instance = newInstance(beanClass);
        if (instance != null) {
            injectConfs(instance, beanClass);
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
            logger.error("Construct bean failed, maybe there is no default constructor.", e);
        } catch (IllegalAccessException e) {
            logger.error("Construct bean failed, maybe there is no public constructor.", e);
        }
        return instance;
    }

    /**
     * 对bean中所有标注了{@link Value}的{@link java.lang.reflect.Field}和{@link java.lang.reflect.Method}进行注入.
     */
    private void injectConfs(Object instance, Class beanClass) {
        Set<Field> fields = ReflectionUtils.getAllFields(beanClass, ReflectionUtils.withAnnotation(Value.class));
        for (Field field : fields) {
            Value value = field.getAnnotation(Value.class);
            String key = value.key(), attr;
            if (StringUtils.isEmpty(key)) {
                logger.error("Malformed key: {}.", key);
                continue;
            }
            if (isXMLBased) {
                attr = value.attr();
            }
            if (isRequireAll(key)) {

            }
        }
    }

    private boolean isRequireAll(String key) {
        return (key.equals("*"));
    }

}
