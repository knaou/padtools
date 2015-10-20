package padtools.core.models;

import java.util.Map;

/**
 * 繰り返しを表すクラス。
 */
public class LoopNode extends WithChildNode {
    private String text;
    private boolean isWhile;

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
     * 前判定ループかどうかを設定する。
     * @param isWhile 前判定ループかどうか
     */
    public void setWhile(boolean isWhile){
        this.isWhile = isWhile;
    }

    /**
     * 前判定ループかどうかを取得する。
     * @return 前判定ループかどうか。
     */
    public boolean isWhile(){
        return isWhile;
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
        map.put("while", Boolean.toString(isWhile));
    }

    @Override
    public void fromMap(Map<String, String> map) {
        text = map.get("text");
        isWhile = Boolean.parseBoolean(map.get("while"));
    }
}
