package padtools.core.formats.spd;

/**
 * 例外を受け取るインターフェイス
 */
public interface ParseErrorReceiver {

    /**
     * パースエラーを受け取る関数
     * @param lineStr 該当行の文字列
     * @param lineNo 行数
     * @param reason 理由
     * @return 処理を継続するかどうか
     */
    boolean receiveParseError(String lineStr, int lineNo, ParseErrorException err);
}
