package padtools.core.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.Serializable;

/**
 * 描画時の設定を保持するクラス。
 * @author monaou
 */
public class ViewOption implements Serializable{
    public static class Insets2D implements Serializable{
        public double top = 0.0;
        public double left = 0.0;
        public double right = 0.0;
        public double bottom = 0.0;
        
        public Insets2D(){}
        public Insets2D(double top, double left, double bottom, double right){
            this.left = left; this.right = right; this.top = top; this.bottom = bottom;
        }
    }
    
    private ViewOption parent = null;
    
    private Font font = null;
    private Double barWidthInBox = null;
    private Double subViewConnectWidth = null;
    private Double caseWidth = null;
    private Double caseSubViewMinHeight = null;
    private Double roundLong = null;
    private Double roundShort = null;
    private Insets2D margin = null;
    private Paint paint = null;
    private Paint backgroundPaint = null;
    private Stroke stroke = null;
    private Boolean antialiasing = null;
    private Boolean textAntialiasing = null;
    
    public ViewOption(){
        font = new Font("Dialog", Font.PLAIN, 14);
        barWidthInBox = 10.0;
        subViewConnectWidth = 15.0;
        caseWidth = 40.0;
        caseSubViewMinHeight = 36.0;
        roundLong = 48.0;
        roundShort = 32.0;
        margin = new Insets2D(5,15,5,15);
        paint = Color.BLACK;
        backgroundPaint = Color.WHITE;
        stroke = new BasicStroke(2.0f);
        antialiasing = true;
        textAntialiasing = true;
    }
    
    public ViewOption(ViewOption parent){
        this.parent = parent;
    }
    
    public void apply(Graphics2D g){
        g.setFont(getFont());
        g.setStroke(getStroke());
        g.setPaint(getPaint());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                getAntialiasing() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                getTextAntialiasing() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }
    
    public ViewOption getParent(){
        return parent;
    }
    
    public void setParent(ViewOption viewOption){
        this.parent = viewOption;
    }
    
    public Font getFont(){
        if(font == null && parent != null){
            return parent.getFont();
        }
        
        return font;
    }
    
    public void setFont(Font defaultFont){
        this.font = defaultFont;
    }
    
    public Double getBarWidthInBox(){
        if(barWidthInBox == null && parent != null){
            return parent.getBarWidthInBox();
        }
        
        return barWidthInBox;
    }
    
    public void setBarWidthInBox(Double width){
        this.barWidthInBox = width;
    }
    
    public Double getSubViewConnectWidth(){
        if(subViewConnectWidth == null && parent != null){
            return parent.getSubViewConnectWidth();
        }
        
        return subViewConnectWidth;
    }
    
    public void setSubViewConnectWidth(Double width){
        subViewConnectWidth = width;
    }
    
    public Double getCaseWidth(){
        if(caseWidth == null && parent != null){
            return parent.getCaseWidth();
        }
        
        return caseWidth;
    }
    
    public void setCaseWidth(Double width){
        this.caseWidth = width;
    }
    
    public Double getCaseSubViewMinHeight(){
        if(caseSubViewMinHeight == null && parent != null){
            return parent.getCaseSubViewMinHeight();
        }
        
        return caseSubViewMinHeight;
    }
    
    public void setCaseSubViewMinHeight(Double height){
        this.caseSubViewMinHeight = height;
    }
    
    public Insets2D getMargin(){
        if(margin == null && parent != null){
            return parent.getMargin();
        }
        
        return margin;
    }
    
    public void setMargin(Insets2D margin){
        this.margin = margin;
    }
    
    public Paint getPaint(){
        if(paint == null && parent != null){
            return parent.getPaint();
        }
        
        return paint;
    }
    
    public void setPaint(Paint paint){
        this.paint = paint;
    }
    
    public Paint getBackGroundPaint(){
        if(backgroundPaint == null && parent != null){
            return parent.getBackGroundPaint();
        }
        
        return backgroundPaint;
    }
    
    public void setBackGroundPaint(Paint paint){
        this.backgroundPaint = paint;
    }
    
    public Stroke getStroke(){
        if(stroke == null && parent != null){
            return parent.getStroke();
        }
        
        return stroke;
    }
    
    public void setStroke(Stroke stroke){
        this.stroke = stroke;
    }

    public Boolean getAntialiasing(){
        if(antialiasing == null && parent != null){
            return parent.getAntialiasing();
        }
        
        return antialiasing;
    }
    
    public void setAntialiasing(Boolean antialiasing){
        this.antialiasing = antialiasing;
    }
    
    public Boolean getTextAntialiasing(){
        if(textAntialiasing == null && parent != null){
            return parent.getTextAntialiasing();
        }
        
        return textAntialiasing;
    }
    
    public Double getRoundLong() {
        if(roundLong == null && parent != null){
            return parent.getRoundLong();
        }
        return roundLong;
    }
    
    public void setRoundLong(Double roundLong){
        this.roundLong = roundLong;
    }
    
    public Double getRoundShort() {
        if(roundShort == null && parent != null){
            return parent.getRoundShort();
        }
        return roundShort;
    }

    public void setRoundShort(Double roundShort){
        this.roundShort = roundShort;
    }
}
