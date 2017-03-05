package bean.annotation;

import bean.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被容器管理的组件.
 *
 * @author skywalker
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    public String name() default "";

    public Scope scope() default Scope.SINGLETOM;

}
