package padtools;

import padtools.converter.Converter;
import padtools.editor.Editor;
import padtools.util.Option;
import padtools.util.OptionParser;
import padtools.util.PathUtil;
import padtools.util.UnknownOptionException;

import java.io.File;
import java.io.IOException;

/**
 * エントリポイントクラス
 */
public class Main {
    private static Setting setting = null;
    public static Setting getSetting () {
        if(setting == null) throw new RuntimeException("Setting is not set");
        return setting;
    }
    /**
     * Settingを保存する
     */
    public static void saveSetting(){
        File setting_file = new File(PathUtil.getBasePath(), "settings.xml");
        try{
            setting.saveToFile(setting_file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * エントリポイント
     * @param args 引数
     */
    public static void main(String[] args) throws IOException{
        //TODO: IO例外を適切に扱ってユーザに分かる形で出力する

        //設定ファイルを読み込む
        Setting setting;
        File setting_file = new File(PathUtil.getBasePath(), "settings.xml");
        if(setting_file.exists()){
            setting = Setting.loadFromFile(setting_file);
        } else {
            setting = new Setting();
            setting.setDisableToolbar(true);
            setting.setDisableSaveMenu(true);
            setting.saveToFile(setting_file);
        }
        Main.setting = setting;

        //オプション定義
        final Option optHelp = new Option("h", "help", false);
        final Option optOut = new Option("o", "output", true);
        final Option optScale = new Option("s", "scale", true);

        final OptionParser optParser = new OptionParser(new Option[]{
                optHelp, optOut, optScale});

        try{
            optParser.parse(args, 1);
        }
        catch(UnknownOptionException ex){
            System.err.println("Unknown option: " + ex.getOption());
            System.exit(1);
        }

        if(optHelp.isSet()){
            System.err.println("Arguments: [-o result_file] [-s scale] [-h] [spd_file]");
            System.err.println("  -o result_file: Save to result_file.");
            System.err.println("        -s scale: Image scale(available when result_file is set).");
            System.err.println("              -h: Show this help.");
            System.err.println("        spd_file: Open lpd file.");
            System.exit(1);
        }

        File file_in;
        if( optParser.getArguments().isEmpty() ) {
            file_in = null;
        }else {
            file_in = new File(optParser.getArguments().getFirst());
        }

        File file_out;
        if( optOut.getArguments().isEmpty()) {
            file_out = null;
        } else {
            file_out = new File( optOut.getArguments().getLast());
        }

        Double scale;
        if(optScale.getArguments().isEmpty()){
            scale = null;
        } else {
            try {
                scale = Double.parseDouble(optScale.getArguments().getLast());
            } catch( NumberFormatException ex) {
                System.err.println("不正なフォーマットのscale値が指定されました。");
                System.exit(1);
                return;
            }
        }

        if( file_out == null ) {
            // file_out が指定されていない場合はエディタを起動
            Editor.openEditor(file_in);
        } else {
            // file_out が指定された場合はエディタを起動せず、変換のみを行う
            Converter.convert(file_in, file_out, scale);
        }
    }

}
