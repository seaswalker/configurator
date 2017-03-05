package bean.converters;

/**
 * float转换器.
 *
 * @author skywalker
 */
public class FloatConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (float.class == type || Float.class == type);
    }

    @Override
    public Object convert(String value) {
        return Float.parseFloat(value);
    }

}
