/**
 * 
 */
package saf.v3d.render;

import java.awt.Color;

import saf.v3d.AppearanceFactory;
import saf.v3d.scene.ColorAppearance;

/**
 * Abstract implementation of BorderRenderer, implementing the various border
 * properties.
 * 
 * @author Nick Collier
 */
public abstract class AbstractBorderRenderer implements BorderRenderer {
  
  protected ColorAppearance color = AppearanceFactory.createColorAppearance(Color.BLACK);
  protected int strokeSize = 1;
  
  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#getColor()
   */
  @Override
  public ColorAppearance getColor() {
    return color;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#getStrokeSize()
   */
  @Override
  public int getStrokeSize() {
    return strokeSize;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#setColor(saf.v3d.scene.ColorAppearance)
   */
  @Override
  public void setColor(ColorAppearance color) {
    this.color = color;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.BorderRenderer#setStrokeSize(int)
   */
  @Override
  public void setStrokeSize(int size) {
    this.strokeSize = size;
  }
  
  

}
