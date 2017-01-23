package conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Source}骨架实现，提供基于配置文件路径的构造器和日志.
 *
 * @author skywalker
 */
public abstract class AbstractSource implements Source {

    protected final String path;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected AbstractSource(String path) {
        this.path = path;
    }

}
