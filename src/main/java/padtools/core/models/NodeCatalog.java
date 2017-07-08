package padtools.core.models;

import java.util.HashMap;
import java.util.Map;

/**
 * ノードの種類を列挙した列挙体。
 */
public enum NodeCatalog {
    /* 呼び出し */
    Call("call", CallNode.class),
    /* 端子 */
    Terminal("terminal", TerminalNode.class),
    /* コメント */
    Comment("comment", CommentNode.class),
    /* 条件分岐（if） */
    If("if", IfNode.class),
    /* 繰り返し */
    Loop("loop", LoopNode.class),
    /* ノードの集合 */
    NodeList("nodes", NodeListNode.class),
    /* 処理 */
    Process("process", ProcessNode.class),
    /* 条件分岐(switch) */
    Switch("switch", SwitchNode.class)
    ;

    private static final Map<String, NodeCatalog> catalogTypeMap;
    private static final Map<Class, NodeCatalog> catalogClassMap;
    static{
        //ノード種別からマップを作成。
        catalogTypeMap = new HashMap<String, NodeCatalog>();
        catalogClassMap = new HashMap<Class, NodeCatalog>();
        for(NodeCatalog c : NodeCatalog.values()){
            catalogTypeMap.put(c.getTypeName(), c);
            catalogClassMap.put(c.getImplementClass(), c);
        }
    }

    /**
     * ノードの種別名から値を取得する。
     * 該当する種別名がない場合は null を返す。
     * @param typeName ノードの種別名
     * @return 列挙体の値
     */
    public static NodeCatalog getByTypeName(String typeName){
        return catalogTypeMap.get(typeName);
    }

    /**
     * クラスから値を取得する。
     * @param cls クラス
     * @return 列挙体の値
     */
    public static NodeCatalog getByClass(Class cls){
        return catalogClassMap.get(cls);
    }

    private final String typeName;
    private final Class implementsClass;

    NodeCatalog(String typeName, Class implementClass){
        this.typeName = typeName;
        this.implementsClass = implementClass;
    }

    /**
     * ノードの種別名を取得する。
     * @return ノードの種別名
     */
    public String getTypeName(){
        return typeName;
    }

    /**
     * 実装クラスを取得する。
     * @return 実装クラス
     */
    public Class getImplementClass(){
        return implementsClass;
    }

}
