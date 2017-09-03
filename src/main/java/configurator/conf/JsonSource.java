package configurator.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import configurator.conf.exception.LoadException;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * json配置文件来源.
 * <p>不支持object数组格式.</p>
 *
 * @author skywalker
 */
public class JsonSource extends AbstractPathBasedSource {

    private JSONObject json;

    public JsonSource(String path) {
        super(path);
    }

    @Override
    public void load() throws LoadException {
        try {
            String data = readAsString();
            Object o = JSON.parse(data);
            if (!(o instanceof JSONObject)) {
                throw new IllegalStateException("We support json object only.");
            }
            json = JSONObject.class.cast(o);
        } catch (Exception e) {
            throw new LoadException(e);
        }

    }

    /**
     * 将json配置文件读取为字符串.
     */
    private String readAsString() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(super.path)));
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return String.join("", lines);
    }

    @Override
    public String get(String key) {
        Object value = doGet(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    private Object doGet(String key) {
        String[] parts = resolveKey(key);
        String[] parents = extractParents(parts);
        JSONObject node = seekTo(parents);
        Object result = null;
        if (node != null) {
            result = node.get(parts[parents.length]);
        }
        return result;
    }

    /**
     * 提取父节点数组，比如有key: plant.apple.name，应该得到:
     * [plant, apple].
     */
    private String[] extractParents(String[] parts) {
        String[] parents = new String[parts.length - 1];
        System.arraycopy(parts, 0, parents, 0, parts.length - 1);
        return parents;
    }

    private String[] resolveKey(String key) {
        checkKey(key);
        return key.split("\\.");
    }

    /**
     * 检查给定的key是否为null,如果是，抛出{@link IllegalArgumentException}.
     */
    private void checkKey(String key) {
        if (key == null || key.trim().equals("")) {
            throw new IllegalArgumentException("Given key can't be null or empty.");
        }
    }

    /**
     * 定位到叶子key的父节点.
     *
     * @return {@link JSONObject}
     * @throws IllegalStateException 如果指定的节点不存在或者其类型不是{@link JSONObject}
     */
    private JSONObject seekTo(String[] parts) {
        JSONObject node = json;
        for (int i = 0, l = parts.length; i < l; i++) {
            Object o = node.get(parts[i]);
            if (o == null) {
                return null;
            }
            if (!(o instanceof JSONObject)) {
                throw new IllegalStateException("Key: " + concatParts(parts, i) + " can't be leaf or array node.");
            }
            node = JSONObject.class.cast(o);
        }
        return node;
    }

    /**
     * 将部分key还原为字符串.
     * <p>示例: </p>
     * <p>给定([cn, sd], 1), 得到cn.sd.</p>
     *
     * @param util 截止下标(包含)
     */
    private String concatParts(String[] parts, int util) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= util; i++) {
            sb.append(parts[i]).append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public int getInt(String key) {
        return (int) doGet(key);
    }

    @Override
    public long getLong(String key) {
        return (long) doGet(key);
    }

    @Override
    public double getDouble(String key) {
        return ((BigDecimal) doGet(key)).doubleValue();
    }

    @Override
    public boolean getBoolean(String key) {
        return (boolean) doGet(key);
    }

    @Override
    public String[] getStringArray(String key, String separator) {
        String str = (String) doGet(key);
        return str.split(separator);
    }

    @Override
    public String[] getStringArray(String key) {
        Object o = doGet(key);
        if (!(o instanceof JSONArray)) {
            throw new IllegalStateException("Given key: " + key + " must be a json array.");
        }
        JSONArray array = JSONArray.class.cast(o);
        if (!isPrimitiveArray(array)) {
            throw new IllegalStateException("Given key: " + key + " must be a primitive array.");
        }
        return array.stream().map(Object::toString)
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    @Override
    protected Map<String, String> doFind(String prefix) {
        Map<String, String> result = new LinkedHashMap<>();
        JSONObject parent;
        if (prefix.equals("")) {
            parent = json;
        } else {
            parent = seekTo(prefix.split("\\."));
            prefix += ".";
        }
        if (parent != null) {
            collect(result, prefix, parent);
        }
        return result;
    }

    /**
     * 递归收集所有节点.
     *
     * @throws IllegalStateException 如果含有非基本类型数组
     */
    private void collect(Map<String, String> result, String prefix, JSONObject parent) {
        Set<String> keys = parent.keySet();
        keys.forEach(key -> {
            Object o = parent.get(key);
            String newKey = newKey(prefix, key);
            if (o instanceof JSONObject) {
                collect(result, newKey + ".", (JSONObject) o);
            } else if (o instanceof JSONArray) {
                JSONArray array = JSONArray.class.cast(o);
                if (array.size() == 0) {
                    result.put(newKey, "");
                } else if (!isPrimitiveArray(array)) {
                    throw new IllegalStateException("We support primitive array only, key: " + newKey + ".");
                } else {
                    result.put(newKey, array.toString());
                }
            } else {
                result.put(newKey, o.toString());
            }
        });
    }

    /**
     * 生成新的键值.
     */
    private String newKey(String prefix, String key) {
        return (prefix + key);
    }

    /**
     * 是否是基本类型的json数组.
     *
     * @param array {@link JSONArray}
     * @return true，如果是
     */
    private boolean isPrimitiveArray(JSONArray array) {
        for (Object item : array) {
            if (item instanceof JSONObject || item instanceof JSONArray) {
                return false;
            }
        }
        return true;
    }

}
