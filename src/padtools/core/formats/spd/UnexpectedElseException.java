package padtools.core.formats.spd;

/**
 * 不適切な位置にElseが現れた例外。
 */
public class UnexpectedElseException extends ParseErrorException {
    public UnexpectedElseException(){
        super(
                "Else must be after if",
                "不適切なelseです"
        );
    }
}
