package ioc;

import bean.BeanContainer;
import bean.BeanContainerAware;
import bean.Component;

import javax.annotation.Resource;

/**
 * 老师.
 *
 * @author skywalker
 */
@Component
public class Teacher implements BeanContainerAware {

    @Resource
    private Student student;

    public void printStudent() {
        System.out.println(student);
    }

    @Override
    public void setBeanContainer(BeanContainer beanContainer) {
        System.out.println("setBeanContainer调用");
    }
}
