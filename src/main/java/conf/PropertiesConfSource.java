package conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 属性文件来源.
 *
 * @author skywalker
 */
public class PropertiesConfSource extends AbstractConfSource {

    public PropertiesConfSource(String path) {
        super(path);
    }

    @Override
    public void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(path));
            properties.forEach((key, value) -> {
                holder.addValue((String) key, value.toString());
            });
        } catch (IOException e) {
            logger.error("{} load failed.", path, e);
        }
    }

}
