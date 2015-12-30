package padtools.core.formats.spd;

/**
 * インデントが不正な場合の例外
 */
public class IllegalIndentException extends ParseErrorException {
    public IllegalIndentException(){
        super(
                "Illegal tab indent",
                "インデントの数が不正です"
        );
    }
}