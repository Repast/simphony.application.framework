/**
 * 
 */
package saf.v3d.render;

import org.jogamp.vecmath.Point3f;

/**
 * Interface for iterators over triangles.
 * 
 * @author Nick Collier
 */
public interface TriangleIterator {
  
  /**
   * @return true if there are more triangles, otherwise false.
   */
  boolean hasNext();
  
  /**
   * Gets the next triangle.
   * 
   * @param p1 the first point of the triangle
   * @param p2 the second point of the triangle
   * @param p3 the third point of the triangle
   */
  void next(Point3f p1, Point3f p2, Point3f p3);

}
