package bean.converters;

/**
 * double转换器.
 *
 * @author skywalker
 */
public class DoubleConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (double.class == type || Double.class == type);
    }

    @Override
    public Object convert(String value) {
        return Double.parseDouble(value);
    }

}
