package bean.annotation;

import bean.BeanContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 初始化方法.其被调用的时机:
 * bean构造、{@link bean.BeanContainerAware#setBeanContainer(BeanContainer)}执行之后.
 * <p>注意: 此注解只对本类有效，我们将忽略父类中的此注解.</p>
 *
 * @author skywalker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Init {

    /**
     * 方法的优先级，数字越大，优先级越高.
     */
    int order() default 0;

}
