package configurator.ioc;

import configurator.bean.Scope;
import configurator.bean.annotation.Component;
import configurator.bean.annotation.Destroy;

import javax.annotation.Resource;

/**
 * 学生.
 *
 * @author skywalker
 */
@Component(scope = Scope.SINGLETON)
public class Student {

    @Resource
    private Teacher teacher;

    private String name;
    private int age;

    public Student() {
        this.name = "skywalker";
        this.age = 10;
    }

    @Destroy(order = 1)
    public void end1() {
        System.out.println("学生gg1");
    }

    @Destroy(order = 2)
    public void end2() {
        System.out.println("学生gg2");
    }

    @Override
    public String toString() {
        return "Student{" +
                "teacher=" + teacher +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

}
