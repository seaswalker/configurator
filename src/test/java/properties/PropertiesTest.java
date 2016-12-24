package properties;

import bean.BeanContainer;
import conf.ConfSource;
import conf.PropertiesConfSource;
import inject.Injecter;
import org.junit.Test;

/**
 * 测试从属性文件中加载配置.
 *
 * @author skywalker
 */
public class PropertiesTest {

    @Test
    public void properties() {
        ConfSource confSource = new PropertiesConfSource("etc/db.properties");
        Injecter injecter = new Injecter();
        BeanContainer beanContainer = injecter.enbaleConf().basePackage("properties").source(confSource).inject();
        DB db = beanContainer.get(DB.class);
        System.out.println(db);
    }

}
