package conf;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置存放载体.
 *
 * @author skywalker
 */
public final class TrieTree {

    private final Node head = new Node("HEAD");

    //多值分隔符
    private static final String multiSeparator = ",";
    private static final String separator = "\\.";

    /**
     * 添加一个键值对.
     *
     * @param key   键值，示例: sample.A
     * @param value String形式的值，不支持null
     */
    public void addValue(String key, String value) {
        add(key, value, null, null);
    }

    /**
     * 添加一个元信息.
     */
    public void addMetaData(String key, String metaDataKey, String metaDataValue) {
        add(key, null, new String[]{metaDataKey}, new String[]{metaDataValue});
    }

    /**
     * 添加一组元信息.
     */
    public void addMetaDatas(String key, String[] metaDataKeys, String[] metaDataValues) {
        add(key, null, metaDataKeys, metaDataValues);
    }

    /**
     * 添加键值对和元信息.
     *
     * @param key            元素的键值
     * @param value          值，不支持null
     * @param metaDataKeys   元信息的键值数组
     * @param metaDataValues 元信息值数组
     */
    public void add(String key, String value, String[] metaDataKeys, String[] metaDataValues) {
        checkKey(key);
        String[] parts = key.split(separator);
        Node parent = head, node = null;
        for (int i = 0, l = parts.length; i < l; i++) {
            String part = parts[i];
            node = findChild(parent, part);
            if (node == null) {
                node = new Node();
                parent.children.put(part, node);
            }
            parent = node;
        }
        if (value != null) {
            //设置value
            if (node.value != null) {
                node.value += (multiSeparator + value);
            } else {
                node.value = value;
            }
        }
        //设置元信息
        if (metaDataKeys != null && metaDataValues != null) {
            for (int i = 0, l = metaDataKeys.length; i < l; i++) {
                String metaDataKey = metaDataKeys[i];
                checkKey(metaDataKey);
                node.metaData.put(metaDataKey, metaDataValues[i]);
            }
        }
    }

    /**
     * 取出键为key的值.仅支持叶子节点.
     *
     * @return null, 如果没有找到
     */
    public String get(String key) {
        checkKey(key);
        Node node = seekTo(key);
        if (!isLeaf(node)) {
            throw new IllegalStateException("Get() method supports leaf node only.");
        }
        return node.value;
    }

    /**
     * 获取元信息.
     */
    public String getMetaData(String key, String metaDataKey) {
        checkKey(key);
        checkKey(metaDataKey);
        Node node = seekTo(key);
        String result = null;
        if (node != null) {
            result = node.metaData.get(metaDataKey);
        }
        return result;
    }

    /**
     * 前缀搜索.
     */
    public Map<String, String> find(String prefix) {
        Node node = seekTo(prefix);
        if (node == null) {
            return new LinkedHashMap<>(0);
        }
        Map<String, String> result = new LinkedHashMap<>();
        prefix += ".";
        collectAsMap(prefix, node, result);
        return result;
    }

    /**
     * 得到当前树中的所有节点.
     */
    public Map<String, String> getAll() {
        Map<String, String> result = new LinkedHashMap<>();
        collectAsMap("", head, result);
        return result;
    }

    /**
     * 结果收集.
     */
    private void collectAsMap(String prefix, Node node, Map<String, String> result) {
        if (isLeaf(node)) {
            String key = prefix.substring(0, prefix.length() - 1);
            String value = node.value;
            if (hasMeta(node)) {
                value += node.metaData.toString();
            }
            result.put(key, value);
        } else {
            if (node != head && hasMeta(node)) {
                String key = prefix.substring(0, prefix.length() - 1);
                String value = node.metaData.toString();
                result.put(key, value);
            }
            node.children.forEach((key, value) -> {
                String newPrefix = (prefix + key + ".");
                collectAsMap(newPrefix, value, result);
            });
        }
    }

    /**
     * 判断给定的{@link Node}是否有元信息.
     */
    private boolean hasMeta(Node node) {
        return (node.metaData.size() > 0);
    }

    /**
     * 向下对key逐部分进行匹配.
     */
    private Node seekTo(String key) {
        String[] parts = key.split(separator);
        Node node = head;
        for (int i = 0, l = parts.length; i < l; i++) {
            node = findChild(node, parts[i]);
            if (node == null) {
                return null;
            }
        }
        return node;
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
     * 寻找相匹配的子节点.
     */
    private Node findChild(Node node, String part) {
        Map<String, Node> children = node.children;
        return children.get(part);
    }

    /**
     * 判断给定的节点是否是叶子节点.
     *
     * @param node {@link Node}
     */
    private boolean isLeaf(Node node) {
        return (node.children.size() == 0);
    }

    public String getMultiSeparator() {
        return multiSeparator;
    }

    /**
     * 节点.
     */
    private class Node {

        String value;

        //子节点
        final Map<String, Node> children = new LinkedHashMap<>(10);
        //元信息
        final Map<String, String> metaData = new LinkedHashMap<>();

        public Node(String value) {
            this.value = value;
        }

        public Node() {
        }

    }

}
