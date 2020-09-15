/**
 * 
 */
package saf.v3d.scene;

import com.jogamp.opengl.GL2;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

/**
 * Interface for edge head icon (arrows, etc.).
 * 
 * @author Nick Collier
 */
public interface EdgeHead {
  
  /**
   * Updates the EdgeHead to account for any changes in its
   * associated edges end points.
   * 
   * @param edgeWidth the width of the edge
   */
  void update(int edgeWidth);
  
  /**
   * Draws this EdgeHead.
   * 
   * @param gl the gl used to draw
   * @param appearance the appearance to draw the head with
   */
  void draw(GL2 gl, Appearance appearance);
  
  /**
   * Performs an intersection test on this EdgeHead with the specified point.
   * 
   * @param point the point to test
   * @return true if the point intersects with this EdgeHead, otherwise false.
   */
  boolean intersects(Point3f point);
  
  
  /**
   * Performs an intersection test on this EdgeHeader with the specified ray.
   * 
   * @param rayOrigin the ray's origin
   * @param rayDirection the ray's direction
   * 
   * @return true if the ray intersects with this EdgeHead, otherwise false.
   */
  boolean intersects(Point3f rayOrigin, Vector3f rayDirection);

}
