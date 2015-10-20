package padtools.util;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: monaou
 * Date: 12/07/18
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class PathUtil {
    public static String extConvert(String base, String ext){
        String ret;
        File f = new File(base);
        String fn = f.getName();
        int i = fn.lastIndexOf(".");
        if(i>0){
            ret = f.getPath().toString().substring(0, f.getPath().toString().length() - (fn.length() - i));
        }
        else{
            ret = base;
        }
        ret += "." + ext;

        return ret;
    }
}
