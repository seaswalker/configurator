package ioc;

import bean.BeanContainer;
import inject.Injecter;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试IOC功能.
 *
 * @author skywalker
 */
public class IOCTest {

    /**
     * 测试根据Bean名称获取.
     */
    @Test
    public void getByName() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").enableIoc().inject();
        Teacher teacher = (Teacher) container.get("teacher");
        teacher.printStudent();
    }

    /**
     * 测试获取不存在的bean name.
     */
    @Test(expected = NullPointerException.class)
    public void getByNameNull() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").enableIoc().inject();
        Teacher teacher = (Teacher) container.get("tea");
        teacher.printStudent();
    }

    /**
     * 测试bean name冲突的情况.需要将{@link Student}的名改为teacher.
     */
    @Test(expected = IllegalStateException.class)
    public void nameComplict() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").enableIoc().inject();
        Teacher teacher = (Teacher) container.get("teacher");
        teacher.printStudent();
    }

    /**
     * 测试{@link bean.Scope}.
     */
    @Test
    public void scope() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").enableIoc().inject();
        Student s1 = (Student) container.get("student");
        Student s2 = (Student) container.get("student");
        Assert.assertTrue(s1 != s2);
    }

}
