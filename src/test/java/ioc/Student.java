package ioc;

import bean.Component;
import bean.Scope;

/**
 * 学生.
 *
 * @author skywalker
 */
@Component(scope = Scope.PROTOTYPE)
public class Student {

    private String name;
    private int age;

    public Student() {
        this.name = "skywalker";
        this.age = 10;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
