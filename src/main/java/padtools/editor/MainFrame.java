package padtools.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import padtools.Constants;
import padtools.Main;
import padtools.Setting;
import padtools.core.formats.spd.ParseErrorException;
import padtools.core.formats.spd.ParseErrorReceiver;
import padtools.core.formats.spd.SPDParser;
import padtools.core.models.PADModel;
import padtools.core.view.BufferedView;
import padtools.core.view.Model2View;
import padtools.core.view.ViewOption;
import padtools.util.PathUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 *
 * @author monaou
 */
public class MainFrame extends JFrame {

    //現在開いているファイル
    private File filePath = null;
    //最後に選択された行
    private int beforeLine = 0;

    //モデル変換
    private final Model2View model2View = new Model2View();

    //エディタ部コントロール
    private final SPDEditor editor;

    //エラー一覧部コントロール
    private final JList messageList;

    //ビュー部コントロール
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

    public MainFrame(final File file) {
        Setting setting = Main.getSetting();

        //各コンポーネントを生成を生成
        editor = new SPDEditor();
        messageList = new JList(new DefaultListModel());

        //アイコンを読み込む
        loadIcons();

        //イベント設定
        initSPDEditorEvent();

        //描画の設定
        ViewOption defOpt = model2View.getOptionMap().get(model2View.KEY_DEFAULT);
        defOpt.setPaint(new Color(0.2f, 0.2f, 0.2f));
        defOpt.setStroke(new BasicStroke(2.0f));

        //全体のパネル生成
        JPanel mainPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout(10, 10));

        //ツールバー設定
        try {
            setJMenuBar(createMenuBar());
            if(!setting.isDisableToolbar()) {
                mainPanel.add(createToolBar(), BorderLayout.NORTH);
            }
        } catch (IOException ex) {
        }

        //レイアウト及びスクロールバー生成
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

