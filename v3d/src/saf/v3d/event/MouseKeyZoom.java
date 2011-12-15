/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseEvent;

import saf.v3d.scene.Camera;

/**
 * Adds zooming - adjust the 'world' scale via mouse move  up and down
 * in combination with mouse button down and the shift key.
 * 
 * @author Nick Collier
 */
public class MouseKeyZoom extends InputHandler {

  private float zFactor, scale;
  private Camera camera;
  private boolean isEnabled = true;
  private int lastY = -1;
  private int buttonMask;
  
  public MouseKeyZoom(Camera camera, int buttonMask) {
    this.camera = camera;
    zFactor = .005f;
    scale = 1;
    this.buttonMask = buttonMask;
    
  }

  public float getScale() {
    return scale;
  }

  public void reset() {
    scale = 1f;
  }
  
  public void reset(float scale) {
    this.scale = scale;
  }

  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent evt) {
    if (((evt.getModifiers() & buttonMask) != 0) && isEnabled && evt.isShiftDown()) {

      int y = evt.getY();
      if (lastY != -1) {
        float dy = y - lastY;
        scale += dy * zFactor;
        camera.scale(scale);
        camera.update();
      }
      lastY = y;
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    lastY = -1;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public String toString() {
    return String.format("Zoom: s: %f", scale);
  }
}
