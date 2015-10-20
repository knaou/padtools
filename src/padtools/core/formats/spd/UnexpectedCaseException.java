package padtools.core.formats.spd;

public class UnexpectedCaseException extends ParseErrorException {
    public UnexpectedCaseException(){
        super(
                "Case must be after switch or case",
                "不適切なcaseが現れました"
        );
    }
}