        //メインウインドウの動作を設定
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initMainWindowListener();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //初期値設定
                if(file == null){
                    initWithDefaultText();
                }
                else {
                    open(file);
                }
            }
        });

        //タイトルを更新する
        updateTitle();
    }

    private ImageIcon iconNew;
    private ImageIcon iconOpen;
    private ImageIcon iconSave;
    private ImageIcon iconSavePad;
    private ImageIcon iconRefresh;
    private ImageIcon iconHelp;
    private void loadIcons() {
        //アイコンの読み込み
        //画像を読み込む
        try {
            iconNew = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/new.png")));
        } catch (IOException io) {
            iconNew = null;
        }
        try {
            iconOpen = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/open.png")));
        } catch (IOException io) {
            iconOpen = null;
        }
        try {
            iconSave = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/save.png")));
        } catch (IOException io) {
            iconSave = null;
        }
        try {
            iconRefresh = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/refresh.png")));
        } catch (IOException io) {
            iconRefresh = null;
        }
        try {
            iconSavePad = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/pictures.png")));
        } catch (IOException io) {
            iconSavePad = null;
        }
        try {
            iconHelp = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/help.png")));
        } catch (IOException io) {
            iconHelp = null;
        }
    }

    //新規作成アクション
    private ActionListener actionNew = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                initWithDefaultText();
                filePath = null;
                updateTitle();
            }
        }
    };
    //開くアクション
    private ActionListener actionOpen = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                open();
            }
        }
    };
    //保存アクション
    private ActionListener actionSave = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            save();
        }
    };
    //名前をつけて保存アクション
    private ActionListener actionSaveAs = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            saveAs();
        }
    };
    //PNG形式で保存アクション
    private ActionListener actionSavePadImageAsPng = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            applyLogic();
            savePadImageAsPng();
        }
    };
    //SVG形式で保存アクション
    private ActionListener actionSavePadImageAsSvg = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            applyLogic();
            savePadImageAsSvg();
        }
    };
    //画面の更新アクション
    private ActionListener actionRefresh = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            applyLogic();
        }
    };
    //バージョン情報アクション
    private ActionListener actionVersion = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JOptionPane.showMessageDialog(MainFrame.this, Constants.APP_NAME + " " + Constants.APP_VERSION, "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
        }
    };
    //閉じるアクション
    private ActionListener actionClose = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (releaseOK()) {
                MainFrame.this.dispose();
            }
        }
    };
    private JToolBar createToolBar() throws IOException {
        Setting setting = Main.getSetting();

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

        if(!setting.isDisableSaveMenu()) {
            button = new JButton("保存", iconSave);
            toolBar.add(button);
            button.setBorderPainted(false);
            button.addActionListener(actionSave);
        }

        toolBar.addSeparator();

        button = new JButton("再描画", iconRefresh);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionRefresh);

        toolBar.addSeparator();

        button = new JButton("PNG形式で保存", iconSavePad);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionSavePadImageAsPng);

        button = new JButton("SVG形式で保存", iconSavePad);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionSavePadImageAsSvg);

        toolBar.add(Box.createGlue());
        toolBar.addSeparator();

        button = new JButton("バージョン情報", iconHelp);
        toolBar.add(button);
        button.setBorderPainted(false);
        button.addActionListener(actionVersion);

        return toolBar;
    }
    private JMenuBar createMenuBar() throws IOException {
        Setting setting = Main.getSetting();

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("ファイル(F)");
        menuBar.add(menu);

        JMenuItem item = new JMenuItem("新規作成(N)", iconNew);
        menu.add(item);
        item.addActionListener(actionNew);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

        menu.addSeparator();

        item = new JMenuItem("開く(O)", iconOpen);
        menu.add(item);
        item.addActionListener(actionOpen);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

        menu.addSeparator();

        if(!setting.isDisableSaveMenu()) {
            item = new JMenuItem("保存(S)", iconSave);
            menu.add(item);
            item.addActionListener(actionSave);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        }

        item = new JMenuItem("名前を付けて保存(A)", null);
        menu.add(item);
        item.addActionListener(actionSaveAs);

        menu.addSeparator();

        item = new JMenuItem("閉じる(X)", null);
        menu.add(item);
        item.addActionListener(actionClose);

        menu = new JMenu("出力(O)");
        menuBar.add(menu);

        item = new JMenuItem("PNG形式で保存(I)", iconSavePad);
        menu.add(item);
        item.addActionListener(actionSavePadImageAsPng);

        item = new JMenuItem("SVG形式で保存(J)", iconSavePad);
        menu.add(item);
        item.addActionListener(actionSavePadImageAsSvg);

        menu = new JMenu("表示(V)");
        menuBar.add(menu);

        item = new JMenuItem("再描画(R)", iconRefresh);
        menu.add(item);
        item.addActionListener(actionRefresh);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));

        menu = new JMenu("ヘルプ(H)");
        menuBar.add(menu);

        item = new JMenuItem("バージョン情報(A)", iconHelp);
        menu.add(item);
        item.addActionListener(actionVersion);

        return menuBar;
    }

    private void initSPDEditorEvent() {
        editor.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent ke) {
            }

            public void keyPressed(KeyEvent ke) {
            }

            public void keyReleased(KeyEvent ke) {
                checkRefresh();
                updateTitle();
            }
        });
        editor.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent fe) {
                if (editor.isEdited()) {
                    applyLogic();
                }
                beforeLine = editor.getDocument().getDefaultRootElement().getElementIndex(editor.getSelectionStart());
            }

            public void focusLost(FocusEvent fe) {
                if (editor.isEdited()) {
                    applyLogic();
                }
            }
        });
        editor.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent me) {
                checkRefresh();
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });
    }

    private void initMainWindowListener() {
        addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent we) {
            }

            public void windowClosing(WindowEvent we) {
                if (releaseOK()) {
                    MainFrame.this.dispose();
                }
            }

            public void windowClosed(WindowEvent we) {
            }

            public void windowIconified(WindowEvent we) {
            }

            public void windowDeiconified(WindowEvent we) {
            }

            public void windowActivated(WindowEvent we) {
            }

            public void windowDeactivated(WindowEvent we) {
            }
        });
    }

    private void initWithDefaultText(){
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

    private void updateTitle() {
        String fn;
        if(filePath == null) {
            fn = "NEW";
        } else {
            fn = filePath.getPath();
        }
        String flag;
        if(editor.isRequireSave()) {
            flag = "*";
        } else {
            flag = "";
        }
        this.setTitle(Constants.APP_NAME + " " + Constants.APP_VERSION + " " + "[" + flag + fn + "]");
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
        updateTitle();
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
        updateTitle();
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

    private void savePadImageAsPng() {
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

    void outputSVG(File f) {
        Rectangle r = viewPanel.getBounds();
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svg2d = new SVGGraphics2D(document);
        svg2d.setBackground(new Color(255,255,255,0));
        svg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        view.getView().draw(svg2d, new Point2D.Double());
        Element sv = svg2d.getRoot();
        sv.setAttribute("xml:space", "preserve");
        sv.setAttribute("width", Integer.toString((int)r.getWidth()));
        sv.setAttribute("height", Integer.toString((int)r.getHeight()));
        sv.setAttribute("viewBox",
                        Integer.toString((int)r.getX())+" "+
                        Integer.toString((int)r.getY())+" "+
                        Integer.toString((int)r.getWidth())+" "+
                        Integer.toString((int)r.getHeight())
        );
        try (OutputStream os = new FileOutputStream(f);
             BufferedOutputStream bos = new BufferedOutputStream(os);
             Writer out = new OutputStreamWriter(bos, "UTF-8");){
           svg2d.stream(sv,out);
        } catch (UnsupportedEncodingException ue){
            ue.printStackTrace();
        } catch (SVGGraphics2DIOException se){
            se.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
       }
    }

    private void savePadImageAsSvg() {
        applyLogic();
        JFileChooser fc = new JFileChooser(filePath == null ? new File(".") : filePath);
        fc.setFileFilter(new FileNameExtensionFilter("svg image(*.svg)", "svg"));

        File sel;
        if(filePath == null){
            sel = new File("./new_pad.svg");
        }
        else{
            sel = new File(PathUtil.extConvert(filePath.getPath(), "svg"));
        }

        fc.setSelectedFile(sel);

        if (fc.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
            outputSVG(fc.getSelectedFile());
        }
    }
}
