package padtools.core.models;

/**
 * モデルの正当性評価に失敗した場合に生じる例外。
 */
public class ModelValidationException extends Exception {
    /**
     * コンストラクタ。
     */
    public ModelValidationException(){
    }

    /**
     * コンストラクタ。
     * @param msg エラーメッセージ
     */
    public ModelValidationException(String msg){
        super(msg);
    }
}
