package bean;

import bean.annotation.Component;

/**
 * {@link Component}包装，提供{@link Scope}逻辑支持.
 *
 * @author skywalker
 */
public class BeanWrapper {

    private Scope scope;
    private String beanName;
    private Class targetClass;
    private Object target;
    /**
     * 是否正在创建对应的bean实例.
     */
    private boolean currentlyInCreation = false;

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean isCurrentlyInCreation() {
        return currentlyInCreation;
    }

    public void setCurrentlyInCreation(boolean currentlyInCreation) {
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
