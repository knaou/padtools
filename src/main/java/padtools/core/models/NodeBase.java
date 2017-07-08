package padtools.core.models;

import padtools.core.models.ModelValidationException;

import java.util.Map;

/**
 * PADの各ノードを表す基底クラス。
 */
public abstract class NodeBase {

    /**
     * モデルの正当性を評価する。
     * @throws ModelValidationException 正当性評価に失敗した場合に発生
     */
    public abstract void validation() throws ModelValidationException;

    /**
     * String-String マップに情報を移す。
     * @param map String-String マップ
     */
    public abstract void toMap(Map<String, String> map);

    /**
     * String-String マップから情報を設定する。
     * 余分なキーは無視される。エントリが足りない場合は、足りないエントリの情報は更新されない。
     * @param map String-String マップ
     */
    public abstract void fromMap(Map<String, String> map);
}
