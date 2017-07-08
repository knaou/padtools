package padtools;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class Setting {
    /** 保存メニューを非活性化するかどうか */
    private boolean disableSaveMenu = false;

    /** ツールバーを無効にする */
    private boolean disableToolbar = false;

    public boolean isDisableSaveMenu() {
        return disableSaveMenu;
    }
    public void setDisableSaveMenu(boolean disableSaveMenu) {
        this.disableSaveMenu = disableSaveMenu;
    }
    public boolean isDisableToolbar() {
        return disableToolbar;
    }
    public void setDisableToolbar(boolean disableToolbar) {
        this.disableToolbar = disableToolbar;
    }

    public void saveToFile(File f) throws IOException {
        if (f == null) throw new IOException("File is null");
        final BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(f));

        final XMLEncoder enc = new XMLEncoder(bos);
        enc.writeObject(this);
        enc.close();
        bos.close();
    }

    public static Setting loadFromFile(File f) throws IOException {
        final BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(f));
        final XMLDecoder dec = new XMLDecoder(bis);

        Object obj = dec.readObject();
        dec.close();
        bis.close();

        if(Setting.class.isInstance(obj)) {
            Setting s = (Setting) obj;
            return s;
        } else {
            throw new IOException("Unexpected type");
        }
    }
}
