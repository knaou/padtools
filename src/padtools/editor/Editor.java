package padtools.editor;

import padtools.util.Option;
import padtools.util.OptionParser;
import padtools.util.UnknownOptionException;

import javax.swing.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

public class Editor {
    public static void openEditor(final File file) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(UnsupportedLookAndFeelException ex){}
        catch(ClassNotFoundException ex){}
        catch(InstantiationException ex){}
        catch(IllegalAccessException ex){}

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                MainFrame frame = new MainFrame(file);

                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }


}
