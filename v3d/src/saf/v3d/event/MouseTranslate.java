/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseEvent;

import org.jogamp.vecmath.Vector3f;

import saf.v3d.scene.Camera;

/**
 * Adds translate to a matrix via a mouse (third button drag). This will 
 * call canvas.display() when at the end of each drag event.
 * 
 * @author Nick Collier
 */
public class MouseTranslate extends InputHandler {
  
  private float  lastX, lastY, xFactor, yFactor;
  private Vector3f translation = new Vector3f();
  private int buttonMask;
  private Camera camera;
  
  public MouseTranslate(Camera camera) {
    this(camera, MouseEvent.BUTTON3_DOWN_MASK, .01f, .01f);
  }
  
  /**
   * 
   * @param camera
   * @param buttonMask MouseEvent button mask (e.g {@link MouseEvent.BUTTON3_MASK}
   * @param xFactor
   * @param yFactor
   */
  public MouseTranslate(Camera camera, int buttonMask, float xFactor, float yFactor) {
    this.camera = camera;
    this.xFactor = xFactor;
    this.yFactor = yFactor;
    this.buttonMask = buttonMask;
  }
  
  public void reset() {
    lastX = 0;
    lastY = 0;
    translation.set(0, 0, 0);
  }
  
  public void mousePressed(MouseEvent evt) {
    if (((evt.getModifiersEx() & buttonMask) != 0) && isEnabled && !evt.isShiftDown()) {
      lastX = evt.getX();
      lastY = evt.getY();
    }
  }

  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent evt) {
    if (((evt.getModifiersEx() & buttonMask) != 0) && isEnabled && !evt.isShiftDown()) {
      int x = evt.getX();
      int y = evt.getY();

      float dx = x - lastX;
      float dy = y - lastY;
      
      translation.x += dx * xFactor;
      translation.y += -dy * yFactor;

      lastX = x;
      lastY = y;

      camera.pan(translation);
      camera.update();
    }
  }
  
  public String toString() {
    return String.format("Translate: tx: %f, ty: %f, tz: %f", translation.x, 
	translation.y, 0f);
  }
}
