package configurator.bean.converter;

/**
 * 类型转换器.Users可实现此接口将其注册到容器中以实现自定义类型转换.
 *
 * @author skywalker
 */
public interface TypeConverter {

    /**
     * 此{@link TypeConverter}是否支持给定的类型.
     *
     * @param type {@linkplain Class}
     * @return true, 如果支持
     */
    boolean support(Class type);

    /**
     * 转换.
     *
     * @param value {@linkplain String} 原值
     * @return 转换后的值
     */
    Object convert(String value);

}
