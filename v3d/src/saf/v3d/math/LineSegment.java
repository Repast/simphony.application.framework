/**
 * 
 */
package saf.v3d.math;

import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

/**
 * @author Nick Collier
 */
public class LineSegment {
  
  private Vector3f origin, direction;
  
  public LineSegment(Point3f pt1, Point3f pt2) {
    origin = new Vector3f(pt1);
    direction = new Vector3f();
    direction.sub(pt2, pt1);
  }
  
  /**
   * Calculates the distance squared between this line segment
   * and the specified point.
   * 
   * @param pt
   * @return
   */
  public float distSquared(Point3f pt) {
    Vector3f w = new Vector3f();
    w.sub(pt, origin);
    float proj = w.dot(direction);
    // end point 0 is closest point
    if (proj <= 0) {
      return w.dot(w);
    } else {
      float vsq = direction.dot(direction);
      // end point 1 is closest point
      if (proj >= vsq) {
        return w.dot(w) - 2f * proj + vsq;
        // somewhere else on the segment
      } else {
        return w.dot(w) - proj * proj / vsq;
      }
    }
  }
}
