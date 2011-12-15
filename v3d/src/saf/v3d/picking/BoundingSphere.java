/**
 * 
 */
package saf.v3d.picking;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import saf.v3d.util.Utils3D;

/**
 * A bounding sphere.
 * 
 * @author Nick Collier
 */
public class BoundingSphere {
  
  private Point3f center = new Point3f();
  private float radius;

  /**
   * Creates a BoundingSphere with the specified center and radius.
   * 
   * @param center
   * @param radius
   */
  public BoundingSphere(Point3f center, float radius) {
    this.center.set(center);
    this.radius = radius;
  }
  
  public BoundingSphere(BoundingSphere sphere) {
    this(sphere.center, sphere.radius);
  }
  
  public Point3f getCenterRef() {
    return center;
  }
  
  public float getRadius() {
    return radius;
  }
  
  /**
   * Whether or not the specified point intersects with this BoundingSphere.
   * 
   * @param point the point to test
   * @return true if the ray intersects otherwise false.
   */
  public boolean intersects(Point3f point) {
    Vector3f vec = new Vector3f();
    vec.sub(center, point);
    return vec.lengthSquared() <= radius * radius;
  }
  
  /**
   * Whether or not the specified ray intersects with this BoundingSphere.
   * 
   * @param rayOrigin the ray's origin
   * @param rayDirection the ray's direction
   * @return true if the ray intersects otherwise false.
   */
  // p. 536 of Van Verth and Bishop, "Essential Mathematics for Games ..."
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    Vector3f w = new Vector3f(center);
    w.sub(rayOrigin);
    float wsq = w.dot(w);
    float proj = w.dot(rayDirection);
    float rsq = radius * radius;
    // sphere is behind ray, no intersection
    if (proj < 0f && wsq > rsq) {
      return false;
    }
    
    float vsq = rayDirection.dot(rayDirection);
    // test length of diff vs. radius
    return (vsq * wsq - proj * proj <= vsq * rsq);
  }
  
  /**
   * Transform this bounding sphere.
   * @param scale
   * @param rotate
   * @param translate
   * @return this BoundingSphere post transform
   */
  public BoundingSphere transform(float scale, Quat4f rotate, Vector3f translate) {
    Point3f center = new Point3f(this.center);
    Matrix4f matrix = new Matrix4f();
    matrix.setRotation(rotate);
    matrix.setTranslation(translate);
    matrix.setScale(scale);
    matrix.transform(center);
    this.center = center;
    this.radius *= scale;
    return this;
  }
  /**
   * Merge this BoudingSphere with the specified BoudingSphere
   * storing the results in this BoundingSphere.
   * 
   * @param sphere the sphere to merge with
   * @param out the box to store the result in.
   * @return this BoundingSphere post-merge
   */
  // p. 148-149, Eberly, 3D Game Engine Design
  public BoundingSphere merge(BoundingSphere sphere) {
    if (sphere.radius == 0) return this;
    
    Vector3f diff  = new Vector3f(sphere.center);
    diff.sub(this.center);
    float distSq = diff.dot(diff);
    float radiusDiff = sphere.radius - this.radius;
    
    if (distSq <= radiusDiff * radiusDiff) {
      if (this.radius > sphere.radius) {
	return this;
      } else {
	this.center = new Point3f(sphere.center);
	this.radius = sphere.radius;
	return this;
      }
    } else {
      // build new sphere
      float dist = (float)Math.sqrt(distSq);
      float radius = 0.5f * (this.radius + sphere.radius + dist);
      if (!Utils3D.isZero(dist)) {
	diff.scale((radius - this.radius) / dist);
	this.center.add(diff);
      }
      this.radius = radius;
    }
    
    return this;
   
  }
  
  public String toString() {
    return "center: " + center + ", radius: " + radius;
  }
}
