package test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jogamp.vecmath.Point3f;

import saf.v3d.Canvas2D;
import saf.v3d.CanvasListener;
import saf.v3d.ShapeFactory2D;
import saf.v3d.picking.PickEvent;
import saf.v3d.picking.PickListener;
import saf.v3d.scene.Label;
import saf.v3d.scene.Position;
import saf.v3d.scene.VLabelLayer;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VSpatial;

public class DynamicTest2D {

  public void run() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    final Canvas2D canvas = new Canvas2D();
    final Display2D d2d = new Display2D(canvas.getShapeFactory());
    canvas.addCanvasListener(d2d);
    canvas.addPickListener(d2d);
    frame.add(canvas.getPanel(), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    frame.add(buttonPanel, BorderLayout.NORTH);

    JButton button = new JButton("run");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Date start = new Date();
        for (int i = 0; i < 400; i++) {
          d2d.run();
          canvas.update();
        }
        Date end = new Date();
        double seconds = (end.getTime() - start.getTime()) / 1000.0;
        System.out.println(seconds);
      }
    });
    buttonPanel.add(button);
    frame.setSize(800, 800);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new DynamicTest2D().run();
      }
    });
  }

  public static class Display2D implements CanvasListener, PickListener {

    GLAutoDrawable drawable;
    boolean added = false;
    ShapeFactory2D shapeFactory;
    
    public Display2D(ShapeFactory2D shapeFactory) {
      this.shapeFactory = shapeFactory;
    }

    List<VSpatial> spatials = new ArrayList<VSpatial>();

    public void run() {
      int width = drawable.getSurfaceWidth();
      int height = drawable.getSurfaceHeight();
      for (VSpatial spatial : spatials) {
        spatial.translate((float) (Math.random() * width), (float) (Math.random() * height), 0);
      }
    }

    @Override
    public void init(GLAutoDrawable drawable, VRoot root) {
      this.drawable = drawable;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
      if (!added) {

        VLabelLayer layers = new VLabelLayer(new JLabel().getFont());
        Position position = Position.SOUTH;

        for (int i = 0; i < 400; i++) {
          VSpatial circle = shapeFactory.createCircle(4, 16);
          circle.putProperty("ID", "CIRCLE " + i);
          circle.scale(4f);
          root.addChild(circle);

          circle.translate((float) (Math.random() * width), (float) (Math.random() * height), 0);
          layers.addLabel(new Label("Circle " + i, circle, position));
          spatials.add(circle);
        }

        root.addChild(layers);
        added = true;
      }
    }

    @Override
    public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * repast.simphony.v3d.PickListener#pickPerformed(repast.simphony.v3d.PickEvent
     * )
     */
    @Override
    public void pickPerformed(PickEvent evt) {
      System.out.println(evt);
      for (VSpatial node : evt.getPicked()) {
        System.out.println(node.getProperty("ID"));
      }

    }

  }
}
