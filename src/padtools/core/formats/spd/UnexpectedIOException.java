package padtools.core.formats.spd;

/**
 * 予期しないIO例外。
 * 予期しないIO例外。
 */
public class UnexpectedIOException extends ParseErrorException {
    public UnexpectedIOException(){
        super(
                "Catch unexpected IOException",
                "予期しないIOエラーが発生しました"
        );
    }
}
