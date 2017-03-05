package conf;

import conf.exception.LoadException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * XML配置来源.
 *
 * @author skywalker
 */
public class XmlSource extends AbstractTreeBasedSource {

    /** 属性key分隔符 **/
    private static final String metadataSeparator = "#";

    public XmlSource(String path) {
        super(path);
    }

    /**
     * 解析指定的配置文件.
     */
    @Override
    public void load() throws LoadException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(path);
            Element root = document.getDocumentElement();
            resolve(root);
        } catch (Exception e) {
            throw new LoadException(e);
        }
    }

    /**
     * 解析.
     *
     * @param root {@link Element} 根节点
     */
    private void resolve(Element root) {
        NodeList list = root.getChildNodes();
        String base = root.getNodeName() + ".";
        for (int i = 0, l = list.getLength(); i < l; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                doResolve((Element) node, newKey(node, base));
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

    @Override
    public String get(String key) {
        String[] parts = key.split(metadataSeparator);
        int length = parts.length;
        String result;
        if (length > 2) {
            throw new IllegalStateException("Malformed key: " + key + ", example: key#attr.");
        }
        if (length == 1) {
            result = holder.get(key);
        } else {
            result = holder.getMetaData(parts[0], parts[1]);
        }
        return result;
    }

    /**
     * 获取配置中String数组形式的值.此方法在同一级下的同名标签使用.
     */
    @Override
    public String[] getStringArray(String key) {
        return holder.get(key).split(holder.getMultiSeparator());
    }

}
