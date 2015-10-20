package padtools.core.models;

import java.util.Map;

/**
 * 端子を表すクラス。
 */
public class TerminalNode extends NodeBase {
    private String text;

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
