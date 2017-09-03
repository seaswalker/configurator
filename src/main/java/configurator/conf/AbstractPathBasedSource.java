package configurator.conf;

import configurator.util.Util;

/**
 * 基于单一配置文件路径的{@link Source}.
 *
 * @author skywalker
 */
abstract class AbstractPathBasedSource extends AbstractSource {

    protected final String path;

    /**
     * classpath路径支持.
     */
    private static final String classPathPrefix = "classpath:";

    protected AbstractPathBasedSource(String path) {
        if (Util.isEmpty(path)) {
            throw new IllegalArgumentException("Param 'path' can not be null or empty.");
        }
        if (path.startsWith(classPathPrefix)) {
            path = path.substring(classPathPrefix.length());
            this.path = getClass().getClassLoader().getResource(path).getPath();
        } else {
            this.path = path;
        }
    }

}
