package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.Path2D;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3f;

import saf.v3d.AppearanceFactory;
import saf.v3d.Canvas2D;
import saf.v3d.CanvasListener;
import saf.v3d.ShapeFactory2D;
import saf.v3d.picking.PickEvent;
import saf.v3d.picking.PickListener;
import saf.v3d.scene.VLayer;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VShape;
import saf.v3d.scene.VSpatial;

public class TabbedPanelTests {

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
      // TODO Auto-generated method stub

    }

    @Override
    public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
      // TODO Auto-generated method stub

    }

    @Override
    public void init(GLAutoDrawable drawable, VRoot root) {
      this.drawable = drawable;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
      System.out.println("reshape " + (showCircle ? "circle" : "star"));
      if (!added) {
	VLayer layer = new VLayer();
	root.addChild(layer);
	if (showCircle) {
	  VSpatial circle = shapeFactory.createCircle(18, 16);
	  circle.putProperty("ID", "CIRCLE");
	  layer.addChild(circle);
	  circle.translate(50, 40, 0);
	  circle.setAppearance(AppearanceFactory.createColorAppearance(new Color(255, 0, 0, 128)));
	} else {

	  VShape jShape = shapeFactory.createShape(getStar());
	  jShape.putProperty("ID", "STAR SHAPE");
	  layer.addChild(jShape);
	  jShape.translate(100, 400, 0);
	  jShape.setBorderStrokeSize(5);
	  jShape.setBorderColor(Color.CYAN);
	}
	added = true;
      }
    }

    private Shape getStar() {
      float[] star = { 250f, 50f, 325f, 200f, 400f, 50f, 250f, 150f, 400f, 150f };
      Path2D.Float shape = new Path2D.Float(Path2D.WIND_EVEN_ODD);
      shape.moveTo(star[0], star[1]);
      shape.lineTo(star[2], star[3]);
      shape.lineTo(star[4], star[5]);
      shape.lineTo(star[6], star[7]);
      shape.lineTo(star[8], star[9]);
      shape.closePath();
      return shape;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
      // TODO Auto-generated method stub

    }
  }

  public void run() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    final JTabbedPane tabs = new JTabbedPane();
    frame.add(tabs, BorderLayout.CENTER);

    Canvas2D canvas = new Canvas2D();
    Display2D d2d = new Display2D(canvas.getShapeFactory());
    canvas.addCanvasListener(d2d);
    canvas.addPickListener(d2d);
    tabs.add("Display 1", canvas.getPanel());

    Canvas2D canvas2 = new Canvas2D();
    Display2D d2d2 = new Display2D(canvas2.getShapeFactory());
    d2d2.showCircle = true;
    canvas2.addCanvasListener(d2d2);
    canvas2.addPickListener(d2d2);
    
    tabs.add("Display 2", canvas2.getPanel());
    
    tabs.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	Component comp = tabs.getSelectedComponent();
	//Window window = SwingUtilities.getWindowAncestor(comp);
	//((GLCanvas)comp).getNativeSurface().surfaceSwap();
        
	
      }
    });

    frame.setSize(800, 800);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	new TabbedPanelTests().run();
      }
    });
  }

}
