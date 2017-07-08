package padtools.util;

import java.util.*;

/**
 * コマンド引数をオプション解析するkクラス
 */
public class OptionParser {
    private final Option[] options;
    private final LinkedList<String> arguments;

    public OptionParser(Option[] options){
        this.options = options;
        arguments = new LinkedList<String>();
    }

    public void parse(String[] args) throws UnknownOptionException{
        parse(args, 0, args.length);
    }

    public void parse(String[] args, int index) throws UnknownOptionException{
        parse(args, index, args.length - index);
    }

    public void parse(String[] args, int index, int count) throws UnknownOptionException{
        Map<String, Option> shortOpts = new HashMap<String, Option>();
        Map<String, Option> longOpts = new HashMap<String, Option>();

        arguments.clear();

        for(Option opt : options){
            if(opt.getShortOption() != null){
                shortOpts.put(opt.getShortOption(), opt);
            }
            if(opt.getLongOption() != null){
                longOpts.put(opt.getLongOption(), opt);
            }
        }

        boolean raw = false;
        Option opt = null;
        for(int i=index; i-index<count && i < args.length; ++i){
            String arg = args[i];
            if(opt == null || raw ){
                if( !raw && arg.equals("--") ){
                    raw = true;
                }
                else if(!raw && arg.startsWith("--")){
                    //長いオプション
                    opt = longOpts.get(arg.substring(2));
                    if(opt == null) throw new UnknownOptionException(arg);
                    if(!opt.isRequireArgument()){
                        opt.setSet(true);
                        opt = null;
                    }
                }
                else if(!raw && arg.startsWith("-")){
                    //短いオプション
                    opt = shortOpts.get(arg.substring(1));
                    if(opt == null) throw new UnknownOptionException(arg);
                    if(!opt.isRequireArgument()){
                        opt.setSet(true);
                        opt = null;
                    }
                }
                else{
                    arguments.add(arg);
                }
            }
            else{
                opt.getArguments().add(arg);
                opt.setSet(true);
                opt = null;
            }
        }
    }

    public LinkedList<String> getArguments(){
        return arguments;
    }
}
