package padtools.editor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * タイトル付きパネルを生成するためのクラス。
 */
class TitledPanel extends JPanel {

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
