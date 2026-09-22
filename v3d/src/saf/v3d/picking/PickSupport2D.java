/**
 * 
 */
package saf.v3d.picking;


import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLDrawable;

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

      // The mouse coordinates are in logical (DPI-unscaled) component units,
      // but createOrthoPoint unprojects against the render surface, which is in
      // pixels. Scale by the actual surface-to-component ratio to get pixels.
      // This is correct on every platform and DPI scale; NativeSurface's
      // convertToPixelUnits() is unreliable for a GLJPanel on Windows high-DPI
      // displays (JOGL 2.6.0) and left the pick shifted down and to the right.
      GLDrawable drawable = gl.getContext().getGLDrawable();
      int surfaceWidth = drawable.getSurfaceWidth();
      int surfaceHeight = drawable.getSurfaceHeight();
      int px = x;
      int py = y;
      if (componentWidth > 0 && componentHeight > 0) {
        px = Math.round(x * (float) surfaceWidth / componentWidth);
        py = Math.round(y * (float) surfaceHeight / componentHeight);
      }
      Point3f pt = rayCreator.createOrthoPoint(gl, px, py);
      Accumulator items = new DefaultAccumulator();
      root.intersects(pt, items);
      firePickEvent(items.getItems());
    }
  }
}
