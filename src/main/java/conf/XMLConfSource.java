package conf;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * XML配置来源.
 *
 * @author skywalker
 */
public class XMLConfSource extends AbstractConfSource {

    public XMLConfSource(String path) {
        super(path);
    }

    /**
     * 解析指定的配置文件.
     */
    @Override
    public void load() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(path);
            Element root = document.getDocumentElement();
            resolve(root);
        } catch (Exception e) {
            logger.error("{} parse failed.", path, e);
        }
    }

    /**
     * 解析.
     *
     * @param root {@link Element} 根节点
     */
    private void resolve(Element root) {
        NodeList list = root.getChildNodes();
        for (int i = 0, l = list.getLength(); i < l; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                doResolve((Element) node, newKey(node, ""));
            }
        }
    }

    /**
     * 递归解析类型为{@link Node}.ELEMENT_NODE的节点，即{@link Element}，其逻辑为:
     * <br>
     * 1) 如果此节点为叶子节点，那么保存其值及属性.
     * <br>
     * 2) 如果不是叶子节点，那么递归调用此方法.
     */
    private void doResolve(Element element, String key) {
        NodeList list = element.getChildNodes();
        boolean isLeaf = true;
        for (int i = 0, l = list.getLength(); i < l; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                isLeaf = false;
                doResolve((Element) node, newKey(node, key));
            }
        }
        if (isLeaf) {
            saveLeaf(key, element);
        } else {
            saveUnLeafAttributes(element, key);
        }
    }

    /**
     * 保存非叶子节点的属性.
     *
     * @param key 未经过处理的(带有.)的key
     */
    private void saveUnLeafAttributes(Node node, String key) {
        key = key.substring(0, key.length() - 1);
        NamedNodeMap attributes = node.getAttributes();
        int length = attributes.getLength();
        if (length > 0) {
            String[] metaDataKeys = new String[length], metaDataValues = new String[length];
            for (int i = 0; i < length; i++) {
                Node attr = attributes.item(i);
                metaDataKeys[i] = attr.getNodeName();
                metaDataValues[i] = attr.getNodeValue();
            }
            holder.addMetaDatas(key, metaDataKeys, metaDataValues);
        }
    }

    /**
     * 保存叶子节点的值及属性.
     *
     * @param key 未经过处理的(带有.)的key
     */
    private void saveLeaf(String key, Element element) {
        key = key.substring(0, key.length() - 1);
        String value = element.getTextContent().trim();
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        if (length > 0) {
            String[] metaDataKeys = new String[length], metaDataValues = new String[length];
            for (int i = 0; i < length; i++) {
                Node attr = attributes.item(i);
                metaDataKeys[i] = attr.getNodeName();
                metaDataValues[i] = attr.getNodeValue();
            }
            holder.add(key, value, metaDataKeys, metaDataValues);
        } else {
            holder.addValue(key, value);
        }
    }

    /**
     * 生成新的键值.
     */
    private String newKey(Node node, String base) {
        return (base + node.getNodeName() + ".");
    }

    /**
     * 获取配置中String数组形式的值.此方法在同一级下的同名标签使用.
     */
    public String[] getStringArray(String key) {
        return holder.get(key).split(holder.getMultiSeparator());
    }

    /**
     * 获取属性值.
     */
    public String getAttribute(String key, String attr) {
        return holder.getMetaData(key, attr);
    }

    /**
     * 获取int形式的属性值.
     */
    public int getAttributeAsInt(String key, String attr) {
        return Integer.parseInt(getAttribute(key, attr));
    }

    /**
     * 获取long形式的属性值.
     */
    public long getAttributeAsLong(String key, String attr) {
        return Long.parseLong(getAttribute(key, attr));
    }

    /**
     * 获取boolean形式的属性值.
     */
    public boolean getAttributeAsBoolean(String key, String attr) {
        return Boolean.parseBoolean(getAttribute(key, attr));
    }

    /**
     * 获取doule形式的值.
     */
    public double getAttributeAsDouble(String key, String value) {
        return Double.parseDouble(getAttribute(key, value));
    }

    /**
     * 获取String数组形式的值.
     */
    public String[] getAttributeAsStringArray(String key, String attr, String separator) {
        return getAttribute(key, attr).split(separator);
    }

}
