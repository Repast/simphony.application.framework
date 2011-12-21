/**
 * 
 */
package saf.v3d;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.swing.JPanel;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import saf.v3d.event.MouseKeyZoom;
import saf.v3d.event.MouseTranslate;
import saf.v3d.event.MouseWheelSZoom;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.picking.PickListener;
import saf.v3d.picking.PickSupport2D;
import saf.v3d.render.RenderState;
import saf.v3d.scene.Camera;
import saf.v3d.scene.VRoot;
import saf.v3d.scene.VSpatial;

import com.sun.opengl.util.Screenshot;

/**
 * JOGL canvas set up for 2D drawing.
 * 
 * @author Nick Collier
 */
public class Canvas2D implements GLEventListener, Canvas {

  // private static final String PAN_ICON = "move.png";
  // private static final String SELECT_ICON = "select.png";

  private GLAutoDrawable drawable;
  private JPanel panel;
  private Color4f background = new Color4f(Color.WHITE);

  private MouseTranslate translator;
  private MouseWheelSZoom wheelZoomer;
  private MouseKeyZoom keyZoomer;
  private PickSupport2D picker;

  private List<CanvasListener> listeners = new ArrayList<CanvasListener>();
  //private Box worldSizeAtZero;
  private VRoot root;
  private boolean initialized = false;
  private Camera camera;

  private RenderState rState = new RenderState();
  private Lock lock = new ReentrantLock();

  private ShapeFactory2D shapeFactory = new ShapeFactory2D();

  private int width, height;
  private float extentWidth, extentHeight;

  public Canvas2D() {
    GLCapabilities caps = new GLCapabilities();
    caps.setSampleBuffers(true);
    caps.setNumSamples(4);
    drawable = new GLCanvas(caps);
    drawable.addGLEventListener(this);
    drawable.setAutoSwapBufferMode(false);
    camera = new Camera(this, drawable);
    addCanvasListener(camera);
    addCanvasListener(shapeFactory);
    root = camera.getRoot();
    root.id = "ROOT";

    translator = new MouseTranslate(camera, MouseEvent.BUTTON3_MASK, 1f, 1f);
    wheelZoomer = new MouseWheelSZoom(camera);
    keyZoomer = new MouseKeyZoom(camera, MouseEvent.BUTTON3_MASK);
    picker = new PickSupport2D(this);
  }

  /**
   * Sets the default extent of the objects displayed by this canvas. If these
   * are non-zero then these values will be used to scale the display such that
   * it will fit in the canvas.
   * 
   * @param width
   * @param height
   */
  public void setDefaultExtent(float width, float height) {
    this.extentWidth = width;
    this.extentHeight = height;
  }

