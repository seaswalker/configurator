package configurator.conf;

import configurator.conf.exception.LoadException;

import java.util.*;

/**
 * {@link Source}实现，将一组{@linkplain Source}组合在一起，提供统一的访问接口.非线程安全.
 *
 * @author skywalker
 */
public class CompositeSource extends AbstractSource {

    private final List<Source> sources = new LinkedList<>();

    /**
     * {@link Source}注册.
     *
     * @param ss {@linkplain Source}数组
     */
    public void registerSource(Source... ss) {
        sources.addAll(Arrays.asList(ss));
    }

    @Override
    public void load() throws LoadException {
        for (Source source : sources) {
            source.load();
        }
    }

    @Override
    public String get(String key) {
        String result = null;
        for (Source source : sources) {
            result = source.get(key);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public String[] getStringArray(String key) {
        String[] result = null;
        for (Source source : sources) {
            result = source.getStringArray(key);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    protected Map<String, String> doFind(String prefix) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (Source source : sources) {
            result.putAll(source.find(prefix));
        }
        return result;
    }

}
