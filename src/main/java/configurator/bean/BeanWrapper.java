package configurator.bean;

import configurator.bean.annotation.Component;

/**
 * {@link Component}包装，提供{@link Scope}逻辑支持.
 *
 * @author skywalker
 */
public class BeanWrapper<T> {

    private Scope scope;
    private String beanName;
    private Class<T> targetClass;
    private T target;
    /**
     * 是否正在创建对应的bean实例.
     */
    private boolean currentlyInCreation = false;

    Scope getScope() {
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }

    String getBeanName() {
        return beanName;
    }

    void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    Class<T> getTargetClass() {
        return targetClass;
    }

    void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    T getTarget() {
        return target;
    }

    void setTarget(T target) {
        this.target = target;
    }

    boolean isCurrentlyInCreation() {
        return currentlyInCreation;
    }

    void setCurrentlyInCreation(boolean currentlyInCreation) {
        this.currentlyInCreation = currentlyInCreation;
    }

    @Override
    public String toString() {
        return "BeanWrapper{" +
                "scope=" + scope +
                ", beanName='" + beanName + '\'' +
                ", targetClass=" + targetClass +
                ", target=" + target +
                ", currentlyInCreation=" + currentlyInCreation +
                '}';
    }

}