  /**
   * Gets a BufferedImage of what the canvas is currently displaying.
   * 
   * @return a BufferedImage of what the canvas is currently displaying.
   */
  public BufferedImage createImage() {
    try {
      drawable.getContext().makeCurrent();
      return Screenshot.readToBufferedImage(width, height, true);
    } catch (GLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public ShapeFactory2D getShapeFactory() {
    return shapeFactory;
  }

  public void update() {
    drawable.display();
  }

  /**
   * Gets the lock that can be used to synchronize on the rendering. This lock
   * is locked while rendering occurs and then unlocked when rendering is
   * finished.
   * 
   * @return the lock that can be used to synchronize on the rendering
   */
  public Lock getRenderLock() {
    return lock;
  }

  /**
   * Called when a VSpatial has been moved using the mouse.
   * 
   * @param spatial
   *          the spatial that has been moved
   * @param localTrans
   *          the new local translation of the spatial
   */
  public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
    for (CanvasListener listener : listeners) {
      listener.vSpatialMoved(spatial, localTrans);
    }
  }

  /**
   * Adds the specified canvas listener to this canvas.
   * 
   * @param listener
   *          the listener to add
   */
  public void addCanvasListener(CanvasListener listener) {
    listeners.add(listener);
  }

  /**
   * Adds the specified PickListener to this canvas to listen for pick events.
   * 
   * @param listener
   *          the listener to add
   */
  public void addPickListener(PickListener listener) {
    picker.addPickListener(listener);
  }

  public JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel(new BorderLayout());
      // panel.add(getToolBar(), BorderLayout.NORTH);
      panel.add((Component) drawable, BorderLayout.CENTER);
      panel.setMinimumSize(new Dimension(10, 10));
    }
    return panel;
  }

  public void init(GLAutoDrawable drawable) {
    // if (Boolean.getBoolean("anl.aida.jogl.debug")) {
    // System.out.println("jogl debug on");
    // drawable.setGL(new TraceGL(drawable.getGL(), System.out));
    // }

    // System.err.println("Chosen GLCapabilities: " +
    // drawable.getChosenGLCapabilities());
    GL gl = drawable.getGL();
    // turn off vsyncs
    gl.setSwapInterval(0);
    gl.glClearColor(background.x, background.y, background.z, background.w);
    gl.glShadeModel(GL.GL_SMOOTH);

    gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

    shapeFactory.invalidateRenderers(gl);
    root.invalidate();
    for (CanvasListener listener : listeners) {
      listener.init(drawable, root);
    }

    if (!initialized) {
      drawable.addMouseListener(translator);
      drawable.addMouseMotionListener(translator);
      drawable.addMouseWheelListener(wheelZoomer);
      drawable.addMouseMotionListener(keyZoomer);
      drawable.addMouseListener(keyZoomer);
      drawable.addMouseListener(picker);
    }

    initialized = true;
  }

  public void dispose() {
    for (CanvasListener listener : listeners) {
      drawable.getContext().makeCurrent();
      listener.dispose(drawable);
    }
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    try {
      lock.lock();
      rState.reset();
      rState.width = drawable.getWidth();
      rState.height = drawable.getHeight();
      GL gl = drawable.getGL();
      gl.glClear(GL_COLOR_BUFFER_BIT);

      camera.updateProjection(gl);

      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPushMatrix();
      gl.glLoadIdentity();
      // for exact pixelization
      // see
      // http://www.opengl.org/resources/faq/technical/transformations.htm#tran0030
      gl.glTranslatef(0.375f, 0.375f, 0);
      gl.glPushMatrix();

      camera.applyViewTransforms(gl);
      root.draw(gl, rState);
      gl.glPopMatrix();
      gl.glPopMatrix();

      picker.process(gl, root);
      drawable.swapBuffers();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
  }

  /**
   * Gets a Box representing the dimensions of the viewable world when z is 0.
   * 
   * @return a Box representing the dimensions of the viewable world when z is
   *         0.
   * 
   *         public Box getWorldSizeAtZero() { return worldSizeAtZero == null ?
   *         new Box() : worldSizeAtZero; }
   */

  /**
   * Resets the camera back to the default zoom and pan.
   */
  public void resetCamera() {
    camera.reset();
  }

  // this seems inverted but it accounts for
  // how the scale is actually used to create an
  // orthogonal projection that encompasses a larger area.
  private float calculateScaleFromSphere() {
    BoundingSphere sphere = root.getBoundingSphere();
    float diameter = sphere.getRadius() * 2;
    // scale the diameter so it fits in the smaller of width and height;
    float min = Math.min(width, height);
    float scale = 1;
    if (diameter > min) {
      scale = diameter / min;
    }

    return scale;
  }

  // this seems inverted but it accounts for
  // how the scale is actually used to create an
  // orthogonal projection that encompasses a larger area.
  private float calculateScaleFromExtents() {
    float scale = 1;

    if (extentWidth > width || extentHeight > height) {
      if (extentWidth > width) {
        scale = extentWidth / width;
      }

      if (extentHeight > height) {
        scale = Math.max(scale, extentHeight / height);
      }
      
      // bit extra to create some borders
      scale += .1f;
    }

    return scale;
  }

  /**
   * Centers the scene in the middle of the canvas.
   */
  public void centerScene() {
    translator.reset();
    wheelZoomer.reset();
    keyZoomer.reset();
    camera.resetAndCenter();

    
    float scale = 1;
    if (extentWidth > 0 && extentHeight > 0) {
      scale = calculateScaleFromExtents();
    } else {
      scale = calculateScaleFromSphere();
    }

    wheelZoomer.reset(scale);
    keyZoomer.reset(scale);
    camera.scale(scale);
   
    // camera.update();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL gl = drawable.getGL();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(-width / 2f, width / 2f, -height / 2f, height / 2f, -1, 1);
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    // Make sure depth testing and lighting are disabled for 2D rendering
    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_LIGHTING);

    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    // System.out.println("worldSizeAtZero = " + worldSizeAtZero);

    for (CanvasListener display : listeners) {
      display.reshape(drawable, width, height, root);
    }

    boolean firstReshape = this.width == 0;
    this.width = width;
    this.height = height;
    if (firstReshape)
      centerScene();
  }

  /**
   * Sets the background color of this canvas.
   * 
   * @param color
   *          the new background color
   */
  public void setBackgroundColor(Color color) {
    background.set(color);
  }

  /**
   * Gets the background color of this canvas.
   * 
   * @return the background color.
   */
  public Color getBackgroundColor() {
    return background.get();
  }

  /**
   * Gets the root node of the scene graph managed by this canvas.
   * 
   * @return the root node of the scene graph managed by this canvas.
   */
  public VRoot getRoot() {
    return root;
  }
}
