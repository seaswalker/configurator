package xml;

import bean.BeanContainer;
import conf.Source;
import conf.XmlSource;
import conf.exception.LoadException;
import inject.Injecter;
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
        Injecter injecter = new Injecter();
        BeanContainer beanContainer = injecter.basePackage("xml").source(confSource).inject();
        Reporter reporter = beanContainer.get(SubReporter.class);
        System.out.println(reporter);
    }

}
