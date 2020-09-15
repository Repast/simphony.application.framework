/**
 * 
 */
package saf.v3d;

import com.jogamp.opengl.GLAutoDrawable;
import org.jogamp.vecmath.Point3f;

import saf.v3d.scene.VRoot;
import saf.v3d.scene.VSpatial;

/**
 * Interface for classes that will listen for Canvas reshape and init events.
 * 
 * @author Nick Collier
 */
public interface CanvasListener {
  
  
  /**
   * Called when a VSpatial has been moved using the mouse.
   * 
   * @param spatial the spatial that has been moved
   * @param localTrans the new local translation of the spatial
   */
  void vSpatialMoved(VSpatial spatial, Point3f localTrans);
  
  /**
   * Called when the the canvas initializes itself in its GLEventListener.init.
   * 
   * @param drawable the canvas drawable
   * @param root the root node of the canvas to which other scene elements can be
   * added.
   * @param canvasId the id of the canvas that was initialized
   */
  void init(GLAutoDrawable drawable, VRoot root);
  
  /**
   * Called when canvas reshapes in its GLEventListener.reshape 
   * 
   * @param drawable drawable the canvas drawable
   * @param width the width at z = 0 in world coordinates
   * @param height the height at z = in world coordinates
   * @param root the root node of the canvas to which other scene elements can be
   * added.
   */
  void reshape(GLAutoDrawable drawable, float width, float height, VRoot root);
  
  /**
   * Called when the canvas is being disposed.
   * @param drawable
   */
  void dispose(GLAutoDrawable drawable);
}
