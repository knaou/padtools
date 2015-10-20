package padtools.core.view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * 文字列描画のビュー。
 * 非効率的な実装である。
 * @author monaou
 */
public class TextView extends View{
    private String text = null;
    
    public TextView(ViewOption viewOption, String text){
        super(viewOption);
        this.text = text;
    }
    
    public String getText(){
        return text;
    }
    
    public void setText(String text){
        this.text = text;
    }

    @Override
    public Point2D.Double getSize(Graphics2D g, ViewOption viewOption) {       
        Font f = viewOption.getFont();
        FontMetrics m = g.getFontMetrics(f);
        
        double w = 0, h = 0;
        String[] ss = text.split("\n");
        for(String s : ss){
            Rectangle2D rect = m.getStringBounds(s, g);
            if( w < rect.getWidth() ) w = rect.getWidth();
            h += rect.getHeight();
        }
        
        ViewOption.Insets2D mar = viewOption.getMargin();
        w += mar.left + mar.right;
        h += mar.top + mar.bottom;
        
        return new Point2D.Double(w, h);
    }

    @Override
    public void draw(Graphics2D g, Point2D.Double p, ViewOption viewOption) {
        Font f = viewOption.getFont();
        FontMetrics m = g.getFontMetrics(f);
        ViewOption.Insets2D mar = viewOption.getMargin();
        
        viewOption.apply(g);
        
        String[] ss = text.split("\n");
        double y = p.y + m.getAscent() + mar.top;
        for(String s : ss){
            Rectangle2D rect = m.getStringBounds(s, g);
            g.drawString(s, (float)(p.x + mar.left) , (float)y);
            
            y += rect.getHeight();
            
        }
    }
    
}
