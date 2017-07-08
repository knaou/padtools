package padtools.core.view;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author monaou
 */
public class SwitchView extends View{

    private final LinkedHashMap<View, View> cases;
    private View innerView;
    
    public SwitchView(ViewOption viewOption, View innerView){
        super(viewOption);
        
        cases = new LinkedHashMap<View, View>();
        
        this.innerView = innerView;
    }
    
    public View getInnerView(){
        return innerView;
    }
    
    public void setInnerView(View view){
        this.innerView = view;
    }
    
    public Map<View, View> getCaseMap(){
        return cases;
    }

    @Override
    public Point2D.Double getSize(Graphics2D g, ViewOption viewOption) {
        return _getSize(g, viewOption, true);
    }

    @Override
    public Point2D.Double getInnerSize(Graphics2D g, ViewOption viewOption) {
        return _getSize(g, viewOption, false);
    }

    private Point2D.Double _getSize(Graphics2D g, ViewOption viewOption, boolean enableSub) {
        LinkedHashMap<View, View> cases = this.cases;
        //ケースの数が２以下の場合はダミーを追加する
        if(cases.size() < 2){
            cases = new LinkedHashMap<View, View>(cases);
            while(cases.size() < 2){
                cases.put(new TextView(viewOption, ""), null);
            }
        }
        
        Point2D.Double textSize = innerView.getSize(g);
        ViewOption.Insets2D mar = viewOption.getMargin();
        
        double minHeight = viewOption.getCaseSubViewMinHeight();
        if(textSize.y > minHeight) minHeight = textSize.y;
        
        double labelw = 0.0;
        double subvieww = 0.0;
        double h = 0.0;
        
        double x = mar.left;
        double y = mar.top;

        // labelw <- ラベルの最大幅
        // h <- ラベルと、サブビューの合計値
        // subview <- サブビューの最大幅
        int count = 0;
        double lastdy = 0.0;
        double lastldy = 0.0;
        Map<View, Double> ymap = new HashMap<View, Double>();
        for(Map.Entry<View, View> entry : cases.entrySet()){
            Point2D.Double labelSize = entry.getKey().getSize(g);
            Point2D.Double subSize;
            ViewOption.Insets2D subMar;
            
            //サブビューが無い場合は０で初期化
            if(entry.getValue() == null){
                subSize = new Point2D.Double();
                subMar = new ViewOption.Insets2D();
            }
            else {
                subSize = entry.getValue().getSize(g);
                subMar = entry.getValue().getViewOption().getMargin();
            }
            
            //ラベルの最大幅を更新
            if(labelw < labelSize.x) labelw = labelSize.x;
            
            //サブビューの最大幅を更新
            if(subvieww < subSize.x - subMar.left - subMar.right) subvieww = subSize.x - subMar.left - subMar.right;
            
            //ラベルに合わせて高さを更新
            double uply, bottomly;
            if(count == 0){
                uply = 0.0;
                bottomly = labelSize.y;
            }
            else if(count == cases.size() - 1){
                uply = labelSize.y;
                bottomly = 0.0;
            }
            else{
                uply = bottomly = labelSize.y / 2;
            }
            if( lastdy < uply) lastdy += uply - lastdy;
            
            //ラベルが縦長い場合に調整
            double minldy = lastldy > uply ? (lastldy * 2) : (uply * 2);
            lastldy = bottomly;
            if(minldy > lastdy) lastdy = minldy;
            
            //高さを更新
            h += lastdy;
            ymap.put(entry.getKey(), new Double(h));

            //tmp <- 高さ追記分
            if( bottomly > subSize.y){
                lastdy = bottomly;
            }
            else{
                lastdy = subSize.y;
            }
            if(lastdy < minHeight && count < cases.size() - 1) lastdy = minHeight;
            
            count += 1;            
        }

        if(enableSub) h += lastdy;

        return new Point2D.Double(
                textSize.x + labelw + subvieww + viewOption.getCaseWidth() + viewOption.getSubViewConnectWidth() + mar.left + mar.right,
                h + mar.top + mar.bottom);
    }

