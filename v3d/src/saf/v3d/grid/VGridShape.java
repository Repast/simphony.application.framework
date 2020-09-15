package saf.v3d.grid;

import org.jogamp.vecmath.Point3f;

import saf.v3d.scene.VShape;

/**
 * VShape customized to work with a GridMesh.
 * 
 * @author Nick Collier
 */
public class VGridShape extends VShape {
  
  private Object propertyKey;
  
  public VGridShape(GridMesh grid, Object propertyKey) {
    super(grid);
    this.propertyKey = propertyKey;
  }
  
  /**
   * Updates the grid to reflect the current state.
   */
  public void update() {
    ((GridMesh)shape).update();
  }
  
  protected boolean intersects(Point3f point) {
    if (super.intersects(point)) {
      int[] location = ((GridMesh)shape).getCellLocation(point);
      this.putProperty(propertyKey, location);
      return true;
    }
    return false;
  }
  
}
