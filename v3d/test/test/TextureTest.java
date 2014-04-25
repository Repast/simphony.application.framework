/**
 * 
 */
package test;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.vecmath.Point3f;

import saf.v3d.Canvas2D;
import saf.v3d.CanvasListener;
import saf.v3d.ShapeFactory2D;
import saf.v3d.picking.PickEvent;
import saf.v3d.picking.PickListener;
import saf.v3d.scene.TextureLayer;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VSpatial;

/**
 * @author Nick Collier
 */
public class TextureTest {
  
  public static class Display2D implements CanvasListener, PickListener {

    ShapeFactory2D shapeFactory;
    GLAutoDrawable drawable;
    boolean added = false;
    boolean showCircle = false;

    public Display2D(ShapeFactory2D fac) {
      this.shapeFactory = fac;
    }

    @Override
    public void pickPerformed(PickEvent evt) {
    }

    @Override
    public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
    }

    @Override
    public void init(GLAutoDrawable drawable, VRoot root) {
      this.drawable = drawable;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
      try {
	    TextureLayer texLayer = new TextureLayer();
	    root.addChild(texLayer);
	   
	    BufferedImage img = ImageIO.read(new File("./test/test/zombie.png"));
	    shapeFactory.registerImage("zombie", img);

	    VSpatial zombie = shapeFactory.getNamedSpatial("zombie");
	    texLayer.addChild(zombie);
	    zombie.translate(10, 10, 0);
	
	  } catch (IOException ex) {
	    ex.printStackTrace();
	  }
      
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
  }
  
  public void run() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());

    Canvas2D canvas = new Canvas2D();
    Display2D d2d = new Display2D(canvas.getShapeFactory());
    canvas.addCanvasListener(d2d);
    canvas.addPickListener(d2d);
    frame.add(canvas.getPanel(), BorderLayout.CENTER);

    frame.setSize(400, 400);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	new TextureTest().run();
      }
    });
  }

}
