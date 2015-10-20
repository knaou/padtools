package padtools.core.formats.spd;

/**
 * 予期しない内部エラー。
 */
public class UnexpectedInnerExpection extends ParseErrorException {
    public UnexpectedInnerExpection(String msg){
        super(
                "Inner error: " + msg,
                "内部エラー:" + msg
        );
    }
}
