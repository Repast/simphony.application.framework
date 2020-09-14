/**
 * 
 */
package saf.v3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;
import saf.v3d.render.Shape;

/**
 * Renders a Line 2D Box.
 * 
 * @author Nick Collier
 */
public class BoxShape2D implements Shape {
  
  private float width, height;
  private BoundingSphere sphere;
  private int listIndex;
  private boolean invalid = true;
  
  public BoxShape2D(float width, float height) {
   
    this.width = width;
    this.height = height;
    float radius = Math.max(width / 2, height / 2);
    sphere = new BoundingSphere(new Point3f(width / 2, height / 2, 0), radius);
  }
  
  private void initDL(GL2 gl) {
    listIndex = gl.glGenLists(1);
    gl.glNewList(listIndex, GL2.GL_COMPILE);
   
    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glVertex3f(0, 0, 0);
    gl.glVertex3f(0, height, 0);
    gl.glVertex3f(width, height, 0);
    gl.glVertex3f(width, 0, 0);
    gl.glEnd();
    gl.glEndList();
    
    invalid = false;
    
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#getLocalBounds()
   */
  @Override
  public BoundingSphere getLocalBounds() {
    return sphere;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f, javax.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    return false;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    return false;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#render(com.jogamp.opengl.GL, saf.v3d.render.RenderState)
   */
  @Override
  public void render(GL2 gl, RenderState state) {
    if (invalid) initDL(gl);
    state.appearance.applyAppearance(gl);
    gl.glLineWidth(1);
    state.lineWidth = 1;
    gl.glCallList(listIndex);
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#invalidate()
   */
  @Override
  public void invalidate() {
    invalid = true;
  }
  
  
}
