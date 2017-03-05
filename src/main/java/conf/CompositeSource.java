package conf;

import conf.exception.LoadException;

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
        for (int i = 0, l = sources.size(); i < l; i++) {
            sources.get(i).load();
        }
    }

    @Override
    public String get(String key) {
        String result = null;
        for (int i = 0, l = sources.size(); i < l; i++) {
            result = sources.get(i).get(key);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public String[] getStringArray(String key) {
        String[] result = null;
        for (int i = 0, l = sources.size(); i < l; i++) {
            result = sources.get(i).getStringArray(key);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getAll() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (int i = 0, l = sources.size(); i < l; i++) {
            result.putAll(sources.get(i).getAll());
        }
        return result;
    }

}
