package bean.converter;

/**
 * int转换器.
 *
 * @author skywalker
 */
public class IntConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (type == int.class || type == Integer.class);
    }

    @Override
    public Object convert(String value) {
        return Integer.parseInt(value);
    }

}
