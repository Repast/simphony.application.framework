/**
 * A 3f Ray.
 */
package saf.v3d.math;

import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

/**
 * @author Nick Collier
 */
public class Ray3f {
  
  private Point3f origin;
  private Vector3f direction;
  
  /**
   * @param origin
   * @param direction
   */
  public Ray3f(Point3f origin, Vector3f direction) {
    this.origin = new Point3f(origin);
    this.direction = new Vector3f(direction);
    this.direction.normalize();
  }
 
  /**
   * @return the origin
   */
  public Point3f getOrigin() {
    return origin;
  }

  /**
   * @return the direction
   */
  public Vector3f getDirection() {
    return direction;
  }
  
  public String toString() {
    return "Ray[" + origin + ", " + direction + "]";
  }
  
}
