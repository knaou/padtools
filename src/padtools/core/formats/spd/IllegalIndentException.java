package padtools.core.formats.spd;

public class IllegalIndentException extends ParseErrorException {
    public IllegalIndentException(){
        super(
                "Illegal tab indent",
                "インデントの数が不正です"
        );
    }
}