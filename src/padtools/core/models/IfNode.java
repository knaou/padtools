package padtools.core.models;

import java.util.Map;

/**
 * 条件分岐を表すクラス。
 * If文を扱う。
 */
public class IfNode extends NodeBase {
    private String text;
    private NodeBase trueNode;
    private NodeBase falseNode;

    /**
     * 表示文字列を設定する。
     * @param text 表示文字列
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * 表示文字列を取得する。
     * @return 表示文字列
     */
    public String getText(){
        return text;
    }

    /**
     * 子ノードを設定する(True)。
     * @param trueNode 子ノード
     */
    public void setTrueNode(NodeBase trueNode){
        this.trueNode = trueNode;
    }

    /**
     * 子ノードを取得する(True)。
     * @return 子ノード
     */
    public NodeBase getTrueNode(){
        return trueNode;
    }

    /**
     * 子ノードを設定する(False)。
     * @param falseNode 子ノード
     */
    public void setFalseNode(NodeBase falseNode){
        this.falseNode = falseNode;
    }

    /**
     * 子ノードを取得する(False)。
     * @return 子ノード
     */
    public NodeBase getFalseNode(){
        return falseNode;
    }

    @Override
    public void validation() throws ModelValidationException {
        if(text == null){
            throw new NullFieldException("text");
        }
    }

    @Override
    public void toMap(Map<String, String> map) {
        map.put("text", text);
    }

    @Override
    public void fromMap(Map<String, String> map) {
        text = map.get("text");
    }
}
