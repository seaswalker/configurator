package configurator.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 销毁方法.
 *
 * @author skywalker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Destroy {

    /**
     * 方法的优先级，数字越大，优先级越高.
     */
    int order() default 0;

}
