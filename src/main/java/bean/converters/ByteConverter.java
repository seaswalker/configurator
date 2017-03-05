package bean.converters;

/**
 * byte转换器.
 *
 * @author skywalker
 */
public class ByteConverter implements TypeConverter {

    @Override
    public boolean support(Class type) {
        return (byte.class == type || Byte.class == type);
    }

    @Override
    public Object convert(String value) {
        return Byte.parseByte(value);
    }

}
