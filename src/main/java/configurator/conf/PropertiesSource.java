package configurator.conf;

import configurator.conf.exception.LoadException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 属性文件来源.
 *
 * @author skywalker
 */
public class PropertiesSource extends AbstractTreeBasedSource {

    public PropertiesSource(String path) {
        super(path);
    }

    @Override
    public void load() throws LoadException {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(path));
            properties.forEach((key, value) -> holder.addValue((String) key, value.toString()));
        } catch (IOException e) {
            throw new LoadException(e);
        }
    }

    /**
     * 获取配置中String形式的值.
     */
    @Override
    public String get(String key) {
        return holder.get(key);
    }

}
