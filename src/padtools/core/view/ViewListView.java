package padtools.core.view;

import padtools.core.models.TerminalNode;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.LinkedList;
import java.util.List;

/**
 * Viewのリストを表示する。
 * マージンとパディングは不使用。
 * @author monaou
 */
public class ViewListView extends View{
    private final List<View> views;
    
    public ViewListView(ViewOption viewOption){
        super(viewOption);
        
        views = new LinkedList<View>();
    }
    
    public List<View> getViewList(){
        return views;
    }

    @Override
    public Double getSize(Graphics2D g, ViewOption viewOption) {
        double w = 0, h = 0;
        boolean first = true;
        View last = null;
        
        for(View view : views){
            if(first){
                first = false;
                h -= view.getViewOption().getMargin().top;
            }
            
            Point2D.Double s = view.getSize(g);
            double w_ = s.x - view.getViewOption().getMargin().left - view.getViewOption().getMargin().right;
            if( w < w_) w = w_;
            h += s.y;
            
            last = view;
        }
        
        if(last != null){
            h -= last.getViewOption().getMargin().bottom;
        }
        
        ViewOption.Insets2D mar = viewOption.getMargin();
        w += mar.left + mar.right;
        h += mar.top + mar.bottom;

        return new Double(w, h);
    }

    @Override
    public void draw(Graphics2D g, Double p, ViewOption viewOption) {
        double x = p.x;
        double y = p.y;
        boolean first = true;
        View last = null;
        
        ViewOption.Insets2D mar = viewOption.getMargin();
        
        for(View view : views){
            if(first){
                first = false;
                y -= view.getViewOption().getMargin().top;
            }
            
            view.draw(g, new Double(x - view.getViewOption().getMargin().left + mar.left, y + mar.top));
            y += view.getSize(g).y;
            
            last = view;
        }
        
        if(last != null){
            y -= last.getViewOption().getMargin().bottom;
            y -= last.getSize(g).y - last.getInnerSize(g).y;
        }
        
        //TODO: このこの判定実装場所を配慮すべし
        double y1 = p.y + mar.top;
        double y2 = y + mar.top;
        View v;
        if (views.size() > 0 && 
                BoxView.class.isInstance(v = views.get(0)) &&
                ((BoxView)v).getBorderType() == BoxView.BorderType.WRounded){
            y1 += viewOption.getRoundShort() / 2.0;
        }

        if (views.size() > 0 &&
                BoxView.class.isInstance(v = views.get(views.size() - 1)) &&
                ((BoxView)v).getBorderType() == BoxView.BorderType.WRounded){
            y2 -= viewOption.getRoundShort() / 2.0;
        }
       
        viewOption.apply(g);
        g.draw(new Line2D.Double(p.x + mar.left, y1, x + mar.left, y2));
    }
    
}
