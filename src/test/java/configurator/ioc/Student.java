package configurator.ioc;

import configurator.bean.Scope;
import configurator.bean.annotation.Component;

import javax.annotation.Resource;

/**
 * 学生.
 *
 * @author skywalker
 */
@Component(scope = Scope.PROTOTYPE)
public class Student {

    @Resource
    private Teacher teacher;

    private String name;
    private int age;

    public Student() {
        this.name = "skywalker";
        this.age = 10;
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
