/**
 * 
 */
package saf.v3d.picking;


import com.jogamp.opengl.GL2;

import org.jogamp.vecmath.Point3f;

import saf.v3d.Canvas2D;
import saf.v3d.scene.VComposite;

/**
 * Performs node selection via a mouse click.
 * 
 * @author Nick Collier
 */
public class PickSupport2D extends AbstractPickSupport {

  public PickSupport2D(Canvas2D canvas) {
    super(canvas);
  }

  public void process(GL2 gl, VComposite root) {
    if (mousePressed) {
      // need to set the mouse pressed to false prior t
      // firing the event as the consequences of
      // firing the event can sometimes end up back here.
    	
      mousePressed = false;
      int[] scaled_coords = gl.getContext().getGLDrawable().getNativeSurface().convertToPixelUnits(new int[] {x, y});
      Point3f pt = rayCreator.createOrthoPoint(gl, scaled_coords[0], scaled_coords[1]);
      Accumulator items = new DefaultAccumulator();
      root.intersects(pt, items);
      firePickEvent(items.getItems());
    }
  }
}