    @Override
    public void draw(Graphics2D g, Point2D.Double p, ViewOption viewOption) {
        LinkedHashMap<View, View> cases = this.cases;
        //ケースの数が２以下の場合はダミーを追加する
        if(cases.size() < 2){
            cases = new LinkedHashMap<View, View>(cases);
            while(cases.size() < 2){
                cases.put(new TextView(viewOption, ""), null);
            }
        }
        
        
        Point2D.Double textSize = innerView.getSize(g);
        ViewOption.Insets2D mar = viewOption.getMargin();
        
        double minHeight = viewOption.getCaseSubViewMinHeight();
        if(textSize.y > minHeight) minHeight = textSize.y;
        
        double labelw = 0.0;
        double subvieww = 0.0;
        double h = 0.0;
        
        double x = p.x + mar.left;
        double y = p.y + mar.top;

        // labelw <- ラベルの最大幅
        // h <- ラベルと、サブビューの合計値
        // subview <- サブビューの最大幅
        int count = 0;
        double lastdy = 0.0;
        double lastldy = 0.0;
        Map<View, Double> ymap = new HashMap<View, Double>();
        for(Map.Entry<View, View> entry : cases.entrySet()){
            Point2D.Double labelSize = entry.getKey().getSize(g);
            Point2D.Double subSize;
            ViewOption.Insets2D subMar;
            
            //サブビューが無い場合は０で初期化
            if(entry.getValue() == null){
                subSize = new Point2D.Double();
                subMar = new ViewOption.Insets2D();
            }
            else {
                subSize = entry.getValue().getSize(g);
                subMar = entry.getValue().getViewOption().getMargin();
            }
            
            //ラベルの最大幅を更新
            if(labelw < labelSize.x) labelw = labelSize.x;
            
            //サブビューの最大幅を更新
            if(subvieww < subSize.x - subMar.left - subMar.right) subvieww = subSize.x - subMar.left - subMar.right;
            
            //ラベルに合わせて高さを更新
            double uply, bottomly;
            if(count == 0){
                uply = 0.0;
                bottomly = labelSize.y;
            }
            else if(count == cases.size() - 1){
                uply = labelSize.y;
                bottomly = 0.0;
            }
            else{
                uply = bottomly = labelSize.y / 2;
            }
            if( lastdy < uply) lastdy += uply - lastdy;
            
            //ラベルが縦長い場合に調整
            double minldy = lastldy > uply ? (lastldy * 2) : (uply * 2);
            lastldy = bottomly;
            if(minldy > lastdy) lastdy = minldy;
            
            //高さを更新
            h += lastdy;
            ymap.put(entry.getKey(), new Double(h));

            //tmp <- 高さ追記分
            if( bottomly > subSize.y){
                lastdy = bottomly;
            }
            else{
                lastdy = subSize.y;
            }
            if(lastdy < minHeight && count < cases.size() - 1) lastdy = minHeight;
            
            count += 1;            
        }

        h += lastdy;

        //描画
        boolean first = true;
        Polygon poly = new Polygon();
        poly.addPoint((int)x, (int)y);
        double lasty = 0.0;
        double boxright = x + textSize.x + labelw + viewOption.getCaseWidth();
        for(Map.Entry<View, View> entry : cases.entrySet()){
            double ly = y + ymap.get(entry.getKey());
            if(entry.getValue() != null){
                entry.getValue().draw(g, new Point2D.Double(boxright + viewOption.getSubViewConnectWidth() - entry.getValue().getViewOption().getMargin().left, ly - entry.getValue().getViewOption().getMargin().top));

                viewOption.apply(g);
                g.draw(new Line2D.Double(boxright, ly, boxright + viewOption.getSubViewConnectWidth(), ly));
            }
            
            if(!first){
                poly.addPoint((int)(boxright - viewOption.getCaseWidth()), (int)((lasty + ly) / 2) );
            }
            poly.addPoint((int)(boxright), (int)ly );
            first = false;
            lasty = ly;
        }
        poly.addPoint((int)x, (int)lasty);

        viewOption.apply(g);
        g.setPaint(viewOption.getBackGroundPaint());
        g.fill(poly);
        
        int c = 0;
        for(Map.Entry<View, View> entry : cases.entrySet()){
            Point2D.Double ls = entry.getKey().getSize(g);
            double ly = y + ymap.get(entry.getKey());
            
            if(c >= cases.size() - 1){
                ly -= ls.y;
            }
            else if( c > 0){
                ly -= ls.y / 2;
            }
            c++;
            
            entry.getKey().draw(g, new Point2D.Double(x + textSize.x + labelw - ls.x, ly));
        }
        
        innerView.draw(g, new Point2D.Double(x, (y + lasty) / 2 - textSize.y / 2));
        
        viewOption.apply(g);
        g.draw(poly);
    }
}
