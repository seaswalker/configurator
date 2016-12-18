package inject;

import bean.Component;
import bean.Value;
import conf.ConfSource;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 注射器.
 *
 * @author skywalker
 */
public class Injecter {

    private ConfSource source;
    //扫描的包
    private String basePackage;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 设置配置来源{@link ConfSource}.
     *
     * @param source {@linkplain ConfSource}
     * @return {@link Injecter}
     */
    public Injecter source(ConfSource source) {
        this.source = source;
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
     * 注入.
     */
    public void inject() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().
            setUrls(ClasspathHelper.forPackage(basePackage)));
        logger.info("Start scan package: {}.", basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        logger.info("Scan package {} finished.", basePackage);
        System.out.println(classes);
    }

    /**
     *
     * @param fields
     */
    private void injectToFields(Set<Field> fields) {
        logger.info("Start inject fields...");
        fields.stream().forEach(field -> {
            Value inject = field.getAnnotation(Value.class);
            if (inject != null) {
                String key = inject.key();
                String attr = inject.attr();
                field.setAccessible(true);
                if (key.equals("*")) {

                } else {
                    Class type = field.getType();
                    if (type == String.class) {
                    }
                }
            }
        });
    }

}
