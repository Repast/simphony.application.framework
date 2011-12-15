/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Vector3f;

/**
 * Zooms - translate along z - via a mouse (left button + ctrl and dragging
 * up down along y). This will  call canvas.display() when at the end of each drag 
 * event.
 * 
 * @author Nick Collier
 */
public class MouseZoom extends InputHandler {
  
  private float  lastY, zFactor;
  private Vector3f translation = new Vector3f();
  private GLAutoDrawable canvas;
  
  public MouseZoom(GLAutoDrawable canvas) {
    this.canvas = canvas;
    zFactor = .01f;
  }
  
  public Vector3f getTranslation() {
    return translation;
  }
  
  public void reset() {
    translation.set(0, 0, 0);
  }
  
  public void mousePressed(MouseEvent evt) {
    if (evt.getButton() == MouseEvent.BUTTON1 && isEnabled) {
      lastY = evt.getY();
    }
  }
  
  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent evt) {
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.isAltDown() && isEnabled) {

      int y = evt.getY();

      float dy = y - lastY;
      translation.z += -dy * zFactor;
      
      lastY = y;

      canvas.display();
    }
  }
  
  /*
  public String toString() {
    return String.format("Mouse Zoom: tx: %f, ty: %f, tz: %f", 0, 0, tz);
  }
  */
}
