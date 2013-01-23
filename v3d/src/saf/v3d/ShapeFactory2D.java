/**
 * 
 */
package saf.v3d;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point3f;

import saf.v3d.render.DisplayListRenderer;
import saf.v3d.render.LineListRenderer;
import saf.v3d.render.Renderer;
import saf.v3d.render.Texture2D;
import saf.v3d.scene.VImage2D;
import saf.v3d.scene.VNode;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VShape;
import saf.v3d.scene.VSpatial;
import saf.v3d.util.BezierCurveTessellator;
import saf.v3d.util.Tessellator;

import com.jogamp.common.nio.Buffers;

/**
 * Factory for generation VNodes for particular shapes.
 * 
 * @author Nick Collier
 */
public class ShapeFactory2D implements CanvasListener {

  private Map<String, Texture2D> textureCache = new HashMap<String, Texture2D>();
  private Map<Object, Renderer> rendererCache = new HashMap<Object, Renderer>();
  private Map<String, ShapeCreator> namedShapes = new HashMap<String, ShapeCreator>();

  private Tessellator tessellator;

  public ShapeFactory2D() {
    tessellator = new Tessellator();
  }
  
  /**
   * Invalidates the cached renderers.
   * 
   * @param gl
   */
  public void invalidateRenderers(GL2 gl) {
    for (Renderer renderer : rendererCache.values()) {
      renderer.dispose(gl);
    }
    
    for (Renderer renderer : rendererCache.values()) {
      renderer.invalidate();
    }
  }

  /**
   * Gets a NamedShapeCreator that will create the specified named shape.
   */
  public NamedShapeCreator createNamedShape(String name) {
    return new ShapeCreator(name, this);
  }

  /**
   * Registers the BufferedImage using the specified name,
   * as a "NamedSpatial." VSpatials that display this image
   * can then be retrieved with getNamedSpatial.
   *  
   * 
   * @param name the name to register the image under
   * @param img the image 
   */
  public void registerImage(String name, BufferedImage img) {
    registerImage(name, img, 1);
  }

  /**
   * Registers the BufferedImage using the specified name,
   * as a "NamedSpatial." VSpatials that display this image
   * can then be retrieved with getNamedSpatial.
   *  
   * 
   * @param name the name to register the image under
   * @param img the image 
   * @param scale the default scale of the image
   */
  public void registerImage(String name, BufferedImage img, float scale) {
    namedShapes.remove(name);
    Texture2D data = textureCache.get(name);
    if (data == null) {
      data = new Texture2D(img, scale);
      textureCache.put(name, data);
    }
  }

  /**
   * Registers the image at the specified path using the specified name.
   * The image is registered as a "NamedSpatial." VSpatials that display 
   * this image can then be retrieved with getNamedSpatial.
   *  
   * 
   * @param name the name to register the image under
   * @param img the image 
   */
  public void registerImage(String name, String path) throws IOException {
    registerImage(name, path, 1);
  }

  /**
   * Registers the image at the specified path using the specified name.
   * The image is registered as a "NamedSpatial." VSpatials that display 
   * this image can then be retrieved with getNamedSpatial.
   *  
   * 
   * @param name the name to register the image under
   * @param img the image 
   * @param scale the default scale of the image
   */
  public void registerImage(String name, String path, float scale) throws IOException {
    namedShapes.remove(name);
    Texture2D data = textureCache.get(name);
    if (data == null) {
      data = new Texture2D(path, scale);
      textureCache.put(name, data);
    }
  }

  /**
   * Gets whether or not any "named spatials" have been registered under
   * the specified name.
   * 
   * @param name the name to check
   * @return true if any "named spatials" have been registered under
   * the specified name, otherwise false.
   */
  public boolean isNameRegistered(String name) {
    return namedShapes.containsKey(name) || textureCache.containsKey(name);
  }

  /**
   * Creates a VSpatial for images, shapes etc. registered under the
   * specified name.
   * 
   * @param name the name the image, shape etc. was registered under.
   * 
   * @return the created VSpatial.
   */
  public VSpatial getNamedSpatial(String name) {
    if (namedShapes.containsKey(name))
      return getNamedShape(name);
    if (textureCache.containsKey(name))
      return new VImage2D(textureCache.get(name));
    return null;
  }

