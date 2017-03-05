package bean.converter;

/**
 * {@link TypeConverter}空实现，直接返回原值.
 *
 * @author skywalker
 */
public class StringConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (String.class == type);
    }

    @Override
    public Object convert(String value) {
        return value;
    }

}
