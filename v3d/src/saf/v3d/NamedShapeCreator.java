/**
 * 
 */
package saf.v3d;

import java.awt.Color;
import java.awt.Shape;

/**
 * Interface for classes that can create a shape
 * that can later be retrieved by name.
 * 
 * @author Nick Collier
 */
public interface NamedShapeCreator {
  
  /**
   * Add the specified shape to this NamedShapeCreator.
   * 
   * @param shape the shape to add
   * @param color the color of the added shape
   * @param canUpdateColor whether or not the color of this shape can be updated once its been set
   */
  void addShape(Shape shape, Color color, boolean canUpdateColor);
  
  /**
   * Add the specified shape as line to this NamedShapeCreator.
   * 
   * @param shape the shape to add
   * @param color the color of the added shape
   * @param canUpdateColor whether or not the color of this shape can be updated once its been set
   */
  void addLine(Shape shape, Color color, boolean canUpdateColor);
  
  /**
   * Creates a composite shape for the shapes that have been
   * passed in and registers it with the ShapeFactory2D. The created 
   * shape will be transformed, if necessary, to center around 0, 0.
   */
  void registerShape();

}
