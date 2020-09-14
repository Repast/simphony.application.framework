/**
 * 
 */
package saf.v3d.render;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * Used in conjunction with Renderer, this will draw a border around
 * a polygon. It uses polygon offsets and then draws the polygon as
 * using lines. The polygon itself should then be drawn immediately
 * after it. 
 * 
 * @author Nick Collier
 */
public class DefaultBorderRenderer extends AbstractBorderRenderer {

  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#drawBorder(com.jogamp.opengl.GL, int)
   */
  @Override
  public void drawBorder(GL2 gl, int listIndex, RenderState rState) {
    gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
    if (rState.lineWidth != strokeSize) {
      gl.glLineWidth(strokeSize);
      rState.lineWidth = strokeSize;
    }
    color.applyAppearance(gl);
    gl.glPolygonOffset(0, -1f);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
    gl.glCallList(listIndex);
    gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
  }
  

}
