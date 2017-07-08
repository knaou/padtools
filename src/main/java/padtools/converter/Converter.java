package padtools.converter;

import padtools.Setting;
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
public class Converter {
    public static void  convert(File file_in, File file_out, Double scale){
        InputStream in;
        if(file_in == null){
            in = System.in;
        }
        else{
            try{
                in = new FileInputStream(file_in);
            }
            catch(FileNotFoundException ex){
                System.err.println(String.format("File is not found: %s", file_in));
                System.exit(1);
                return;
            }
        }

        PrintStream out;
        if(file_out == null){
            out = System.out;
        }
        else{
            try{
                out = new PrintStream(file_out);
            }
            catch(FileNotFoundException ex){
                System.err.println(String.format("File is not found: %s", file_out));
                System.exit(1);
                return;
            }
        }

        if(scale == null){
            scale = 1.0;
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
