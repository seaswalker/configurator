package configurator.conf;

/**
 * 基于单一配置文件路径的{@link Source}.
 *
 * @author skywalker
 */
public abstract class AbstractPathBasedSource extends AbstractSource {

    protected final String path;

    protected AbstractPathBasedSource(String path) {
        this.path = path;
    }

}
