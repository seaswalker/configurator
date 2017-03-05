package bean.converters;

/**
 * boolean转换器.
 *
 * @author skywalker
 */
public class BooleanConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (boolean.class == type || Boolean.class == type);
    }

    @Override
    public Object convert(String value) {
        return Boolean.parseBoolean(value);
    }

}
