package ioc;

import bean.BeanContainer;
import bean.BeanContainerAware;
import bean.annotation.Component;

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
