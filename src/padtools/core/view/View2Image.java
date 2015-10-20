package padtools.core.view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


/**
 * Created with IntelliJ IDEA.
 * User: monaou
 * Date: 12/07/17
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class View2Image {
    private View2Image(){}

    public static BufferedImage toImage(View view, double scale){
        BufferedImage bi = new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
        Point2D.Double size = view.getSize(bi.createGraphics());

        int w = (int)Math.ceil(size.getX() * scale);
        int h = (int)Math.ceil(size.getY() * scale);
        BufferedImage out = new BufferedImage(w,h, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = out.createGraphics();
        g.setTransform(AffineTransform.getScaleInstance(scale, scale));
        g.setPaint(Color.WHITE);
        g.fillRect(0,0,w,h);
        view.draw(g, new Point2D.Double(0,0));

        return out;
    }
}
