package configurator.ioc;

import configurator.bean.Scope;
import configurator.bean.annotation.Component;
import configurator.bean.annotation.Destroy;

/**
 * @author skywalker
 */
@Component(scope = Scope.PROTOTYPE)
public class ChinaStudent extends Student {

    @Override
    public String toString() {
        return "China: " + super.toString();
    }

    @Destroy
    private void destroy() {
        System.out.println("China student gg");
    }

}
