package configurator.xml;

import configurator.bean.BeanContainer;
import configurator.conf.Source;
import configurator.conf.XmlSource;
import configurator.conf.exception.LoadException;
import configurator.inject.Injector;
import org.junit.Test;

/**
 * 测试XML配置的导入.
 *
 * @author skywalker
 */
public class XMLConfTest {

    @Test
    public void xml() throws LoadException {
        Source confSource = new XmlSource("etc/test.xml");
        Injector injector = new Injector();
        BeanContainer beanContainer = injector.basePackage("configurator.xml").source(confSource).inject();
        Reporter reporter = beanContainer.get(SubReporter.class);
        System.out.println(reporter);
    }

}
