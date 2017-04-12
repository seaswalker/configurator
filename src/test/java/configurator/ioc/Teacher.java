package configurator.ioc;

import configurator.bean.BeanContainer;
import configurator.bean.BeanContainerAware;
import configurator.bean.annotation.Component;
import configurator.bean.annotation.Value;

import java.util.Map;

/**
 * 老师.
 *
 * @author skywalker
 */
@Component
public class Teacher implements BeanContainerAware {

    @Value(key = "*")
    private Map<String, String> all;


    @Override
    public void setBeanContainer(BeanContainer beanContainer) {
        System.out.println("setBeanContainer调用");
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "all=" + all +
                '}';
    }

}
