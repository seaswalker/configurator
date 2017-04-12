package configurator.conf;

import configurator.conf.exception.LoadException;

import java.util.Map;

/**
 * 配置来源.
 *
 * @author skywalker
 */
public interface Source {

    /**
     * 加载配置.
     *
     * @throws LoadException 如果加载失败
     */
    void load() throws LoadException;

    /**
     * 配置中是否存在指定的key值.
     *
     * @param key key
     * @return true，如果存在
     */
    boolean contains(String key);

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
     * 节点为数组类型的值，比如json配置文件的基本类型数组.
     */
    String[] getStringArray(String key);

    /**
     * 属性搜索.
     *
     * @param prefix 前缀，比如为a.b，那么将得到a.b下所有的子属性的值，如果为null或""，那么表示获取当前Source下的所有属性.
     * @return {@link Map}
     */
    Map<String, String> find(String prefix);

}
