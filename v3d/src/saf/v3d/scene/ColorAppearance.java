package saf.v3d.scene;

import java.awt.Color;

import javax.media.opengl.GL;

/**
 * Simple plain color appeareance.
 * 
 * @author Nick Collier
 */
public class ColorAppearance implements Appearance {
  
  private float[] color = new float[4];
  
  public ColorAppearance(Color color) {
    color.getComponents(this.color);
  }
  
  public float getRed() {
    return color[0];
  }
  
  public float getGreen() {
    return color[1];
  }
  
  public float getBlue() {
    return color[2];
  }
  
  public float getAlpha() {
    return color[3];
  }
  
  /**
   * Gets the color of this ColorAppearance.
   * 
   * @return the color of this ColorAppearance.
   */
  public Color getColor() {
    return new Color(color[0], color[1], color[2], color[3]);
  }

  @Override
  public void applyAppearance(GL gl) {
    gl.glColor4fv(color, 0);
  }
}
