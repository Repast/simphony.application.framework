/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Vector3f;

/**
 * Adds zooming - translate along z - to matrix via a mouse wheel. This will 
 * call canvas.display() when at the end of each wheel event.
 * 
 * @author Nick Collier
 */
public class MouseWheelTZoom implements MouseWheelListener {
  
  private float zFactor;
  private Vector3f translation = new Vector3f();
  private GLAutoDrawable canvas;
  private boolean isEnabled = true;
  
  public MouseWheelTZoom(GLAutoDrawable canvas) {
    this.canvas = canvas;
    zFactor = .01f;
  }
  
  public Vector3f getTranslation() {
    return translation;
  }
  
  public void reset() {
    translation.set(0, 0, 0);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent evt) {
    if (isEnabled) {
    int units = evt.getUnitsToScroll();
    translation.z += units * zFactor;
    
    canvas.display();
    }
  }
  
  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }
  
  public boolean isEnabled() {
    return isEnabled;
  }
  
  public String toString() {
    return String.format("Zoom: tx: %f, ty: %f, tz: %f", 0f, 0f, translation.z);
  }
}
