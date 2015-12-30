package padtools.core.formats.spd;

/**
 * 不明なコマンド例外。
 */
public class UnknownCommandException extends ParseErrorException {
    public UnknownCommandException(){
        super(
                "Unknown command",
                "未知のコマンドです"
        );
    }
}
