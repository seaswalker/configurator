package json;

import bean.BeanContainer;
import conf.JsonSource;
import conf.Source;
import conf.exception.LoadException;
import inject.Injecter;
import org.junit.Test;
import xml.Reporter;

/**
 * 测试{@link conf.JsonSource}.
 *
 * @author skywalker
 */
public class JsonTest {

    @Test
    public void test() throws LoadException {
        Source source = new JsonSource("etc/conf.json");
        Injecter injecter = new Injecter();
        BeanContainer beanContainer = injecter.enbaleConf().basePackage("xml").source(source).inject();
        JsonHolder holder = beanContainer.get(JsonHolder.class);
        System.out.println(holder);
    }

}
