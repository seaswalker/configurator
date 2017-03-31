package configurator.ioc;

import configurator.bean.BeanContainer;
import configurator.bean.annotation.Component;
import configurator.conf.CompositeSource;
import configurator.conf.JsonSource;
import configurator.conf.PropertiesSource;
import configurator.conf.XmlSource;
import configurator.conf.exception.LoadException;
import configurator.inject.Injector;
import org.junit.Assert;
import org.junit.Test;

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
        Injector injector = new Injector().source(source);
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        Teacher teacher = (Teacher) container.get("teacher");
        System.out.println(teacher);
    }

    /**
     * 测试获取不存在的bean name.
     */
    @Test(expected = NullPointerException.class)
    public void getByNameNull() {
        Injector injector = new Injector();
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        Teacher teacher = (Teacher) container.get("tea");
        System.out.println(teacher.toString());
    }

    /**
     * 测试{@link configurator.bean.Scope}.
     */
    @Test
    public void scope() {
        Injector injector = new Injector();
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        Student s1 = (Student) container.get("student");
        Student s2 = (Student) container.get("student");
        Assert.assertTrue(s1 != s2);
    }

    @Test
    public void annotation() throws ClassNotFoundException {
        Class clazz = Class.forName("configurator.ioc.ChinaStudent");
        Component component = (Component) clazz.getAnnotation(Component.class);
        System.out.println(component);
    }

    @Test
    public void threadSafe() throws ExecutionException, InterruptedException {
        Injector injector = new Injector();
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        ExecutorService service = Executors.newFixedThreadPool(2);
        Callable<Teacher> task = () -> container.get(Teacher.class);
        Future<Teacher> s1 = service.submit(task);
        Future<Teacher> s2 = service.submit(task);
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
        Injector injector = new Injector().source(source).allowCircularReference(true);
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        System.out.println(container.get(Student.class));
    }

    @Test
    public void destroy() {
        Injector injector = new Injector();
        BeanContainer container = injector.basePackage("configurator.ioc").inject();
        container.get(ChinaStudent.class);
        container.get("student");
        container.detachBean(ChinaStudent.class);
        container.close();
    }

}
