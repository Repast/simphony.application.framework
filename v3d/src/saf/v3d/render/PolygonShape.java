/**
 * 
 */
package saf.v3d.render;

import javax.media.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.util.Utils3D;

/**
 * Base class for Shape implementations.
 * 
 * @author Nick Collier
 */
public class PolygonShape implements Shape {
  
  protected BoundingSphere bounds;
  protected PolygonRenderer renderer;
  
  public PolygonShape(PolygonRenderer renderer) {
    this.renderer = renderer;
    bounds = Utils3D.createBoundingSphere(renderer.getVertices());
  }
  
  /* (non-Javadoc)
   * @see repast.simphony.v3d.Mesh#getLocalBounds()
   */
  @Override
  public BoundingSphere getLocalBounds() {
    return bounds;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#render(javax.media.opengl.GL, saf.v3d.render.RenderState)
   */
  @Override
  public void render(GL2 gl, RenderState rState) {
    renderer.render(gl, rState);
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f, javax.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    TriangleIterator iter = renderer.triangleIterator();
    Point3f p0 = new Point3f();
    Point3f p1 = new Point3f();
    Point3f p2 = new Point3f();
    while (iter.hasNext()) {
      iter.next(p0, p1, p2);
      if (Utils3D.triangleIntersect(p0, p1, p2, rayOrigin, rayDirection)) return true;
    }
    return false;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    TriangleIterator iter = renderer.triangleIterator();
    Point3f p0 = new Point3f();
    Point3f p1 = new Point3f();
    Point3f p2 = new Point3f();
    while (iter.hasNext()) {
      iter.next(p0, p1, p2);
      if (Utils3D.triangleIntersect(point, p0, p1, p2)) return true;
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#invalidate()
   */
  @Override
  public void invalidate() {
    renderer.invalidate();
  }
}
