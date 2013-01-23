/**
 * 
 */
package saf.v3d.render;

import javax.media.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;

/**
 * Interface for classes that represent a Shape. A Shape is wrapped
 * by a VShape to adapt it to the V* Scene Graph.
 * 
 * @author Nick Collier
 */
public interface Shape {
  
  /**
   * Renders this shape in the current gl context.
   * 
   * @param gl
   */
  void render(GL2 gl, RenderState rState);
  
  /**
   * Gets whether or not the specified ray intersects
   * this shape. The ray is assumed to be in local coordinates.
   * 
   * @param rayOrigin the ray's origin
   * @param rayDirection the ray's direction
   * 
   * @return whether or not the specified ray intersects
   * this shape.
   */
  boolean intersects(Point3f rayOrigin, Vector3f rayDirection);
  
  /**
   * Gets whether or not the specified point
   * intersects with this shape geometry. The point
   * is assumed to be in local coordinates.
   * 
   * @param point the point to test
   * @return true if the point intersects, otherwise false
   */
  boolean intersects(Point3f point);
  
  /**
   * Gets this Shape's local bounds.
   * 
   * @return this Shape's local bounds.
   */
  BoundingSphere getLocalBounds();
  
  /**
   * Invalidates this Shape renderer.
   */
  void invalidate();

}
