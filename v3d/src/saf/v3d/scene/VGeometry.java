/**
 * 
 */
package saf.v3d.scene;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;

/**
 * Abstract base class for VNodes that contain geometry. This
 * implements the intersects methods from VSpatial converting from world
 * to local coordinates.
 * 
 * @author Nick Collier
 */
public abstract class VGeometry extends VSpatial {
  
  /* (non-Javadoc)
   * @see repast.simphony.v3d.VSpatial#intersects(javax.vecmath.Point3f, repast.simphony.v3d.Accumulator)
   */
  @Override
  public void intersects(Point3f point, Accumulator accumulator) {
    BoundingSphere sphere = getBoundingSphere();
    if (sphere.intersects(point)) {
      Point3f localPt = new Point3f(point);
      Matrix4f matrix = new Matrix4f();
      matrix.setIdentity();
      matrix.setRotation(worldRotation);
      matrix.setScale(worldScale);
      matrix.setTranslation(worldTrans);
      
      Matrix4f invert = new Matrix4f();
      invert.setIdentity();
      invert.invert(matrix);
      
      matrix.setTranslation(worldTrans);
      invert.setIdentity();
      invert.invert(matrix);
      invert.transform(localPt);
      
      if (intersects(localPt)) {
        accumulator.add(this);
      }
    }
  }

  /* (non-Javadoc)
   * @see anl.mifs.viz3d.VNode#intersects(javax.vecmath.Point3f, javax.vecmath.Vector3f, anl.mifs.viz3d.Accumulator)
   */
  @Override
  public void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator) {
    BoundingSphere sphere = getBoundingSphere();
    if (sphere.intersects(rayOrigin, rayDirection)) {
      
      // transform the ray to local frame from world frame using the
      // inverse of the local to world matrix. The ray can then
      // be tested against the geometry to see if it intersects
      Point3f transOrigin = new Point3f(rayOrigin);
      Vector3f transDir = new Vector3f(rayDirection);
      
      Matrix4f matrix = new Matrix4f();
      matrix.setIdentity();
      
      matrix.setRotation(worldRotation);
      matrix.setScale(worldScale);
      
      Matrix4f invert = new Matrix4f();
      invert.setIdentity();
      invert.invert(matrix);
      invert.transform(transDir);
      transDir.normalize();
      
      matrix.setTranslation(worldTrans);
      invert.setIdentity();
      invert.invert(matrix);
      invert.transform(transOrigin);
      
      if (intersects(transOrigin, transDir)) {
	accumulator.add(this);
      }
    }
  }
  
  /**
   * Gets whether or not the specified ray intersects
   * this VGeometry's geometry.
   * 
   * @param rayOrigin the ray's origin
   * @param rayDirection the ray's direction
   * 
   * @return whether or not the specified ray intersects
   * this VGeometry's geometry.
   */
  protected abstract boolean intersects(Point3f rayOrigin, Vector3f rayDirection);
  
  /**
   * Gets whether or not the specified point
   * intersects with this VGeometry's geometry.
   * 
   * @param point the point to test
   * @return true if the point intersects, otherwise false
   */
  protected abstract boolean intersects(Point3f point);

}
