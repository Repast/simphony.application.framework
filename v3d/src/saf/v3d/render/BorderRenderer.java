/**
 * 
 */
package saf.v3d.render;

import javax.media.opengl.GL2;

import saf.v3d.scene.ColorAppearance;

/**
 * Interface for classes that render a border using a
 * display list.
 * 
 * @author Nick Collier
 */
public interface BorderRenderer {
  
  /**
   * Sets the border color.
   * 
   * @param color the border color.
   */
  void setColor(ColorAppearance color);
  
  /**
   * Gets the border color.
   * 
   * @return the border color.
   */
  ColorAppearance getColor();
  
  /**
   * Gets the stroke size of the border.
   * 
   * @return the border stroke size.
   */
  int getStrokeSize();
  
  /**
   * Sets the border stroke size.
   * 
   * @param size the border stroke size
   */
  void setStrokeSize(int size);
  
  /**
   * Draws the border.
   * 
   * @param gl
   * @param listIndex the display list index 
   */
  void drawBorder(GL2 gl, int listIndex, RenderState rState);

}