  /**
   * Gets the specified named shape.
   * 
   * @param name
   *          the name of the shape to get
   * 
   * @return the named shape.
   */
  private VNode getNamedShape(String name) {
    ShapeCreator creator = namedShapes.get(name);
    return creator.createShape();
  }

  private void registerNamedShape(String name, ShapeCreator creator) {
    textureCache.remove(name);
    namedShapes.put(name, creator);
  }

  /**
   * Creates a VImage2D using the image at the specified path.
   * 
   * @param path
   *          the path to the image
   * @return the created VImage2D
   * @throws IOException
   *           if the image file cannot be found or read
   */
  public VImage2D createImage(String path) throws IOException {
    return createImage(path, 1f);
  }

  /**
   * Creates a VImage2D using the image at the specified path.
   * 
   * @param path
   *          the path to the image
   * @param scale
   *          the amount to scale the image by default
   * 
   * @return the created VImage2D
   * @throws IOException
   *           if the image file cannot be found or read
   */
  public VImage2D createImage(String path, float scale) throws IOException {
    String key = path + scale;
    Texture2D data = textureCache.get(key);
    if (data == null) {
      data = new Texture2D(path, scale);
      textureCache.put(key, data);
    }

    return new VImage2D(data);
  }

  /**
   * Creates a VImage2D from the specified BufferedImage. 
   * Images are cached so use the same id to reuse the
   * image data.
   * 
   * @param id the id of the image.
   * 
   * @return the created VImage2D
   * @throws IOException
   *           if the image file cannot be found or read
   */
  public VImage2D createImage(String id, BufferedImage img) {
    Texture2D data = textureCache.get(id);
    if (data == null) {
      data = new Texture2D(img);
      textureCache.put(id, data);
    }
    return new VImage2D(data);
  }

  /**
   * Creates a rectangle of the specified width and height centered around 0, 0
   * 
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the created rectangle.
   */
  public VShape createRectangle(int width, int height) {
    return createRectangle(width, height, true);
  }

  /**
   * Creates a rectangle of the specified width and height centered around 0, 0
   * 
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the created rectangle.
   */
  public VShape createRectangle(int width, int height, boolean colorUpdatable) {
    return createShape(new Rectangle2D.Float(-width / 2f, -height / 2f, width, height),
        colorUpdatable);
  }

  private boolean isClosed(Path2D path) {
    float[] coords = new float[6];
    int ret = PathIterator.SEG_CUBICTO;
    for (PathIterator iter = path.getPathIterator(null); !iter.isDone(); iter.next()) {
      ret = iter.currentSegment(coords);
    }
    return ret == PathIterator.SEG_CLOSE;
  }

  private LineListRenderer createLineRenderer(Path2D path) {
    // creates a FloatBuffer holding the points, and
    // indices into that buffer where path has done a Move To.
    // renderer should do a new GL.GL_LINE_STRIP where that occurs
    List<Float> vertices = new ArrayList<Float>();
    List<Integer> moveToIndices = new ArrayList<Integer>();
    float[] coords = new float[6];
    int vertIndex = 0;
    for (PathIterator iter = path.getPathIterator(null); !iter.isDone(); iter.next()) {
      int ret = iter.currentSegment(coords);

      if (ret == PathIterator.SEG_MOVETO || ret == PathIterator.SEG_LINETO) {
        if (ret == PathIterator.SEG_MOVETO)
          moveToIndices.add(vertIndex);
        vertices.add(coords[0]);
        vertices.add(coords[1]);
        vertices.add(0f);
        vertIndex += 3;
      } else if (ret == PathIterator.SEG_CUBICTO) {
        int vertsSize = vertices.size();
        Point3f a = new Point3f(vertices.get(vertsSize - 3), vertices.get(vertsSize - 2), 0);
        Point3f b = new Point3f(coords[0], coords[1], 0);
        Point3f c = new Point3f(coords[2], coords[3], 0);
        Point3f d = new Point3f(coords[4], coords[5], 0);
        float[] verts = new BezierCurveTessellator(a, b, c, d).tessellate(20);
        for (int i = 0, n = verts.length; i < n; i += 2) {
          vertices.add(verts[i]);
          vertices.add(verts[i + 1]);
          vertices.add(0f);
        }
        vertices.add(d.x);
        vertices.add(d.y);
        vertices.add(0f);
      } else if (ret != PathIterator.SEG_CLOSE) {
        // we want to ignore seg close because sometimes we want to treat a
        // polygon
        // like a line (e.g. a rect with no width or height)
        throw new IllegalArgumentException("Quadratic curves are not yet supported.");
      }
    }

    FloatBuffer buf = Buffers.newDirectFloatBuffer(vertices.size());
    for (Float val : vertices) {
      buf.put(val);
    }

    int[] moveTos = new int[moveToIndices.size()];
    int i = 0;
    for (Integer val : moveToIndices) {
      moveTos[i++] = val;
    }

    return new LineListRenderer(buf, moveTos);
  }

