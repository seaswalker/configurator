package configurator.conf;

/**
 * {@link Source}骨架实现，提供统一的{@link Source#contains(String)}方法实现.
 *
 * @author skywalker
 */
public abstract class AbstractSource implements Source {

    @Override
    public boolean contains(String key) {
        return (get(key) != null);
    }

    /**
     * 获取配置中int形式的值.
     */
    @Override
    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * 获取配置中long形式的值.
     */
    @Override
    public long getLong(String key) {
        return Long.parseLong(get(key));
    }

    /**
     * 获取配置中double形式的值.
     */
    @Override
    public double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    /**
     * 获取配置中boolean形式的值.
     */
    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * 获取配置中String数组形式的值.此方法自定义分隔符的情况下是使用.
     */
    @Override
    public String[] getStringArray(String key, String separator) {
        return get(key).split(separator);
    }

    @Override
    public String[] getStringArray(String key) {
        return null;
    }

}
