package properties;

import bean.BeanContainer;
import conf.Source;
import conf.PropertiesSource;
import conf.exception.LoadException;
import inject.Injecter;
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
        Injecter injecter = new Injecter();
        BeanContainer beanContainer = injecter.enbaleConf().basePackage("properties").source(confSource).inject();
        DB db = beanContainer.get(DB.class);
        System.out.println(db);
    }

}
