package configurator.inject;

import configurator.bean.BeanContainer;
import configurator.bean.annotation.Component;
import configurator.conf.Source;
import configurator.conf.exception.LoadException;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * 注入器.
 *
 * @author skywalker
 */
public class Injector {

    private Source source;
    /**
     * 扫描的包.
     */
    private String basePackage;
    private boolean allowCircularReference = true;

    /**
     * 设置配置来源{@link Source}.
     *
     * @param source {@linkplain Source}
     * @return {@link Injector}
     */
    public Injector source(Source source) throws LoadException {
        this.source = source;
        source.load();
        return this;
    }

    /**
     * 设置扫描的包.
     *
     * @return {@link Injector}
     */
    public Injector basePackage(String base) {
        this.basePackage = base;
        return this;
    }

    /**
     * 设置是否开启允许循环引用，默认开启，注意:
     * <p>此选项仅在属性注入和方法注入且被引用的bean的作用域为{@link configurator.bean.Scope#SINGLETON}时有效.</p>
     */
    public Injector allowCircularReference(boolean allowCircularReference) {
        this.allowCircularReference = allowCircularReference;
        return this;
    }

    /**
     * 执行注入.
     *
     * @return {@link BeanContainer}
     */
    public BeanContainer inject() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().
                setUrls(ClasspathHelper.forPackage(basePackage)));
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class, true);
        BeanContainer container = new BeanContainer(source, allowCircularReference);
        classes.forEach(c -> {
            if ((!Modifier.isAbstract(c.getModifiers())) && !c.isInterface() && Modifier.isPublic(c.getModifiers())) {
                container.register(c);
            }
        });
        return container;
    }

}