  /**
   * Creats a VSpatial from a java awt shape.
   * 
   * @param shape
   *          the shape to create the VSpatial for
   * 
   * @return the created VSpatial.
   */
  public VShape createShape(java.awt.Shape shape) {
    return createShape(shape, true);
  }

  /**
   * Creats a VSpatial from a java awt shape.
   * 
   * @param shape
   *          the shape to create the VSpatial for
   * @param colorUpdateable
   *          whether or not the color of this shape can be changed after the
   *          first set.
   * 
   * @return the created VSpatial.
   */
  public VShape createShape(java.awt.Shape shape, boolean colorUpdateable) {
    return new VShape(createRenderer(shape).createShape(), colorUpdateable);
  }

  private Renderer createRenderer(java.awt.Shape shape) {
    Renderer renderer = rendererCache.get(shape);
    if (renderer == null) {
      Path2D.Float path = new Path2D.Float(shape);
      Rectangle2D bounds = path.getBounds2D();
      if (bounds.getWidth() == 0 || bounds.getHeight() == 0) {
        // rectangles with no width or height need to be treated as lines
        renderer = createLineRenderer(path);
      } else if (isClosed(path)) {
        renderer = tessellator.createRenderer(path);
      } else {
        renderer = createLineRenderer(path);
      }

      if (renderer.getVertices().limit() == 0) {
        renderer = createLineRenderer(path);
      }

      rendererCache.put(shape, renderer);
    }

    return renderer;
  }

