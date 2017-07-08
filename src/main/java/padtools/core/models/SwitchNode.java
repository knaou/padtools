package padtools.core.models;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 条件分岐を表すクラス。
 * Switch文を扱う。
 */
public class SwitchNode extends NodeBase{
    private String text;
    private final LinkedHashMap<String, NodeBase> cases;

    /**
     * コンストラクタ。
     */
    public SwitchNode(){
        cases = new LinkedHashMap<String, NodeBase>();
    }

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
     * ケースのマップを取得する。
     * @return ケースのマップ
     */
    public LinkedHashMap<String, NodeBase> getCases(){
        return cases;
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
