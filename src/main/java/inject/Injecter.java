package inject;

import bean.BeanContainer;
import bean.Component;
import conf.Source;
import conf.exception.LoadException;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 注射器.
 *
 * @author skywalker
 */
public class Injecter {

    private Source source;
    //扫描的包
    private String basePackage;
    //是否启用配置注入
    private boolean isConfEnabled = false;
    //是否启用依赖注入
    private boolean isIocEnabled = false;
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
     * 启用配置注入.
     *
     * @return {@link Injecter}
     */
    public Injecter enbaleConf() {
        this.isConfEnabled = true;
        return this;
    }

    /**
     * 启用依赖注入，即IOC.
     *
     * @return {@link Injecter}
     */
    public Injecter enableIoc() {
        this.isIocEnabled = true;
        return this;
    }

    /**
     * 执行注入.
     *
     * @return {@link BeanContainer}
     */
    public BeanContainer inject() {
        if (!isConfEnabled && !isIocEnabled) {
            throw new IllegalStateException("You must enable conf inject or ioc or both.");
        }
        Reflections reflections = new Reflections(new ConfigurationBuilder().
                setUrls(ClasspathHelper.forPackage(basePackage)));
        logger.info("Start scan package: {}.", basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        logger.info("Scan package {} finished.", basePackage);
        BeanContainer container = new BeanContainer(source, isConfEnabled, isIocEnabled);
        classes.forEach(c -> container.register(c));
        return container;
    }

}
