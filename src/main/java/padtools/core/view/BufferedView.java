package padtools.core.view;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

/**
 *
 * @author monaou
 */
public class BufferedView extends View {
    private final View view;
    private final boolean doBufferImage;
    private BufferedImage imageBuffer = null;
    private Point2D.Double size = null;
    private Point2D.Double innerSize = null;
    
    public BufferedView(View view, boolean doBufferImage){
        super(view.getViewOption());
        this.view = view;
        this.doBufferImage = doBufferImage;
    }

    @Override
    public Double getInnerSize(Graphics2D g, ViewOption viewOption) {
        if(innerSize != null){
            return innerSize;
        }
        
        innerSize = view.getInnerSize(g);
        return innerSize;
    }
    
    public void resetBuffer(){
        imageBuffer = null;
        size = null;
        innerSize = null;
    }

    @Override
    public Double getSize(Graphics2D g, ViewOption viewOption) {
        if(size != null){
            return size;
        }
        
        size = view.getSize(g);
        return size;
    }
    
    public View getView(){
        return view;
    }

    @Override
    public void draw(Graphics2D g, Double p, ViewOption viewOption) {
        if(doBufferImage) {
            if(imageBuffer == null){
                Point2D.Double size = getSize(g);
                imageBuffer = new BufferedImage((int)size.x, (int)size.y, BufferedImage.TYPE_4BYTE_ABGR);
                view.draw(imageBuffer.createGraphics(), new Double());
            }
            g.drawImage(imageBuffer, (int)p.x, (int)p.y, null);
        }
        else {
            view.draw(g, p);
        }
        
    }
}
