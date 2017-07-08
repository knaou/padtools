package padtools.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
/**
 * LPDを編集するペイン。
 * @author monaou
 */
class SPDEditor extends JTextPane {

    private class RowHeader extends JComponent {
        private int fontHeight = 0;
        
        private RowHeader(){
            setPreferredSize(new Dimension(30, 0));
            setBorder(null);
            setFont(new Font("Dialog", Font.PLAIN, 10));
        }

        @Override
        protected void paintComponent(Graphics grphcs) {
            if( fontHeight <= 0 ){
                FontMetrics fm = getFontMetrics(getFont());
                fontHeight = fm.getHeight();
            }
            
            Element elm = SPDEditor.this.getDocument().getDefaultRootElement();
            Rectangle rect = grphcs.getClipBounds();
            int startno = elm.getElementIndex(SPDEditor.this.viewToModel(new Point(0, rect.y)));
            int endno = elm.getElementIndex(SPDEditor.this.viewToModel(new Point(0, rect.y + rect.height)));
            
            for(int no=startno; no<=endno; ++no){
                try {              
                    Rectangle re = SPDEditor.this.modelToView(elm.getElement(no).getStartOffset());
                    
                    if( re != null){
                        grphcs.setColor(Color.DARK_GRAY);
                        grphcs.drawString(String.format("%03d", no + 1), 0, re.y + fontHeight - 2);
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(SPDEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class SimpleSyntaxDocument extends DefaultStyledDocument {

        private HashSet<String> keywords = new HashSet<String>();
        private MutableAttributeSet keywordMark = new SimpleAttributeSet();
        private MutableAttributeSet normal = new SimpleAttributeSet();
        private MutableAttributeSet comment = new SimpleAttributeSet();

        private SimpleSyntaxDocument() {
            super();
            StyleConstants.setForeground(normal, Color.BLACK);
            StyleConstants.setBold(normal, false);
            StyleConstants.setForeground(keywordMark, Color.BLUE);
            StyleConstants.setBold(keywordMark, true);
            StyleConstants.setForeground(comment, Color.GRAY);
            keywords.add(":if");
            keywords.add(":while");
            keywords.add(":else");
            keywords.add(":switch");
            keywords.add(":dowhile");
            keywords.add(":comment");
            keywords.add(":case");
            keywords.add(":call");
            keywords.add(":terminal");
        }

        @Override
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            setEdited(true);
            setRequireSave(true);
            super.insertString(offset, str, a);
            applyHighlight(offset, str.length());
            
            if(str.equals("\n")){
                Element root = getDefaultRootElement();
                int startLine = root.getElementIndex(offset);
                int s = root.getElement(startLine).getStartOffset();
                int e = root.getElement(startLine).getEndOffset();
                String line = getText(s, e-s);
                String tab = "";
                for(int i=0; i<line.length() && line.charAt(i) == '\t'; ++i){
                    tab += "\t";
                }
                
                line = line.trim();
                if(     line.startsWith(":if") || 
                        line.startsWith(":while") ||
                        line.startsWith(":dowhile") ||
                        line.startsWith(":case") ||
                        line.startsWith(":else")){
                    tab += "\t";
                }

                insertString(offset + str.length(),tab, a);
            }
            
        }

        @Override
        public void remove(int offset, int length) throws BadLocationException {
            setEdited(true);
            setRequireSave(true);
            super.remove(offset, length);
            applyHighlight(offset, 0);
        }
        
        public void refreshHighlght(){
            try{
                applyHighlight(0, getLength());
            }catch(BadLocationException ex){}
        }

        private void applyHighlight(int offset, int length) throws BadLocationException {
            Element root = getDefaultRootElement();
            int startLine = root.getElementIndex(offset);
            int endLine = root.getElementIndex(offset + length);
            for (int i = startLine; i <= endLine; i++) {
                int s = root.getElement(i).getStartOffset();
                int e = root.getElement(i).getEndOffset();
                setCharacterAttributes(s, e-s, normal, true);
                String line = getText(s, e-s);
                if( line.trim().startsWith("#")){
                    setCharacterAttributes(s, e-s, comment, true);
                }
                else {
                    for(String key : keywords){
                        for(int n=0;;n++){
                            int pos = line.indexOf(key, n);
                            if( pos < 0)break;
                            if(
                                    (pos == 0 || Character.isWhitespace(line.charAt(pos-1))) &&
                                    (pos + key.length() + 1 == line.length() || Character.isWhitespace(line.charAt(pos + key.length())))){
                                setCharacterAttributes(s + pos, key.length(),keywordMark, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private final SimpleSyntaxDocument doc;
    private final RowHeader rowHeader;
    private boolean edited = false;
    private boolean reqSave = false;
    
    public SPDEditor() {
        setDocument(doc = (SimpleSyntaxDocument)new SimpleSyntaxDocument());
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setSpaceAbove(attr, 2.0f);
        doc.setParagraphAttributes(0, doc.getLength(), attr, true);
        
        //タブを設定する。
        setFont(new Font("Dialog", Font.PLAIN, 14));
        FontMetrics fm = getFontMetrics(getFont());
        int charWidth = fm.charWidth('m');
        int tabLength = charWidth * 2;
        TabStop[] tabs = new TabStop[10];
        for (int j = 0; j < tabs.length; j++) {
            tabs[j] = new TabStop((j + 1) * tabLength);
        }
        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setTabSet(attrs, tabSet);
        int l = getDocument().getLength();
        getStyledDocument().setParagraphAttributes(0, l, attrs, false);
        
        rowHeader = new RowHeader();

        //右クリックメニューをつける
        new JTextEditPopupMenu(this).assignEvent();
    }
    
    public void setErrorLine(int line){
        int s = doc.getDefaultRootElement().getElement(line).getStartOffset();
        int e = doc.getDefaultRootElement().getElement(line).getEndOffset();
        
        MutableAttributeSet error = new SimpleAttributeSet();
        StyleConstants.setForeground(error, Color.RED);
        StyleConstants.setUnderline(error, true);
        doc.setCharacterAttributes(s, e-s, error, true);
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs);
        
        Rectangle rect = grphcs.getClipBounds();
        rowHeader.repaint(0, rect.y, rowHeader.getWidth(), rect.height);
    }
    
    public JScrollPane withScroll(){
        JPanel panel = new JPanel(new BorderLayout(0,0)){

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                return new Dimension(1, dim.height);
            }
            
        };
        panel.add(rowHeader, BorderLayout.WEST);
        panel.add(this, BorderLayout.CENTER);
        this.setBorder(null);
        
        JScrollPane ret = new JScrollPane(panel);
        ret.setBorder(new LineBorder(Color.gray));
        

        return ret;
    }
    
    public void refreshHighlight(){
        doc.refreshHighlght();
    }
    
    
    /**
     * @return the edited
     */
    public boolean isEdited() {
        return edited;
    }

    /**
     * @param edited the edited to set
     */
    public void setEdited(boolean edited) {
        this.edited = edited;
    }
    
    /**
     * @return the saved
     */
    public boolean isRequireSave() {
        return reqSave;
    }

    /**
     * @param reqSave the saved to set
     */
    public void setRequireSave(boolean reqSave) {
        this.reqSave = reqSave;
    }
    
}