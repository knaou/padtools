package padtools.core.formats.spd;

public class UnexpectedIOException extends ParseErrorException {
    public UnexpectedIOException(){
        super(
                "Catch unexpected IOException",
                "予期しないIOエラーが発生しました"
        );
    }
}
