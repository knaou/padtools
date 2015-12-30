package padtools.core.formats.spd;

/**
 * 不適切な位置にCaseが現れた例外。
 */
public class UnexpectedCaseException extends ParseErrorException {
    public UnexpectedCaseException(){
        super(
                "Case must be after switch or case",
                "不適切なcaseが現れました"
        );
    }
}
