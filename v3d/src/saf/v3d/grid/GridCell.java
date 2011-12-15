/**
 * 
 */
package saf.v3d.grid;

/**
 * Encapsulates the location and value of a grid cell.
 * 
 * @author Nick Collier
 */
public class GridCell {
  
  private int[] location;
  private double value;
  
  public GridCell(double value, int... location) {
    this.value = value;
    this.location = location;
  }
  
  /**
   * Gets the number of dimensions for the coordinate
   * of this GridCell.
   * 
   * @return the number of dimensions for the coordinate
   * of this GridCell.
   */
  public int getDimensionCount() {
    return location.length;
  }
  
  /**
   * Gets the indexed component of the grid cell coodinate.
   * @param dimension the dimension (e.g x, y, etc.) of the component we want
   * @return the indexed component of the grid cell coodinate.
   */
  public int getComponent(int dimension) {
    return location[dimension];
  }
  
  /**
   * Gets the value of this GridCell.
   * 
   * @return the value of this GridCell.
   */
  public double getValue() {
    return value;
  }

}
