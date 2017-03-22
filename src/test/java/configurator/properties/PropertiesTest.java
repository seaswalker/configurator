package configurator.properties;

import configurator.bean.BeanContainer;
import configurator.conf.Source;
import configurator.conf.PropertiesSource;
import configurator.conf.exception.LoadException;
import configurator.inject.Injector;
import org.junit.Test;

/**
 * 测试从属性文件中加载配置.
 *
 * @author skywalker
 */
public class PropertiesTest {

    @Test
    public void properties() throws LoadException {
        Source confSource = new PropertiesSource("etc/db.properties");
        Injector injector = new Injector();
        BeanContainer beanContainer = injector.basePackage("configurator.properties").source(confSource).inject();
        DB db = beanContainer.get(DB.class);
        System.out.println(db);
    }

}
