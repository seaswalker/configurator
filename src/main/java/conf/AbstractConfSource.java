package conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ConfSource}骨架实现，提供统一的getXXX方法实现.
 *
 * @author skywalker
 */
abstract class AbstractConfSource implements ConfSource {

    protected final String path;
    protected final TrieTree holder = new TrieTree();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected AbstractConfSource(String path) {
        this.path = path;
    }

    /**
     * 获取配置中String形式的值.
     */
    @Override
    public String get(String key) {
        return holder.get(key);
    }

    /**
     * 获取配置中int形式的值.
     */
    @Override
    public int getInt(String key) {
        return Integer.parseInt(holder.get(key));
    }

    /**
     * 获取配置中long形式的值.
     */
    @Override
    public long getLong(String key) {
        return Long.parseLong(holder.get(key));
    }

    /**
     * 获取配置中double形式的值.
     */
    @Override
    public double getDouble(String key) {
        return Double.parseDouble(holder.get(key));
    }

    /**
     * 获取配置中boolean形式的值.
     */
    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(holder.get(key));
    }

    /**
     * 获取配置中String数组形式的值.此方法自定义分隔符的情况下是使用.
     */
    @Override
    public String[] getStringArray(String key, String separator) {
        return holder.get(key).split(separator);
    }

}
