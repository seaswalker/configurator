package inject;

import bean.BeanContainer;
import bean.annotation.Component;
import conf.Source;
import conf.exception.LoadException;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * 注射器.
 *
 * @author skywalker
 */
public class Injecter {

    private Source source;
    /**
     * 扫描的包.
     */
    private String basePackage;
    private boolean allowCircularReference = true;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 设置配置来源{@link Source}.
     *
     * @param source {@linkplain Source}
     * @return {@link Injecter}
     */
    public Injecter source(Source source) throws LoadException {
        this.source = source;
        source.load();
        return this;
    }

    /**
     * 设置扫描的包.
     *
     * @return {@link Injecter}
     */
    public Injecter basePackage(String base) {
        this.basePackage = base;
        return this;
    }

    /**
     * 设置是否开启允许循环引用，默认开启，注意:
     * <p>此选项仅在属性注入和方法注入且被引用的bean的作用域为{@link bean.Scope#SINGLETOM}时有效.</p>
     */
    public Injecter allowCircularReference(boolean allowCircularReference) {
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
        logger.info("Start scan package: {}.", basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class, true);
        logger.info("Scan package {} finished.", basePackage);
        BeanContainer container = new BeanContainer(source, allowCircularReference);
        classes.forEach(c -> {
            if ((!Modifier.isAbstract(c.getModifiers())) && !c.isInterface()) {
                container.register(c);
            }
        });
        return container;
    }

}
