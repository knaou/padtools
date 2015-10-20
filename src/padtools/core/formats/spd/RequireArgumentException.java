package padtools.core.formats.spd;

public class RequireArgumentException extends ParseErrorException {
    public RequireArgumentException(){
        super(
                "Require argument",
                "このコマンドは引数が必要です"
        );
    }
}
