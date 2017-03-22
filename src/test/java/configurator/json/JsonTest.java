package configurator.json;

import configurator.bean.BeanContainer;
import configurator.conf.JsonSource;
import configurator.conf.Source;
import configurator.conf.exception.LoadException;
import configurator.inject.Injector;
import org.junit.Test;

/**
 * 测试{@link configurator.conf.JsonSource}.
 *
 * @author skywalker
 */
public class JsonTest {

    @Test
    public void test() throws LoadException {
        Source source = new JsonSource("etc/conf.json");
        Injector injector = new Injector();
        BeanContainer beanContainer = injector.basePackage("configurator.json").source(source).inject();
        JsonHolder holder = beanContainer.get(JsonHolder.class);
        System.out.println(holder);
    }

}
