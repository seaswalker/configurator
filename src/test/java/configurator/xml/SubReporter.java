package configurator.xml;

import configurator.bean.annotation.Component;

/**
 * 配置是否可以继承注入?
 *
 * @author skywalker
 */
@Component
public class SubReporter extends Reporter {

    @Override
    public String toString() {
        System.out.println("SubReporter打印");
        return super.toString();
    }
}
