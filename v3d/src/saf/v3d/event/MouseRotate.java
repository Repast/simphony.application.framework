/**
 * 
 */
package saf.v3d.event;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;

import saf.v3d.scene.VSpatial;

/**
 * Adds rotation to a matrix via a mouse (left button drag). This will 
 * call canvas.display() when at the end of each drag event.
 * 
 * @author Nick Collier
 */
public class MouseRotate extends InputHandler {
  
  private float prevMouseX, prevMouseY, rotX, rotY;
  private float ox, oy;
  private GLAutoDrawable canvas;
  
  public MouseRotate(GLAutoDrawable canvas, float rotX, float rotY) {
    this.canvas = canvas;
    this.rotX = ox = rotX;
    this.rotY = oy = rotY;
  }
  
  public void process(GL2 gl) {
    gl.glRotatef(-rotX, 1.0f, 0.0f, 0.0f);
    gl.glRotatef(rotY, 0.0f, 1.0f, 0.0f);
  }
  
  public void process(VSpatial node) {
    Quat4f xQuat = new Quat4f();
    xQuat.set(new AxisAngle4f(1f, 0, 0, (float)Math.toRadians(-rotX)));
    Quat4f yQuat = new Quat4f();
    yQuat.set(new AxisAngle4f(0f, 1f, 0, (float) Math.toRadians(rotY)));
    yQuat.mul(xQuat);
    
    AxisAngle4f angle = new AxisAngle4f();
    angle.set(yQuat);
    node.rotate((float)Math.toDegrees(angle.angle), angle.x, angle.y, angle.z);
  }
  
  public void reset() {
    rotX = ox;
    rotY = oy;
  }
  
  public void mousePressed(MouseEvent evt) {
    if (((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && isEnabled) {
      prevMouseX = evt.getX();
      prevMouseY = evt.getY();
    }
  }

  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent evt) {
    if (((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && !evt.isAltDown() && isEnabled) {
      int x = evt.getX();
      int y = evt.getY();
      Dimension size = evt.getComponent().getSize();

      float thetaY = 360.0f * ((float) (x - prevMouseX) / (float) size.width);
      float thetaX = 360.0f * ((float) (prevMouseY - y) / (float) size.height);

      prevMouseX = x;
      prevMouseY = y;

      rotX += thetaX;
      rotY += thetaY;
      
      canvas.display();
    }
  }
  
  public String toString() {
    return String.format("Rotate: rotx: %f, roty: %f, rotz: %f", rotX, rotY, 0f);
  }
}
