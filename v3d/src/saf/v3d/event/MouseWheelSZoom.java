/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import saf.v3d.scene.Camera;

/**
 * Adds zooming - adjust the 'world' scale via a mouse wheel. This will call
 * canvas.display() when at the end of each wheel event.
 * 
 * @author Nick Collier
 */
public class MouseWheelSZoom implements MouseWheelListener {

  private float zFactor, scale;
  private Camera camera;
  private boolean isEnabled = true;
  //private int x, y;
  
  public MouseWheelSZoom(Camera camera) {
    this.camera = camera;
    zFactor = .01f;
    scale = 1;
  }

  public float getScale() {
    return scale;
  }

  public void reset() {
    scale = 1f;
    //x = y = 0;
  }
  
  public void reset(float scale) {
    this.scale = scale;
    //x = y = 0;
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent evt) {
    if (isEnabled) {
      //x = evt.getX();
      //y = evt.getY();

      int units = evt.getUnitsToScroll();
      scale += units * zFactor;
      
      camera.scale(scale);
      camera.update();
    }
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
