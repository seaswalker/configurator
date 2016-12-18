package bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置注射注解.
 *
 * @author skywalker
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    /**
     * 键值，默认*，即获取所有的配置.
     */
    public String key() default "*";

    /**
     * 属性值，只对{@link conf.XMLConfSource}有效.
     */
    public String attr() default "";

}
