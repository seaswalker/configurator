package xml;

import bean.BeanContainer;
import conf.ConfSource;
import conf.XMLConfSource;
import inject.Injecter;
import org.junit.Test;

/**
 * 测试XML配置的导入.
 *
 * @author skywalker
 */
public class XMLConfTest {

    @Test
    public void xml() {
        ConfSource confSource = new XMLConfSource("etc/test.xml");
        Injecter injecter = new Injecter();
        BeanContainer beanContainer = injecter.enbaleConf().basePackage("xml").source(confSource).inject();
        Reporter reporter = beanContainer.get(Reporter.class);
        System.out.println(reporter);
    }

}
