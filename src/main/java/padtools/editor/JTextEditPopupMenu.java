package padtools.editor;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JTextEditPopupMenu extends JPopupMenu {
    private final JTextComponent owner;
    private final JMenuItem itemCut;
    private final JMenuItem itemCopy;
    private final JMenuItem itemPaste;
    private final JMenuItem itemSelectAll;

    public JTextEditPopupMenu(JTextComponent text){
        super("Text");

        owner = text;

        ActionMap am = text.getActionMap();

        itemCut = add(am.get(DefaultEditorKit.cutAction));
        itemCut.setText("切り取り(X)");
        itemCut.setMnemonic('X');

        itemCopy = add(am.get(DefaultEditorKit.copyAction));
        itemCopy.setText("コピー(C)");
        itemCopy.setMnemonic('C');

        itemPaste = add(am.get(DefaultEditorKit.pasteAction));
        itemPaste.setText("貼り付け(V)");
        itemPaste.setMnemonic('V');

        itemSelectAll = add(am.get(DefaultEditorKit.selectAllAction));
        itemSelectAll.setText("全て選択(A)");
        itemSelectAll.setMnemonic('A');

        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String selected = owner.getSelectedText();
                boolean empty = selected == null || selected.length() == 0;

                itemCut.setEnabled(!empty);
                itemCopy.setEnabled(!empty);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }

    public JTextEditPopupMenu assignEvent() {
        owner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger()) {
                    owner.requestFocusInWindow();
                    JTextEditPopupMenu.this.show((Component) e.getSource(), e.getX(), e.getY());
                }
            }
        });

        return this;
    }
}
