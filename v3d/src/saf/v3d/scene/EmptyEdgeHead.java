/**
 * 
 */
package saf.v3d.scene;

import com.jogamp.opengl.GL2;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

/**
 * Empty no-op implementation of EdgeHead
 * @author Nick Collier
 */
public class EmptyEdgeHead implements EdgeHead {

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#draw(com.jogamp.opengl.GL, saf.ui.v3d.RenderState)
   */
  @Override
  public final void draw(GL2 gl, Appearance appearance) {}

  

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#intersects(org.jogamp.vecmath.Point3f, org.jogamp.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    return false;
  }

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#intersects(org.jogamp.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#update()
   */
  @Override
  public final void update(int edgeWidth) {}
  
  

}
