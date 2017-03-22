package configurator.ioc;

import configurator.bean.BeanContainer;
import configurator.bean.BeanContainerAware;
import configurator.bean.annotation.Component;

/**
 * 老师.
 *
 * @author skywalker
 */
@Component
public class Teacher implements BeanContainerAware {


    @Override
    public void setBeanContainer(BeanContainer beanContainer) {
        System.out.println("setBeanContainer调用");
    }

}
