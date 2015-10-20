package padtools.util;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: monaou
 * Date: 12/07/08
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class Option {
    private final String shortOption;
    private final String longOption;
    private final boolean requireArgument;
    private boolean set;
    private final LinkedList<String> arguments;

    public Option(String shortOption, String longOption, boolean requireArgument){
        this.shortOption = shortOption;
        this.longOption = longOption;
        this.requireArgument = requireArgument;
        set = false;
        arguments = new LinkedList<String>();
    }

    public String getShortOption(){
        return shortOption;
    }

    public String getLongOption(){
        return longOption;
    }

    public boolean isRequireArgument(){
        return requireArgument;
    }

    public boolean isSet(){
        return set;
    }

    public void setSet(boolean set){
        this.set = set;
    }

    public LinkedList<String> getArguments(){
        return arguments;
    }
}
