package padtools.util;

/**
 * Created with IntelliJ IDEA.
 * User: monaou
 * Date: 12/07/08
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
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
