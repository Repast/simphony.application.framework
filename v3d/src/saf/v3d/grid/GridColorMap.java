package saf.v3d.grid;

import org.jogamp.vecmath.Color3f;

public interface GridColorMap {
  
  /**
   * Gets the color for the specified coordinates and
   * updates color with it.
   * 
   * @param x
   * @param y
   * @param color an out parameter containing the new color
   */
  void getColor(int x, int y, Color3f color);

}
