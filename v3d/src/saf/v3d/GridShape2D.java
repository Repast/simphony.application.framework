/**
 * 
 */
package saf.v3d;

import javax.media.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;
import saf.v3d.render.Shape;

/**
 * Renders a grid.
 * 
 * @author Nick Collier
 */
public class GridShape2D implements Shape {
  
  private float cellSize;
  private int cols, rows;
  private BoundingSphere sphere;
  private int listIndex;
  private boolean invalid = true;
  
  public GridShape2D(float cellSize, int cols, int rows) {
    this.cellSize = cellSize;
    this.cols = cols;
    this.rows = rows;
    float width = cols * cellSize;
    float height = rows * cellSize;
    float radius = Math.max(width / 2, height / 2);
    sphere = new BoundingSphere(new Point3f(width / 2, height / 2, 0), radius);
  }
  
  private void initDL(GL2 gl) {
    // create the display list
    listIndex = gl.glGenLists(1);
    gl.glNewList(listIndex, GL2.GL_COMPILE);
    gl.glBegin(GL2.GL_LINES);
    float yLimit = cellSize * rows;
    float xLimit = cellSize * cols;
    for (float x = 0; x <= xLimit; x += cellSize) {
      gl.glVertex3f(x, 0, 0);
      gl.glVertex3f(x, yLimit, 0);
    }
    
    for (float y = 0; y <= yLimit; y += cellSize) {
      gl.glVertex3f(0, y, 0);
      gl.glVertex3f(xLimit, y, 0);
    }
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
   * @see saf.v3d.render.Shape#render(javax.media.opengl.GL, saf.v3d.render.RenderState)
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
