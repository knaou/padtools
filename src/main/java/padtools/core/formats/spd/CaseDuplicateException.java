package padtools.core.formats.spd;

/**
 * Case名が重複している場合の例外。
 */
public class CaseDuplicateException extends ParseErrorException {
    public CaseDuplicateException(){
        super(
                "The case is already exist.",
                "既に同名のCaseが存在します"
        );
    }
}