package bean.converters;

/**
 * long转换器.
 *
 * @author skywalker
 */
public class LongConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (long.class == type || Long.class == type);
    }

    @Override
    public Object convert(String value) {
        return Long.parseLong(value);
    }

}
