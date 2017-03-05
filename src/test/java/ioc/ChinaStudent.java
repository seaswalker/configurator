package ioc;

import bean.annotation.Component;

/**
 * @author skywalker
 */
@Component
public class ChinaStudent extends Student {

    @Override
    public String toString() {
        return "China: " + super.toString();
    }
}
