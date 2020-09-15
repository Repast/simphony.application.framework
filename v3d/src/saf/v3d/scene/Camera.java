/**
 * 
 */
package saf.v3d.scene;

import java.util.concurrent.locks.Lock;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import org.jogamp.vecmath.Matrix3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import saf.v3d.Canvas2D;
import saf.v3d.CanvasListener;

/**
 * Camera that manipulates the view transform.
 * 
 * @author Nick Collier
 */
public class Camera implements CanvasListener {

  private GLAutoDrawable drawable;
  private Canvas2D canvas;
  private VRoot root;
  private float width, height;
  float scale = 1;
  Matrix3f rotation;

  private Vector3f pan = new Vector3f();
  private Vector3f translate = new Vector3f();

  public Camera(Canvas2D canvas, GLAutoDrawable drawable) {
    this.drawable = drawable;
    this.canvas = canvas;

    root = new VRoot(this);
    root.markAsDirty();
    rotation = new Matrix3f();
    rotation.setIdentity();
  }

  /**
   * Gets the root node of the scene.
   * 
   * @return the root node of the scene.
   */
  public VRoot getRoot() {
    return root;
  }

  public void resetAndCenter() {
    Lock lock = canvas.getRenderLock();
    try {
      lock.lock();
      root.markBoundsDirty();
      Point3f center = root.getBoundingSphere().getCenterRef();
      boolean isNaN = Float.isNaN(center.x) || Float.isNaN(center.y) || Float.isNaN(center.z);
      if (!isNaN) {
        this.pan.set(new Vector3f());
        translate.x = width / 2f - center.x;
        translate.y = height / 2f - center.y;
        translate.z = 0;
        scale = 1;
      }
      root.markAsDirty();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Resets the camera to the default zoom and pan.
   */
  public void reset() {
    Lock lock = canvas.getRenderLock();
    try {
      lock.lock();
      root.markAsDirty();
      this.pan.set(new Vector3f());
      this.translate.set(new Vector3f());
      this.scale = 1;
    } finally {
      lock.unlock();
    }
  }

  public void pan(Vector3f pan) {
    Lock lock = canvas.getRenderLock();
    try {
      lock.lock();
      root.markAsDirty();
    } finally {
      lock.unlock();
    }
    translate.x += (pan.x - this.pan.x) * scale;
    translate.y += (pan.y - this.pan.y) * scale;
    this.pan.set(pan);
  }

  public float getScale() {
    return scale;
  }

  public void getTranslate(Vector3f trans) {
    translate.get(trans);
  }

  /**
   * Scales the camera view by the specified amount around the specified point.
   * The point is in screen (e.g. mouse) coordinates.
   * 
   * @param scale
   *          the amount to scale
   * @param pt
   *          the point to scale around
   */
  public void scale(float scale) {
    Lock lock = canvas.getRenderLock();
    try {
      lock.lock();
      root.markAsDirty();
      root.markBoundsDirty();
    } finally {
      lock.unlock();
    }
    this.scale = scale;
  }

  /**
   * Forces a redraw.
   */
  public void update() {
    drawable.display();
  }

  public void updateProjection(GL2 gl) {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(-width / 2 * scale, width / 2 * scale, -height / 2 * scale, height / 2 * scale, -1,
        1);
  }

  /**
   * Applies the view transform to the GL.
   * 
   * @param gl
   */
  public void applyViewTransforms(GL2 gl) {
    // pan, plus the translate back so 0, 0 is lower right corner
    gl.glTranslatef(translate.x - width / 2, translate.y - height / 2, 0);
  }

  @Override
  public void init(GLAutoDrawable drawable, VRoot root) {
  }

  @Override
  public void reshape(GLAutoDrawable drawable, float width, float height, VRoot root) {
    this.height = height;
    this.width = width;
  }

  public void dispose(GLAutoDrawable drawable) {
  }

  @Override
  public void vSpatialMoved(VSpatial spatial, Point3f localTrans) {
  }
}
