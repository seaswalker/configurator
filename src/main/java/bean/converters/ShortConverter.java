package bean.converters;

/**
 * short转换器.
 *
 * @author skywalker
 */
public class ShortConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (short.class == type || Short.class == type);
    }

    @Override
    public Object convert(String value) {
        return Short.parseShort(value);
    }

}
