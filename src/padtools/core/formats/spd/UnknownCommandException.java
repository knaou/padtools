package padtools.core.formats.spd;

public class UnknownCommandException extends ParseErrorException {
    public UnknownCommandException(){
        super(
                "Unknown command",
                "未知のコマンドです"
        );
    }
}
