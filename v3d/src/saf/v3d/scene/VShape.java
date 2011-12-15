/**
 * 
 */
package saf.v3d.scene;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.AppearanceFactory;
import saf.v3d.render.BorderRenderer;
import saf.v3d.render.DefaultBorderRenderer;
import saf.v3d.render.NullBorderRenderer;
import saf.v3d.render.RenderState;
import saf.v3d.render.Shape;

/**
 * Adapts a Shape to the requirements of the scene graph. Rendering and
 * intersection using local coordinates is done by the Shape. Everything is done
 * by VSpatial / VGeometry.
 * 
 * @author Nick Collier
 */
public class VShape extends VGeometry {

  protected Shape shape;
  private BorderRenderer border = new NullBorderRenderer();
  
  public VShape(Shape shape, boolean colorUpdatable) {
    this(shape);
    if (!colorUpdatable) appSetter = new OneTimeAppSetter(this);
  }

  public VShape(Shape shape) {
    this.shape = shape;
    boundingSphere = shape.getLocalBounds();
  }

  @Override
  protected boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    return shape.intersects(rayOrigin, rayDirection);
  }

  @Override
  protected boolean intersects(Point3f point) {
    return shape.intersects(point);
  }

  @Override
  protected void doDraw(GL gl, RenderState state) {
    state.border = border;
    shape.render(gl, state);
  }

  /**
   * Sets the border color.
   * 
   * @param color
   *          the border color.
   */
  public void setBorderColor(Color color) {
    if (border.getStrokeSize() == -1) {
      border = new DefaultBorderRenderer();
    }
    border.setColor(AppearanceFactory.createColorAppearance(color));
  }

  /**
   * Gets the border color.
   * 
   * @return the border color.
   */
  public Color getBorderColor() {
    return border.getColor().getColor();
  }

  /**
   * Gets the stroke size of the border.
   * 
   * @return the border stroke size.
   */
  public int getBorderStrokeSize() {
    return border.getStrokeSize();
  }

  /**
   * Sets the border stroke size.
   * 
   * @param size
   *          the border stroke size
   */
  public void setBorderStrokeSize(int size) {
    if (size == 0) {
      border = new NullBorderRenderer();
    } else {
      if (border.getStrokeSize() == -1) {
        border = new DefaultBorderRenderer();
      }
      border.setStrokeSize(size);
    }
  }
  
  /**
   * Invalidates the Shape contained by this VShape.
   * 
   */
  @Override
  public void invalidate() {
    shape.invalidate();
  }
}
