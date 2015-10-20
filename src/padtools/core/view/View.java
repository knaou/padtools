package padtools.core.view;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * 描画するオブジェクトの基礎になるクラス
 * @author monaou
 */
public abstract class View {
    private ViewOption viewOption;
    private Object tag = null;
    
    public View(ViewOption viewOption){
        this.viewOption = viewOption;
    }
    
    public ViewOption getViewOption(){
        return viewOption;
    }
    
    public void setViewOption(ViewOption viewOption){
        this.viewOption = viewOption;
    }
    
    public Object getTag(){
        return tag;
    }
    
    public void setTag(Object tag){
        this.tag = tag;
    }
    
    public final Point2D.Double getSize(Graphics2D g){
        return getSize(g, viewOption);
    }
    
    public final Point2D.Double getInnerSize(Graphics2D g){
        return getInnerSize(g, viewOption);
    }
    
    public final void draw(Graphics2D g, Point2D.Double p){
        draw(g, p, viewOption);
    }
    
    /**
     * 描画する矩形を取得する。
     * @param g グラフィックオブジェクト
     * @return サイズ
     */
    public abstract Point2D.Double getSize(Graphics2D g, ViewOption viewOption);
    
    /**
     * 描画する矩形を取得する（サブノードを含めいない）。
     * @param g2
     * @return 
     */
    public Point2D.Double getInnerSize(Graphics2D g, ViewOption viewOption){
        return getSize(g, viewOption);
    }
    
    /**
     * 描画する。
     * @param g グラフィックオブジェクト
     * @param p 座標
     */
    public abstract void draw(Graphics2D g, Point2D.Double p, ViewOption viewOption);
}
