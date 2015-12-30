package padtools.core.formats.spd;

/**
 * 引数が必要でない場合に引数が指定された例外。
 */
public class NotRequireArgumentException extends ParseErrorException {
    public NotRequireArgumentException(){
        super(
                "Not require argument",
                "このコマンドに引数は不要です"
        );
    }
}

