package padtools.core.formats.spd;

/**
 * 引数が必要にも関わらず引数が指定されていない例外。
 */
public class RequireArgumentException extends ParseErrorException {
    public RequireArgumentException(){
        super(
                "Require argument",
                "このコマンドは引数が必要です"
        );
    }
}
