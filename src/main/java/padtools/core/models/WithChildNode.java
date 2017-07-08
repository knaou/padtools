package padtools.core.models;

/**
 * 単独の子を持つノードの基底クラス。
 */
public abstract class WithChildNode extends NodeBase{
    private NodeBase childNode;

    /**
     * 子ノードを設定する。
     * @param childNode 子ノード
     */
    public void setChildNode(NodeBase childNode){
        this.childNode = childNode;
    }

    /**
     * 子ノードを取得する。
     * @return 子ノード
     */
    public NodeBase getChildNode(){
        return childNode;
    }
}
