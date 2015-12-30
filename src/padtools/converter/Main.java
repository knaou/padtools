package padtools.converter;

import padtools.core.formats.spd.ParseErrorException;
import padtools.core.formats.spd.ParseErrorReceiver;
import padtools.core.formats.spd.SPDParser;
import padtools.core.models.PADModel;
import padtools.core.view.Model2View;
import padtools.core.view.View2Image;
import padtools.util.Option;
import padtools.util.OptionParser;
import padtools.util.UnknownOptionException;

import javax.imageio.ImageIO;
import java.io.*;

/**
 * コンバーターのメインクラス
 */
public class Main {

    /**
     * エントリポイント
     * @param args 引数
     */
    public static void main(String[] args){

        final Option optHelp = new Option("h", "help", false);
        final Option optIn= new Option("i", "in", true);
        final Option optOut = new Option("o", "output", true);
        final Option optScale = new Option("s", "scale", true);

        final OptionParser optParser = new OptionParser(new Option[]{optHelp, optIn, optOut});
        try{
            optParser.parse(args, 1);
        }
        catch(UnknownOptionException ex){
            System.err.println("Unknown option: " + ex.getOption());
            System.exit(1);
        }

        if(optHelp.isSet()){
            System.err.println("$ " + args[0] +  " [-s scale] [-i lpd_file] [-o result_file] [-h]");
            System.err.println("        -s:    Image scale.");
            System.err.println("  lpd_file:    Open lpd file.");
            System.err.println("  result_file: Save to result_file.");
            System.err.println("        -h:    Show this help.");
            System.exit(1);
        }

        InputStream in;
        if(optIn.isSet()){
            try{
                in = new FileInputStream(optIn.getArguments().getLast());
            }
            catch(FileNotFoundException ex){
                System.err.println(String.format("File is not found: %s", optIn.getArguments().getLast()));
                System.exit(1);
                return;
            }
        }
        else{
            in = System.in;
        }

        PrintStream out;
        if(optOut.isSet()){
            try{
                out = new PrintStream(optOut.getArguments().getLast());
            }
            catch(FileNotFoundException ex){
                System.err.println(String.format("File is not found: %s", optIn.getArguments().getLast()));
                System.exit(1);
                return;
            }
        }
        else{
            out = System.out;
        }

        //入力する
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String buf;
        try{
            while((buf = br.readLine()) != null ){
                pw.println(buf);
            }
        }
        catch (IOException ex){
        }

        double scale = 1.0;
        if(optScale.isSet()){
            try{
                scale = Double.parseDouble(optScale.getArguments().getLast());
            }
            catch(NumberFormatException ex){
                System.err.println(ex.getLocalizedMessage());
                System.exit(1);
            }
        }

        PADModel pad = SPDParser.parse(sw.toString(), new ParseErrorReceiver() {
            @Override
            public boolean receiveParseError(String lineStr, int lineNo, ParseErrorException err) {
                System.err.println(String.format("%d: %s", lineNo, err.getUserMessage()));
                return true;
            }
        });

        Model2View m2v = new Model2View();
        try{
            ImageIO.write(View2Image.toImage(m2v.toView(pad), scale), "png", out);
        }
        catch(IOException ex){
            System.err.println(ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}