  private Renderer createLineRenderer(java.awt.Shape shape) {
    Renderer renderer = rendererCache.get(shape);
    if (renderer == null) {
      renderer = createLineRenderer(new Path2D.Float(shape));
      rendererCache.put(shape, renderer);
    }

    return renderer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.ui.v3d.CanvasListener#dispose(javax.media.opengl.GLAutoDrawable)
   */
  @Override
  public void dispose(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    for (Texture2D texture : textureCache.values()) {
      texture.dispose(gl);
    }
    textureCache.clear();
    
    for (Renderer renderer : rendererCache.values()) {
      renderer.dispose(gl);
    }
    rendererCache.clear();

    namedShapes.clear();
  }

  private FloatBuffer createCircleGeom(float radius, int slices) {
    final int count = (slices + 2) * 3;
    FloatBuffer buf = Buffers.newDirectFloatBuffer(count);
    // first vertex at 0
    buf.put(0);
    buf.put(0);
    buf.put(0);
    float interval = (float) (Math.PI * 2.0 / slices);
    // counter clockwise
    for (int s = slices; s >= 0; s--) {
      float angle = 0f;
      if (s != slices)
        angle = s * interval;
      float sa = (float) Math.sin(angle);
      float ca = (float) Math.cos(angle);
      buf.put(sa * radius);
      buf.put(ca * radius);
      buf.put(0f);
    }
    buf.rewind();
    return buf;
  }

  /**
   * Creates a circle VShape with the specified radius and the specified number
   * of slices. The number of slices is the number of "pie" shaped slices to
   * create the circle out of.
   * 
   * @param radius
   *          the radius of the circle
   * @param slices
   *          the number of slices to create the circle out of
   * 
   * @return the created circle
   */
  public VShape createCircle(float radius, int slices) {
    return createCircle(radius, slices, true);
  }

  /**
   * Creates a circle VShape with the specified radius and the specified number
   * of slices. The number of slices is the number of "pie" shaped slices to
   * create the circle out of.
   * 
   * @param radius
   *          the radius of the circle
   * @param slices
   *          the number of slices to create the circle out of
   * @param colorUpdatable
   *          whether or not the color of this circle is updatable once it has
   *          been set
   * 
   * @return the created circle
   */
  public VShape createCircle(float radius, int slices, boolean colorUpdatable) {
    String key = "circle:" + radius + ":" + slices;

    Renderer renderer = rendererCache.get(key);
    if (renderer == null) {
      FloatBuffer buf = createCircleGeom(radius, slices);
      renderer = new DisplayListRenderer(buf, GL.GL_TRIANGLE_FAN);
      rendererCache.put(key, renderer);
    }

    return new VShape(renderer.createShape(), colorUpdatable);
  }

  @Override
  public void init(GLAutoDrawable drawable, VRoot root) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.CanvasListener#reshape(javax.media.opengl.GLAutoDrawable,
   * float, float, saf.v3d.scene.VRoot)
   */
  @Override
  public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.CanvasListener#vSpatialMoved(saf.v3d.scene.VSpatial,
   * javax.vecmath.Point3f)
   */
  @Override
  public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
  }

  private static class ShapeCreator implements NamedShapeCreator {

    private static class ShapeProperties {
      java.awt.Shape shape;
      Color color;
      boolean isLine;
      boolean colorUpdatable;

      public ShapeProperties(Shape shape, Color color, boolean isLine, boolean colorUpdatable) {
        this.shape = shape;
        this.color = color;
        this.isLine = isLine;
        this.colorUpdatable = colorUpdatable;
      }
    }

    private static class VShapeProperties {
      Renderer renderer;
      Color color;
      boolean colorUpdateable;

      public VShapeProperties(Renderer renderer, Color color, boolean colorUpdateable) {
        this.renderer = renderer;
        this.color = color;
        this.colorUpdateable = colorUpdateable;
      }
    }

    private String name;
    private List<ShapeProperties> shapes = new ArrayList<ShapeProperties>();
    private List<VShapeProperties> vShapeProps = new ArrayList<VShapeProperties>();
    private ShapeFactory2D shapeFactory;

    public ShapeCreator(String name, ShapeFactory2D factory) {
      this.name = name;
      this.shapeFactory = factory;
    }

    VNode createShape() {
      VNode node = null;
      for (VShapeProperties data : vShapeProps) {
        VShape shape = new VShape(data.renderer.createShape(), data.colorUpdateable);
        shape.setAppearance(AppearanceFactory.createColorAppearance(data.color));
        if (node == null) {
          node = new VNode(shape);
        } else {
          node.addChild(shape);
        }
      }

      return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see saf.v3d.NamedShapeCreator#addShape(java.awt.Shape, java.awt.Color)
     */
    @Override
    public void addShape(Shape shape, Color color, boolean canUpdateColor) {
      Rectangle2D bounds = shape.getBounds2D();
      // exclude illegal neg. dimension shapes
      if ((bounds.getWidth() == 0 && bounds.getHeight() == 0)
          || (bounds.getWidth() < 0 || bounds.getHeight() < 0)) {
        // System.out.printf("Invalid shape dimension: %s %s%n", name, bounds);
      } else {
        shapes.add(new ShapeProperties(shape, color, false, canUpdateColor));
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see saf.v3d.NamedShapeCreator#addLine(java.awt.Shape, java.awt.Color)
     */
    @Override
    public void addLine(Shape shape, Color color, boolean canUpdateColor) {
      shapes.add(new ShapeProperties(shape, color, true, canUpdateColor));

    }

    /*
     * (non-Javadoc)
     * 
     * @see saf.v3d.NamedShapeCreator#createShape()
     */
    @Override
    public void registerShape() {
      Rectangle2D.Float bounds = null;
      for (ShapeProperties shapeData : shapes) {
        if (bounds == null) {
          bounds = new Rectangle2D.Float();
          bounds.setRect(shapeData.shape.getBounds2D());
        }
        bounds.add(shapeData.shape.getBounds2D());
      }

      AffineTransform trans = AffineTransform.getTranslateInstance(-bounds.getCenterX(), -bounds
          .getCenterY());
      for (ShapeProperties shapeData : shapes) {
        java.awt.Shape newShape = trans.createTransformedShape(shapeData.shape);
        Renderer renderer = null;
        if (shapeData.isLine)
          renderer = shapeFactory.createLineRenderer(newShape);
        else
          renderer = shapeFactory.createRenderer(newShape);
        vShapeProps.add(new VShapeProperties(renderer, shapeData.color, shapeData.colorUpdatable));
      }

      shapeFactory.registerNamedShape(name, this);
    }
  }
}
