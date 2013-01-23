/**
 * 
 */
package saf.v3d.render;

import javax.media.opengl.GL2;

/**
 * Null no-op drawing border renderer.
 * 
 * @author Nick Collier
 */
public final class NullBorderRenderer extends AbstractBorderRenderer {

  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#drawBorder(javax.media.opengl.GL, int)
   */
  @Override
  public void drawBorder(GL2 gl, int listIndex, RenderState rState) {}

  /* (non-Javadoc)
   * @see saf.v3d.render.AbstractBorderRenderer#getStrokeSize()
   */
  @Override
  public int getStrokeSize() {
    return -1;
  }
}
