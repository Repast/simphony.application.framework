/**
 * 
 */
package saf.v3d.render;

import com.jogamp.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.util.Utils3D;

/**
 * Shape implementation for lines.
 * 
 * @author Nick Collier
 */
public class LineShape implements Shape {
  
  private LineListRenderer renderer;
  private BoundingSphere bounds;
  
  public LineShape(LineListRenderer renderer) {
    this.renderer = renderer;
    bounds = Utils3D.createBoundingSphere(renderer.getVertices());
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#getLocalBounds()
   */
  @Override
  public BoundingSphere getLocalBounds() {
    return bounds;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f, javax.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    return renderer.intersects(point);
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#render(com.jogamp.opengl.GL, saf.v3d.render.RenderState)
   */
  @Override
  public void render(GL2 gl, RenderState state) {
    renderer.render(gl, state);
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#invalidate()
   */
  @Override
  public void invalidate() {
    renderer.invalidate();
  }
}
