package padtools.editor;

import padtools.util.Option;
import padtools.util.OptionParser;
import padtools.util.UnknownOptionException;

import javax.swing.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(UnsupportedLookAndFeelException ex){}
        catch(ClassNotFoundException ex){}
        catch(InstantiationException ex){}
        catch(IllegalAccessException ex){}


        final Option optHelp = new Option("h", "help", false);
        final OptionParser optParser = new OptionParser(new Option[]{optHelp});
        try{
            optParser.parse(args, 1);
        }
        catch(UnknownOptionException ex){
            System.err.println("Unknown option: " + ex.getOption());
            System.exit(1);
        }

        if(optHelp.isSet()){
            System.err.println("$ " + args[0] +  " [lpd_file] [-h]");
            System.err.println("  lpd_file: Open lpd file.");
            System.err.println("        -h: Show this help.");
            System.exit(1);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                MainFrame frame = new MainFrame(optParser.getArguments().size() == 0 ? null : new File(optParser.getArguments().get(0)));

                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }


}
