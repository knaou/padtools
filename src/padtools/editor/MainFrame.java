package padtools.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import padtools.core.formats.spd.ParseErrorException;
import padtools.core.formats.spd.ParseErrorReceiver;
import padtools.core.formats.spd.SPDParser;
import padtools.core.models.PADModel;
import padtools.core.view.BufferedView;
import padtools.core.view.Model2View;
import padtools.core.view.ViewOption;
import padtools.util.PathUtil;

/*
 *
 * @author monaou
 */
public class MainFrame extends JFrame {

    /**
     * タイトル付きパネルを生成するためのクラス。
     */
    private class TitledPanel extends JPanel {

        /**
         * タイトル付きのコンポーネントを表示する。
         * @param comp 表示するメインのコンポーネント。
         * @param title 表示するタイトル。
         */
        public TitledPanel(Component comp, String title) {
            super(new BorderLayout());
            //this.add(new JLabel(title), BorderLayout.NORTH);
            this.add(comp, BorderLayout.CENTER);
            this.setBorder(new TitledBorder(title));
        }
    }

    private final SPDEditor editor;
    private final JList messageList;
    private final Model2View model2View = new Model2View();
    private File filePath = null;//開いているファイル
    private BufferedView view = null;
    private final JPanel viewPanel = new JPanel() {

        @Override
        protected void paintComponent(Graphics grphcs) {
            Graphics2D g = (Graphics2D) grphcs;
            Dimension s = getSize();
            g.setPaint(new GradientPaint(new Point(), Color.white, new Point(s.width, s.height), Color.lightGray));
            g.fillRect(0, 0, s.width, s.height);
            if (view != null) {
                view.draw(g, new Point2D.Double());
            }
        }
    };
    //最後に選択された行
    private int beforeLine = 0;

