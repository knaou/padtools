package padtools.util;

import java.util.LinkedList;

/**
 * コマンドの引数を扱うクラス
 */
public class Option {
    private final String shortOption;
    private final String longOption;
    private final boolean requireArgument;
    private boolean set;
    private final LinkedList<String> arguments;

    /**
     * コンストラクタ。
     * 例） Option("h", "help", false)
     *
     * @param shortOption 短いオプション
     * @param longOption 長いオプション
     * @param requireArgument 引数をとるかどうか
     */
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
