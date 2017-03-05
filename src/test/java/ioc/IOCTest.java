package ioc;

import bean.BeanContainer;
import bean.annotation.Component;
import conf.CompositeSource;
import conf.JsonSource;
import conf.PropertiesSource;
import conf.XmlSource;
import conf.exception.LoadException;
import inject.Injecter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

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
    public void getByName() throws LoadException {
        CompositeSource source = new CompositeSource();
        source.registerSource(new JsonSource("etc/conf.json"), new PropertiesSource("etc/db.properties"),
                new XmlSource("etc/test.xml"));
        Injecter injecter = new Injecter().source(source);
        BeanContainer container = injecter.basePackage("ioc").inject();
        Teacher teacher = (Teacher) container.get("teacher");
        teacher.printStudent();
    }

    /**
     * 测试获取不存在的bean name.
     */
    @Test(expected = NullPointerException.class)
    public void getByNameNull() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").inject();
        Teacher teacher = (Teacher) container.get("tea");
        teacher.printStudent();
    }

    /**
     * 测试bean name冲突的情况.需要将{@link Student}的名改为teacher.
     */
    @Test(expected = IllegalStateException.class)
    public void nameComplict() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").inject();
        Teacher teacher = (Teacher) container.get("teacher");
        teacher.printStudent();
    }

    /**
     * 测试{@link bean.Scope}.
     */
    @Test
    public void scope() {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").inject();
        Student s1 = (Student) container.get("student");
        Student s2 = (Student) container.get("student");
        Assert.assertTrue(s1 != s2);
    }

    @Test
    public void annotation() throws ClassNotFoundException {
        Class clazz = Class.forName("ioc.ChinaStudent");
        Component component = (Component) clazz.getAnnotation(Component.class);
        System.out.println(component);
    }

    @Test
    public void threadSafe() throws ExecutionException, InterruptedException {
        Injecter injecter = new Injecter();
        BeanContainer container = injecter.basePackage("ioc").inject();
        ExecutorService service = Executors.newFixedThreadPool(2);
        Callable<Student> task = () -> container.get(Student.class);
        Future<Student> s1 = service.submit(task);
        Future<Student> s2 = service.submit(task);
        service.shutdown();
        Assert.assertTrue(s1.get() == s2.get());
    }

    /**
     * 测试{@link BeanContainer#getBeansWithType(Class)}.
     */
    @Test
    public void getWithType() throws LoadException {
        CompositeSource source = new CompositeSource();
        source.registerSource(new JsonSource("etc/conf.json"), new PropertiesSource("etc/db.properties"),
                new XmlSource("etc/test.xml"));
        Injecter injecter = new Injecter().source(source);
        BeanContainer container = injecter.basePackage("ioc").inject();
        List<Student> students = container.getBeansWithType(Student.class);
        System.out.println(students);
    }

}
