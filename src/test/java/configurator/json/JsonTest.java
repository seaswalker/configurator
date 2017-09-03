package configurator.json;

import configurator.bean.BeanContainer;
import configurator.conf.CompositeSource;
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
        Source classPathSource = new JsonSource("classpath:conf_classpath.json");
        CompositeSource compositeSource = new CompositeSource();
        compositeSource.registerSource(source, classPathSource);
        Injector injector = new Injector();
        BeanContainer beanContainer = injector.basePackage("configurator.json").source(compositeSource).inject();
        JsonHolder holder = beanContainer.get(JsonHolder.class);
        System.out.println(holder);
    }

}
