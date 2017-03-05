package bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置注入注解.注意:
 * <p>如果要标注在方法参数上，推荐设置key属性，避免使用方法名进行注入，因为只有在JDK1.8 javac开启-parameters选项
 * 时才可以得到真实的方法名.</p>
 *
 * @author skywalker
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    /**
     * 键值，默认*，即获取所有的配置.
     */
    String key() default "";

    /**
     * 分隔符，用于获取String数组时.
     */
    String separator() default "";

    /**
     * 默认值.
     */
    String defaultValue() default "";

}
