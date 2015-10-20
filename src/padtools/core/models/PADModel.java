package padtools.core.models;

import java.util.HashMap;
import java.util.Map;

/**
 * PADをモデル化したクラス。
 */
public class PADModel {
    private NodeBase topNode;
    private final Map<String, String> additionalInformation;

    /**
     * コンストラクタ。
     */
    public PADModel(){
        additionalInformation = new HashMap<String, String>();
    }

    /**
     * ノードの先頭を取得する。
     * @return ノードの先頭
     */
    public NodeBase getTopNode(){
        return  topNode;
    }

    /**
     * ノードの先頭を設定する。
     * @param topNode ノードの先頭
     */
    public void setTopNode(NodeBase topNode){
        this.topNode = topNode;
    }
}
