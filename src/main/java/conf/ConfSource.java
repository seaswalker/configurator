package conf;

import java.util.Map;

/**
 * 配置来源.
 *
 * @author skywalker
 */
public interface ConfSource {

    /**
     * 加载配置.
     */
    void load();

    /**
     * 获取配置中String形式的值.
     */
    String get(String key);

    /**
     * 获取配置中int形式的值.
     */
    int getInt(String key);

    /**
     * 获取配置中long形式的值.
     */
    long getLong(String key);

    /**
     * 获取配置中double形式的值.
     */
    double getDouble(String key);

    /**
     * 获取配置中boolean形式的值.
     */
    boolean getBoolean(String key);

    /**
     * 获取配置中String数组形式的值.此方法自定义分隔符的情况下是使用.
     */
    String[] getStringArray(String key, String separator);

    /**
     * 以{@link Map}的形式得到所有的配置.
     *
     * @return {@link Map}
     */
    Map<String, String> getAll();

}
