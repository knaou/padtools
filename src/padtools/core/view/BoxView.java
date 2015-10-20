package padtools.core.view;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author monaou
 */
public class BoxView extends View{
    /**
     * 描画タイプを指定する
     */
    enum BorderType {
        /* 枠なし */
        None,
        /* 四角形 */
        Box,
        /* 横長の角丸（Left/Right Barとの併用不可） */
        WRounded
    }
    
    private BorderType borderType;
    private boolean drawLeftBar;
    private boolean drawRightBar;
    private View innerView;
    private View subView;
    
    public BoxView(ViewOption viewOption, View innerView, View subView, BorderType borderType, boolean drawLeftBar, boolean drawRightBar){
        super(viewOption);
        this.borderType = borderType;
        this.drawLeftBar = drawLeftBar;
        this.drawRightBar = drawRightBar;
        
        this.innerView = innerView;
        this.subView = subView;
    }
    
    public BorderType getBorderType(){
        return borderType;
    }
    
    public void setDrawBox(BorderType borderType){
        this.borderType = borderType;
    }
    
    public boolean isDrawLeftBar(){
        return drawLeftBar;
    }
    
    public void setDrawLeftBar(boolean drawLeftBar){
        this.drawLeftBar = drawLeftBar;
    }
    
    public boolean isDrawRightBar(){
        return drawRightBar;
    }
    
    public void setDrawRightBar(boolean drawRifhtBar){
        this.drawRightBar = drawRifhtBar;
    }
    
    public View getInnerView(){
        return innerView;
    }
    
    public void setInnerView(View innerView){
        this.innerView = innerView;
    }
    
    public View getSubView(){
        return this.subView;
    }
    
    public void setSubView(View subView){
        this.subView = subView;
    }

    @Override
    public Double getSize(Graphics2D g, ViewOption viewOption) {
        Point2D.Double s = innerView == null ? new Point2D.Double() : innerView.getSize(g);
        ViewOption.Insets2D mar = viewOption.getMargin();

        double w = s.x;
        double h = s.y;
        if(drawLeftBar) w += viewOption.getBarWidthInBox();
        if(drawRightBar) w += viewOption.getBarWidthInBox();
        
        if(subView != null){
            w += viewOption.getSubViewConnectWidth();
            
            Point2D.Double s2 = subView.getSize(g);
            ViewOption.Insets2D s2mar = subView.getViewOption().getMargin();
            double s2x = s2.x - s2mar.left - s2mar.right;
            double s2y = s2.y - s2mar.top - s2mar.bottom;
            
            w += s2x;
            
            if( h < s2y) h = s2y;
        }
        
        w += mar.left + mar.right;
        h += mar.top + mar.bottom;
        
        return new Double(w, h);
    }

    @Override
    public Double getInnerSize(Graphics2D g, ViewOption viewOption) {
        Point2D.Double s = innerView == null ? new Point2D.Double() : innerView.getSize(g);
        ViewOption.Insets2D mar = viewOption.getMargin();

        double w = s.x;
        double h = s.y;
        if(drawLeftBar) w += viewOption.getBarWidthInBox();
        if(drawRightBar) w += viewOption.getBarWidthInBox();
        w += mar.left + mar.right;
        h += mar.top + mar.bottom;
        
        return new Double(w, h);
    }

    @Override
    public void draw(Graphics2D g, Double p, ViewOption viewOption) {
        Point2D.Double s = innerView == null ? new Point2D.Double() : innerView.getSize(g);
        ViewOption.Insets2D mar = viewOption.getMargin();
        
        //サブ描画
        if(subView != null){
            double boxRight = p.x + mar.left + s.x;
            if(drawLeftBar) boxRight += viewOption.getBarWidthInBox();
            if(drawRightBar) boxRight += viewOption.getBarWidthInBox();
            ViewOption.Insets2D submar = subView.getViewOption().getMargin();
            subView.draw(g, new Double(
                    boxRight + viewOption.getSubViewConnectWidth() - submar.left,
                    p.y + mar.top - submar.top));
            viewOption.apply(g);
            g.draw(new Line2D.Double(
                    boxRight, p.y + mar.top,
                    boxRight + viewOption.getSubViewConnectWidth(), p.y + mar.top));
        }
        
        //Box描画
        viewOption.apply(g);

        double w = s.x;
        double h = s.y;
        if(drawLeftBar) w += viewOption.getBarWidthInBox();
        if(drawRightBar) w += viewOption.getBarWidthInBox();
        Rectangle2D.Double box = new Rectangle2D.Double(p.x + mar.left, p.y + mar.top, w, h);
        switch(borderType) {
            case Box:
                g.setPaint(viewOption.getBackGroundPaint());
                g.fill(box);
                break;
            case WRounded:
                g.setPaint(viewOption.getBackGroundPaint());
                g.fillRoundRect((int)box.x, (int)box.y, (int)box.width, (int)box.height,
                        viewOption.getRoundLong().intValue(),
                        viewOption.getRoundShort().intValue());
                break;
        }

            
        if(innerView != null){
            innerView.draw(g, new Double(
                    p.x + mar.left + (drawLeftBar ? viewOption.getBarWidthInBox() : 0),
                    p.y + mar.top));
        }

        switch(borderType) {
            case Box:
                g.setPaint(viewOption.getPaint());
                g.draw(box);

                if(drawLeftBar){
                    double x = p.x + mar.left + viewOption.getBarWidthInBox();
                    g.draw(new Line2D.Double(
                            x, p.y + mar.top,
                            x, p.y + mar.top + h
                    ));
                }
                if(drawRightBar){
                    double x = p.x + mar.left + w - viewOption.getBarWidthInBox();
                    g.draw(new Line2D.Double(
                            x, p.y + mar.top,
                            x, p.y + mar.top + h
                    ));
                }
                break;
            case WRounded:
                g.setPaint(viewOption.getPaint());
                g.drawRoundRect((int)box.x, (int)box.y, (int)box.width, (int)box.height,
                        viewOption.getRoundLong().intValue(),
                        viewOption.getRoundShort().intValue());
                break;
        }
    }
}