    public MainFrame(final File file) {
        super("SPD Editor");
        //各コンポーネントを生成を生成
        editor = new SPDEditor();
        messageList = new JList(new DefaultListModel());

        //アイコンの読み込み
        //画像を読み込む
        try {
            iconNew = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/new.png")));
        } catch (IOException io) {
            iconNew = null;
        }
        try {
            iconOpen = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/open.png")));
        } catch (IOException io) {
            iconOpen = null;
        }
        try {
            iconSave = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/save.png")));
        } catch (IOException io) {
            iconSave = null;
        }
        try {
            iconRefresh = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/refresh.png")));
        } catch (IOException io) {
            iconRefresh = null;
        }
        try {
            iconSavePad = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/pictures.png")));
        } catch (IOException io) {
            iconSavePad = null;
        }
        try {
            iconHelp = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("padtools/editor/resources/help.png")));
        } catch (IOException io) {
            iconHelp = null;
        }

        //イベント設定
        initSPDEditorEvent();

        //描画の設定
        ViewOption defOpt = model2View.getOptionMap().get(model2View.KEY_DEFAULT);
        defOpt.setPaint(new Color(0.2f, 0.2f, 0.2f));
        defOpt.setStroke(new BasicStroke(2.0f));

        //構成を設定
        JPanel mainPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout(10, 10));

        //ツールバー設定
        try {
            setJMenuBar(createMenuBar());
            mainPanel.add(createToolBar(), BorderLayout.NORTH);
        } catch (IOException ex) {
        }

        JComponent editorWithScroll = editor.withScroll();
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new TitledPanel(editorWithScroll, "Logic入力"), new TitledPanel(messageList, "エラー情報"));
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, new JScrollPane(viewPanel));
        mainPanel.add(mainSplit, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        //表示の調整
        mainPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        messageList.setBackground(new Color(0.9f, 0.9f, 0.9f));
        messageList.setBorder(new LineBorder(Color.gray));
        leftSplit.setResizeWeight(0.8);
        mainSplit.setResizeWeight(0.3);
        mainSplit.setBorder(null);
        leftSplit.setBorder(null);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent we) {
            }

            @Override
            public void windowClosing(WindowEvent we) {
                if (releaseOK()) {
                    MainFrame.this.dispose();
                }
            }

            @Override
            public void windowClosed(WindowEvent we) {
            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });

        
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    //初期値設定
                    if(file == null){
                    
                        //初期文字列設定
                        String header = ":terminal START\n\n";
                        String comment = "#ロジックを記述してください\nロジック";
                        String footer = "\n\n:terminal END";
                        editor.requestFocusInWindow();
                        editor.setText(header + comment + footer);
                        editor.select(header.length(), header.length() + comment.length());
                        editor.setEdited(false);
                        editor.setRequireSave(false);

                        applyLogic();
                    }
                    else {
                        open(file);
                    }
                    
                }
            });
    }
        
    private ImageIcon iconNew;
    private ActionListener actionNew = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                newDocument();
            }
        }
    };
    private ImageIcon iconOpen;
    private ActionListener actionOpen = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                open();
            }
        }
    };
    private ImageIcon iconSave;
    private ActionListener actionSave = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            save();
        }
    };
    private ActionListener actionSaveAs = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            saveAs();
        }
    };
    private ImageIcon iconClipboard;
    private ImageIcon iconSavePad;
    private ActionListener actionSavePadImage = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            applyLogic();
            savePadImage();
        }
    };
    private ImageIcon iconRefresh;
    private ActionListener actionRefresh = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            applyLogic();
        }
    };
    private ImageIcon iconHelp;
    private ActionListener actionVersion = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JOptionPane.showMessageDialog(MainFrame.this, "PadTools 1.0", "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
        }
    };
    private ActionListener actionClose = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                MainFrame.this.dispose();
            }
        }
    };

    private JToolBar createToolBar() throws IOException {
        JToolBar toolBar = new JToolBar();
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setFocusCycleRoot(false);

        JButton button = new JButton("新規作成", iconNew);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionNew);

        button = new JButton("開く", iconOpen);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionOpen);

        button = new JButton("保存", iconSave);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionSave);

        toolBar.addSeparator();

        button = new JButton("再描画", iconRefresh);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionRefresh);

        toolBar.addSeparator();

        button = new JButton("PAD図を保存", iconSavePad);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionSavePadImage);

        toolBar.add(Box.createGlue());
        toolBar.addSeparator();

        button = new JButton("バージョン情報", iconHelp);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionVersion);

        return toolBar;
    }

    private JMenuBar createMenuBar() throws IOException {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("ファイル(F)");
        menuBar.add(menu);

        JMenuItem item = new JMenuItem("新規作成(N)", iconNew);
        menu.add(item);
        item.addActionListener(actionNew);

        menu.addSeparator();

        item = new JMenuItem("開く(O)", iconOpen);
        menu.add(item);
        item.addActionListener(actionOpen);

        menu.addSeparator();

        item = new JMenuItem("保存(S)", iconSave);
        menu.add(item);
        item.addActionListener(actionSave);

        item = new JMenuItem("名前を付けて保存(A)", null);
        menu.add(item);
        item.addActionListener(actionSaveAs);

        menu.addSeparator();

        item = new JMenuItem("閉じる(X)", null);
        menu.add(item);
        item.addActionListener(actionClose);

        menu = new JMenu("出力(O)");
        menuBar.add(menu);

        item = new JMenuItem("PAD図を保存(I)", iconSavePad);
        menu.add(item);
        item.addActionListener(actionSavePadImage);

        menu = new JMenu("表示(V)");
        menuBar.add(menu);

        item = new JMenuItem("再描画(R)", iconRefresh);
        menu.add(item);
        item.addActionListener(actionRefresh);

        menu = new JMenu("ヘルプ(H)");
        menuBar.add(menu);

        item = new JMenuItem("バージョン情報(A)", iconHelp);
        menu.add(item);
        item.addActionListener(actionVersion);

        return menuBar;
    }

    private void initSPDEditorEvent() {
        editor.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                checkRefresh();
            }
        });
        editor.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent fe) {
                if (editor.isEdited()) {
                    applyLogic();
                }
                beforeLine = editor.getDocument().getDefaultRootElement().getElementIndex(editor.getSelectionStart());
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (editor.isEdited()) {
                    applyLogic();
                }
            }
        });
        editor.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                checkRefresh();
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });
    }

    private void checkRefresh() {
        int line = editor.getDocument().getDefaultRootElement().getElementIndex(editor.getSelectionStart());
        if (beforeLine != line) {
            beforeLine = line;
            if (editor.isEdited()) {
                applyLogic();
            }
        }
    }

    private void applyLogic() {
        ((DefaultListModel) messageList.getModel()).removeAllElements();
        editor.refreshHighlight();

        final PADModel model = SPDParser.parse(editor.getText(), new ParseErrorReceiver() {

            public boolean receiveParseError(String lineStr, int lineNo, ParseErrorException err) {
                ((DefaultListModel) messageList.getModel()).addElement(String.format("line %d, %s", lineNo + 1, err.getUserMessage()));
                editor.setErrorLine(lineNo);
                return true;
            }
        });

        view = new BufferedView(model2View.toView(model), true);
        editor.setEdited(false);

        Point2D.Double s = view.getSize((Graphics2D) getGraphics());
        Dimension d = new Dimension((int) s.x, (int) s.y);
        viewPanel.setSize(d);
        viewPanel.setPreferredSize(d);
        viewPanel.updateUI();
    }

    /**
     * 解放してよいか確認する。
     * @return 開放して良いか。 
     */
    private boolean releaseOK() {
        if (!editor.isRequireSave()) {
            return true;
        }

        switch (JOptionPane.showConfirmDialog(
                this,
                "編集されています。\n保存しますか？",
                "編集されています",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE)) {

            case JOptionPane.YES_OPTION:
                return save();
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
            default:
                return false;
        }
    }

    private void newDocument() {
        editor.setText("");
        editor.setRequireSave(false);
        applyLogic();
    }

    private void open(File file) {
        filePath = file;
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringWriter sw = new StringWriter();

            String buf;
            while ((buf = br.readLine()) != null) {
                sw.append(buf);
                sw.append("\n");
            }

            br.close();
            fr.close();
            editor.setText(sw.toString());
            editor.setRequireSave(false);
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(
                    this,
                    ex.getLocalizedMessage(),
                    "読み込み失敗",
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE);
        }
        
        applyLogic();
    }

    private void open() {
        JFileChooser fc = new JFileChooser(filePath == null ? new File(".") : filePath);
        fc.setFileFilter(new FileNameExtensionFilter("Simple Pad Description(*.spd)", "spd"));

        if (fc.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
            open(fc.getSelectedFile());
        }
    }

    private boolean save() {
        return filePath == null ? saveAs() : save(filePath);
    }

    private boolean save(File file) {
        filePath = file;
        try {
            PrintWriter ps = new PrintWriter(file);
            ps.print(editor.getText());
            ps.close();
            editor.setRequireSave(false);
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(
                    this,
                    ex.getLocalizedMessage(),
                    "保存失敗",
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            return saveAs();
        }
        applyLogic();
        return true;
    }

    private boolean saveAs() {
        JFileChooser fc = new JFileChooser(filePath == null ? new File(".") : filePath);
        fc.setFileFilter(new FileNameExtensionFilter("Simple Pad Description(*.spd)", "spd"));
        fc.setSelectedFile(new File("new_document.spd"));
        if (fc.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
            return save(fc.getSelectedFile());
        } else {
            return false;
        }
    }

    private void savePadImage() {
        applyLogic();
        JFileChooser fc = new JFileChooser(filePath == null ? new File(".") : filePath);
        fc.setFileFilter(new FileNameExtensionFilter("png image(*.png)", "png"));
        
        File sel;
        if(filePath == null){
            sel = new File("./new_pad.png");
        }
        else{
            sel = new File(PathUtil.extConvert(filePath.getPath(), "png"));
        }
        
        fc.setSelectedFile(sel);

        if (fc.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
            BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D tmpg = tmp.createGraphics();
            
            Point2D.Double size = view.getSize(tmpg);
            double scale = 1.0;

            BufferedImage img = new BufferedImage((int) (size.x * scale), (int) (size.y * scale), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = img.createGraphics();
            g.setTransform(AffineTransform.getScaleInstance(scale, scale));
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            view.getView().draw(g, new Point2D.Double());

            try {
                ImageIO.write(img, "png", fc.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
