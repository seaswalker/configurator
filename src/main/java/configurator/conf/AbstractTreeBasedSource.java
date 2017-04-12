package configurator.conf;

import java.util.Map;

/**
 * {@link Source}骨架实现，给予{@link TrieTree}.
 *
 * @author skywalker
 */
public abstract class AbstractTreeBasedSource extends AbstractPathBasedSource {

    protected final TrieTree holder = new TrieTree();

    protected AbstractTreeBasedSource(String path) {
        super(path);
    }

    @Override
    protected final Map<String, String> doFind(String prefix) {
        return holder.find(prefix);
    }

}
