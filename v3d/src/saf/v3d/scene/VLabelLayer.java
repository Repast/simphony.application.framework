/**
 * 
 */
package saf.v3d.scene;

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Layer specialized for drawing labels.
 * 
 * @author Nick Collier
 */
public class VLabelLayer extends VSpatial {

  private TextRenderer txtRenderer;
  private boolean invalid = true;
  private Font font;
  
  private Set<Label> labels = new HashSet<Label>();
  
  public VLabelLayer(Font font) {
    boundingSphere = new BoundingSphere(new Point3f(0, 0, 0), 0);
    this.font = font;
  }
  
  private void init() {
    if (txtRenderer != null) txtRenderer.dispose();
    txtRenderer = new TextRenderer(font, true, true);
  }


  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.scene.VSpatial#invalidate()
   */
  @Override
  public void invalidate() {
    if (txtRenderer != null)
      txtRenderer.dispose();
    txtRenderer = null;
    invalid = true;
  }

  /**
   * Adds the specified label to this layer.
   * 
   * @param label the label to add
   */
  public void addLabel(Label label) {
    labels.add(label);
  }
  
  /**
   * Remove the specified label from this layer.
   * 
   * @param label the label to remove
   */
  public void removeLabel(Label label) {
    labels.remove(label);
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.AbstractVNode#doDraw(javax.media.opengl.GL)
   */
  @Override
  protected void doDraw(GL gl, RenderState rState) {
    if (visible) {
      if (txtRenderer == null || invalid) {
        init();
      }

      txtRenderer.begin3DRendering();
      for (Label label : labels) {
        label.drawText(txtRenderer);
      }
      txtRenderer.end3DRendering();
    }
  }

  @Override
  public void intersects(Point3f point, Accumulator accumulator) {}

  @Override
  public void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator) {}
}
