package padtools.core.formats.spd;

public class UnexpectedElseException extends ParseErrorException {
    public UnexpectedElseException(){
        super(
                "Else must be after if",
                "不適切なelseです"
        );
    }
}
