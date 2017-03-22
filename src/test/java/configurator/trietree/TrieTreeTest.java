package configurator.trietree;

import configurator.conf.TrieTree;
import org.junit.Test;

/**
 * 测试{@link configurator.conf.TrieTree}.
 *
 * @author skywalker
 */
public class TrieTreeTest {

    private final TrieTree tree = new TrieTree();

    /**
     * 测试添加键值对.
     */
    @Test
    public void addValue() {
        tree.addValue("china.name", "China");
        System.out.println(tree.get("china.name"));
    }

    /**
     * 测试添加元信息.
     */
    @Test
    public void addMetaData() {
        tree.addMetaData("china.name", "continent", "Asia");
        System.out.println(tree.getMetaData("china.name", "continent"));
    }

    /**
     * 同一个key下多个值.
     */
    @Test
    public void addMultiValue() {
        tree.addValue("china.name", "China");
        tree.addValue("china.name", "TG");
        System.out.println(tree.get("china.name"));
    }

    /**
     * 前缀搜索
     */
    @Test
    public void prefix() {
        tree.addValue("china.name", "China");
        tree.addValue("china.leader", "习近平");
        tree.addValue("us.leader", "Obama");
        //tree.addMetaData("us.leader", "color", "black");
        tree.addMetaData("china.leader", "color", "yellow");
        System.out.println(tree.find("china"));
        System.out.println(tree.find("us"));
    }

    /**
     * 获取所有.
     */
    @Test
    public void all() {
        tree.addValue("china.name", "China");
        tree.addValue("china.leader", "习近平");
        tree.addValue("us.leader", "Obama");
        tree.addMetaData("us.leader", "color", "black");
        tree.addMetaData("china.leader", "color", "yellow");
        System.out.println(tree.getAll());
    }

    /**
     * 测试当key为null的时候.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullKey() {
        tree.addValue(null, "");
    }

    /**
     * 测试当key为空串的时候.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyKey() {
        tree.addValue("", "");
    }

    /**
     * 测试当元信息key为null的时候.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullMetaDataKey() {
        tree.addMetaData("key", null, "");
    }

}
