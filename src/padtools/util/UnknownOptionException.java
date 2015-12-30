package padtools.util;

/**
 * 不明なオプションが指定された場合に発生する例外
 */
public class UnknownOptionException extends Exception {
    private final String option;

    public UnknownOptionException(String option){
        super(String.format("Unknown option: %s", option));
        this.option = option;
    }

    public String getOption(){
        return option;
    }
}
