package configurator.bean.annotation;

import configurator.bean.Scope;

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

    String name() default "";

    Scope scope() default Scope.SINGLETON;

}
