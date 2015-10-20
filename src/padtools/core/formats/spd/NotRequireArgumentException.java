package padtools.core.formats.spd;

public class NotRequireArgumentException extends ParseErrorException {
    public NotRequireArgumentException(){
        super(
                "Not require argument",
                "このコマンドに引数は不要です"
        );
    }
}

