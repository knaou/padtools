package padtools.core.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ノードの集合を表すクラス。
 */
public class NodeListNode extends NodeBase {
    private final List<NodeBase> children;

    /**
     * コンストラクタ。
     */
    public NodeListNode(){
        children = new LinkedList<NodeBase>();
    }

    /**
     * 子ノードのリストを取得する。
     * @return 子ノードのリスト
     */
    public List<NodeBase> getChildren(){
        return children;
    }

    @Override
    public void validation() throws ModelValidationException {
    }

    @Override
    public void toMap(Map<String, String> map) {
    }

    @Override
    public void fromMap(Map<String, String> map) {
    }
}
