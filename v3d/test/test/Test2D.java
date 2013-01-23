package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import saf.v3d.AppearanceFactory;
import saf.v3d.Canvas2D;
import saf.v3d.CanvasListener;
import saf.v3d.ShapeFactory2D;
import saf.v3d.grid.GridColorMap;
import saf.v3d.grid.GridMesh;
import saf.v3d.picking.PickEvent;
import saf.v3d.picking.PickListener;
import saf.v3d.render.DisplayListRenderer;
import saf.v3d.render.PolygonShape;
import saf.v3d.scene.Label;
import saf.v3d.scene.Position;
import saf.v3d.scene.TextureLayer;
import saf.v3d.scene.VComposite;
import saf.v3d.scene.VEdge2D;
import saf.v3d.scene.VEdgeLayer;
import saf.v3d.scene.VImage2D;
import saf.v3d.scene.VLabelLayer;
import saf.v3d.scene.VLayer;
import saf.v3d.scene.VNode;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VShape;
import saf.v3d.scene.VSpatial;

import com.jogamp.common.nio.Buffers;

public class Test2D {

  private static int EDGE_COUNT = 50;

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
    
    JButton button = new JButton("run");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if (d2d.colorMap.flag == 0)
          d2d.colorMap.flag = 1;
        else if (d2d.colorMap.flag == 1)
          d2d.colorMap.flag = 0;
        d2d.grid.update();
        canvas.update();
        /*
         * new Thread() { public void run() { d2d.run(); } }.start();
         */
      }
    });
    frame.add(buttonPanel, BorderLayout.NORTH);
    buttonPanel.add(button);
    
    button = new JButton("New Frame");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	JFrame aFrame = new JFrame();
	aFrame.setLayout(new BorderLayout());
	aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	aFrame.add(canvas.getPanel(), BorderLayout.CENTER);
	aFrame.setSize(800, 800);
	aFrame.setVisible(true);
      }
    });

    buttonPanel.add(button);
    frame.setSize(800, 800);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    new Test2D().run();
  }

  public static class Display2D implements CanvasListener, PickListener {

    boolean added = false;
    List<VSpatial> items = new ArrayList<VSpatial>();
    List<VEdge2D> edges = new ArrayList<VEdge2D>();
    GLAutoDrawable drawable;

    TestColorMap colorMap;
    GridMesh grid;
    ShapeFactory2D shapeFactory;
    
    public Display2D(ShapeFactory2D fac) {
      this.shapeFactory = fac;
    }

    @Override
    public void init(GLAutoDrawable drawable, VRoot root) {
      this.drawable = drawable;
    }

    public void run() {
      long start = new Date().getTime();
      for (int i = 0; i < 100; i++) {

        Random rnd = new Random();
        for (VSpatial spatial : items) {
          int x = (int) Math.round(Math.random() * 49);
          int y = (int) Math.round(Math.random() * 49);
          spatial.translate(x * 10 + 4, y * 10 + 4, 0);
        }

        VComposite root = edges.get(0).getParent();
        for (int j = 0; j < EDGE_COUNT; j++) {
          VEdge2D edge = edges.get(rnd.nextInt(edges.size()));
          root.removeChild(edge);
          edges.remove(edge);
        }

        for (int j = 0; j < EDGE_COUNT; j++) {
          int index = rnd.nextInt(items.size());
          VSpatial source = items.get(index);
          int tIndex = rnd.nextInt(items.size());
          while (index == tIndex) {
            tIndex = rnd.nextInt(items.size());
          }
          VSpatial target = items.get(tIndex);
          VEdge2D edge = new VEdge2D(source, target, true);
          edge.putProperty("ID", "EDGE " + j);
          root.addChild(edge);
          edges.add(edge);
        }

        /*
         * try { Thread.sleep(250); } catch (InterruptedException ex) { }
         */

        // if (i % 100 == 0 && i != 0)
        // System.out.println(i);
        drawable.display();
      }
      long delta = new Date().getTime() - start;
      delta /= 1000;
      System.out.printf("Elapsed time: %d seconds%n", delta);
      System.out.printf("DPS: %d%n", 1000 / delta);
    }

    private Shape createRectTri() {
      // float[] rect = { 50f, 50f, 200.0f, 50.0f, 200.0f, 200.0f, 50.0f, 200.0f
      // };
      float[] rect = { -75f, -75f, 75.0f, -75.0f, 75.0f, 75.0f, -75.0f, 75.0f };

      // float[] tri = { 75f, 75f, 125f, 175f, 175f, 75f };
      float[] tri = { -50f, -50f, 0f, 50f, 50f, -50f };

      Path2D.Float shape = new Path2D.Float(Path2D.WIND_EVEN_ODD);
      shape.moveTo(rect[0], rect[1]);
      shape.lineTo(rect[2], rect[3]);
      shape.lineTo(rect[4], rect[5]);
      shape.lineTo(rect[6], rect[7]);
      shape.closePath();

      shape.moveTo(tri[0], tri[1]);
      shape.lineTo(tri[2], tri[3]);
      shape.lineTo(tri[4], tri[5]);
      shape.closePath();

      return shape;
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

    private FloatBuffer getQuads() {
      float[] data = { 10, 10, 0, 20, 10, 0, 20, 50, 0, 10, 50, 0,

      20, 12, 0, 40, 12, 0, 40, 48, 0, 20, 48, 0 };

      FloatBuffer buf = Buffers.newDirectFloatBuffer(24);
      buf.put(data);
      return buf;
    }

    private FloatBuffer getQuadStrip() {
      float[] data = { 10, 10, 0, 10, 50, 0, 40, 10, 0, 45, 51, 0,

      60, 12, 0, 58, 60, 0, };

      FloatBuffer buf = Buffers.newDirectFloatBuffer(18);
      buf.put(data);
      return buf;
    }

    private Shape getLines() {
      Path2D.Float shape = new Path2D.Float();
      shape.moveTo(-5, -5);
      shape.lineTo(5, 5);
      shape.lineTo(15, 25);
      shape.moveTo(10, -5);
      shape.lineTo(-20, 20);

      return shape;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
      if (!added) {
        try {
          VLabelLayer layers = new VLabelLayer(new JLabel().getFont());
          VLayer layer = new VLayer();
          root.addChild(layer);
          colorMap = new TestColorMap();
          grid = new GridMesh(2, 3, 20, colorMap);
          VShape aGrid = new VShape(grid);
          layer.addChild(aGrid);
          aGrid.translate(0, 400, 0);

          DisplayListRenderer renderer = new DisplayListRenderer(getQuads(), GL2.GL_QUADS);
          VSpatial quads = new VShape(new PolygonShape(renderer));
          layer.addChild(quads);
          quads.translate(600, 600, 0);

          renderer = new DisplayListRenderer(getQuadStrip(), GL2.GL_QUAD_STRIP);
          quads = new VShape(new PolygonShape(renderer));
          layer.addChild(quads);
          quads.translate(400, 400, 0);

          VShape line = shapeFactory.createShape(getLines());
          line.translate(10, 10, 0);
          line.putProperty("ID", "LINES");
          layer.addChild(line);

          VShape jShape = shapeFactory.createShape(getStar());
          jShape.putProperty("ID", "STAR SHAPE");
          layer.addChild(jShape);
          jShape.translate(100, 400, 0);
          jShape.setBorderStrokeSize(5);
          jShape.setBorderColor(Color.CYAN);

          Shape shape = new Rectangle2D.Float(-10, -15, 20, 30);
          jShape = shapeFactory.createShape(shape);
          layer.addChild(jShape);
          jShape.translate(100, 100, 0);

          jShape = shapeFactory.createShape(createRectTri());
          layer.addChild(jShape);
          jShape.translate(200, 400, 0);
          jShape.rotate2D(45);
          
          Position position = Position.SOUTH_WEST;

          VSpatial circle = shapeFactory.createCircle(18, 16);
          circle.putProperty("ID", "CIRCLE");
          layer.addChild(circle);
          circle.translate(50, 40, 0);
          circle.setAppearance(AppearanceFactory.createColorAppearance(new Color(255, 0, 0, 128)));
          layers.addLabel(new Label("Circle", circle, position));
         
          VNode compNode = new VNode(shapeFactory.createRectangle(40, 40));
          Color red = new Color(255, 0, 0, 128);
          compNode.setAppearance(AppearanceFactory.createColorAppearance(red));
          VSpatial child = shapeFactory.createCircle(10, 16);
          compNode.addChild(child);
          child.translate(30, 30, 0);
          layer.addChild(compNode);
          compNode.scale(2);
          compNode.translate(200, 600, 0);
          compNode.putProperty("ID", "COMP_NODE");

          TextureLayer textureLayer = new TextureLayer();
          String path = "./test/test/bookmark.png";
          VImage2D sprite = shapeFactory.createImage(path);
          sprite.putProperty("ID", "STAR 1");
          sprite.scale(2);
          sprite.translate(300, 300, 0);
          textureLayer.addChild(sprite);
          layers.addLabel(new Label("Star 1", sprite, position));

          VImage2D sprite2 = shapeFactory.createImage(path, .5f);
          sprite2.putProperty("ID", "STAR 2");
          sprite2.translate(500, 100, 0);
          textureLayer.addChild(sprite2);
          layers.addLabel(new Label("Star 2", sprite2, position));
          
          VImage2D lockImg = shapeFactory.createImage("./test/test/lock.png");
          lockImg.putProperty("ID", "LOCK");
          lockImg.translate(40, 30, 0);
          textureLayer.addChild(lockImg);
          
          root.addChild(textureLayer);
          
          VEdgeLayer edgeLayer = new VEdgeLayer();

          VEdge2D edge = new VEdge2D(sprite, sprite2, true);
          edge.putProperty("ID", "EDGE");
          edgeLayer.addChild(edge);

          edge = new VEdge2D(circle, jShape, true);
          edgeLayer.addChild(edge);

          edge = new VEdge2D(compNode, circle, true);
          edge.setAppearance(AppearanceFactory.createColorAppearance(Color.green));
          edgeLayer.addChild(edge);
          
          layer.addChild(edgeLayer);
          root.addChild(layers);

          /*
           * VSpatial circle1 = shapeFactory.createCircle(18,
           * 16); circle1.putProperty("ID", "CIRCLE 1"); root.addChild(circle1);
           * circle1.translate(50, 650, 0);
           * 
           * VEdge2D edge1 = new VEdge2D(circle, circle1, true);
           * root.addChild(edge1);
           * 
           * VEdge2D edge2 = new VEdge2D(sprite, circle1, true);
           * edge2.setAppearance
           * (AppearanceFactory.createColorAppearance(Color.GREEN));
           * root.addChild(edge1); root.addChild(edge); root.addChild(edge2);
           */

        } catch (IOException ex) {
          ex.printStackTrace();
        }

        /*
         * VSpatial circle = shapeFactory.createCircle(10, 16);
         * circle.putProperty("ID", 0);
         * circle.setAppearance(AppearanceFactory.createColorAppearance
         * (Color.RED)); circle.translate(40, 700, 0); root.addChild(circle);
         * 
         * circle = shapeFactory.createCircle(10, 16);
         * circle.putProperty("ID", 1);
         * circle.setAppearance(AppearanceFactory.createColorAppearance
         * (Color.BLUE)); circle.translate(100, 380, 0); root.addChild(circle);
         */

        Color color = Color.RED;
        for (int i = 0; i < 3000; i++) {
          if (i == 1000)
            color = Color.BLUE;
          if (i == 2000)
            color = Color.GREEN;

          int x = (int) Math.round(Math.random() * 49);
          int y = (int) Math.round(Math.random() * 49);
          VShape circle = shapeFactory.createCircle(4, 16);
          circle.setBorderStrokeSize(2);
          circle.putProperty("ID", i);
          // VLabel label =
          // shapeFactory.createLabel(String.valueOf(i));
          // label.translate(5, 5, 0);
          // node.addChild(label);

          circle.setAppearance(AppearanceFactory.createColorAppearance(color));
          circle.translate(x * 10 + 4, y * 10 + 4, 0);
          // root.addChild(circle);
          items.add(circle);
        }

        Random rnd = new Random();
        for (int i = 0; i < EDGE_COUNT; i++) {
          int index = rnd.nextInt(items.size());
          VSpatial source = items.get(index);
          int tIndex = rnd.nextInt(items.size());
          while (index == tIndex) {
            tIndex = rnd.nextInt(items.size());
          }
          VSpatial target = items.get(tIndex);
          VEdge2D edge = new VEdge2D(source, target, true);
          edge.putProperty("ID", "EDGE " + i);
          // root.addChild(edge);
          edges.add(edge);
        }

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

  public static class TestColorMap implements GridColorMap {

    int flag = 0;

    /*
     * (non-Javadoc)
     * 
     * @see saf.v3d.GridColorMap#getColor(int, int, javax.vecmath.Color3f)
     */
    @Override
    public void getColor(int x, int y, Color3f color) {
      System.out.printf("x: %d, y: %d%n", x, y);
      if (flag == 0) {
        if (y == 0) {
          if (x == 0)
            color.set(Color.RED);
          else if (x == 1)
            color.set(Color.GREEN);
          else if (x == 2)
            color.set(Color.BLUE);
          else
            color.set(Color.GRAY);
        } else {
          if (x == 0)
            color.set(Color.BLUE);
          else if (x == 1)
            color.set(Color.PINK);
          else if (x == 2)
            color.set(Color.CYAN);
          else
            color.set(Color.GRAY);
        }
      } else {
        if (y == 1) {
          if (x == 0)
            color.set(Color.RED);
          else if (x == 1)
            color.set(Color.GREEN);
          else if (x == 2)
            color.set(Color.BLUE);
          else
            color.set(Color.GRAY);
        } else {
          if (x == 0)
            color.set(Color.BLUE);
          else if (x == 1)
            color.set(Color.PINK);
          else if (x == 2)
            color.set(Color.CYAN);
          else
            color.set(Color.GRAY);

        }
      }
    }
  }
}
