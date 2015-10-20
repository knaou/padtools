package padtools.core.formats.spd;

public class CaseDuplicateException extends ParseErrorException {
    public CaseDuplicateException(){
        super(
                "The case is already exist.",
                "既に同名のCaseが存在します"
        );
    }
}