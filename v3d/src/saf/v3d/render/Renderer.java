/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;

/**
 * Interface for classes that can render using a GL
 * and render state.
 * 
 * @author Nick Collier
 */
public interface Renderer {
  
  /**
   * Performs a render using the specified gl and state.
   * 
   * @param gl
   * @param rState
   */
  void render(GL2 gl, RenderState rState);
  
  /**
   * Gets the vertices to be rendered by this Renderer.
   * 
   * @return the vertices to be rendered by this Renderer.
   */
  FloatBuffer getVertices();
  
  /**
   * Disposes of any resources used by this renderer.
   * 
   * @param gl
   */
  void dispose(GL2 gl);
  
  /**
   * Creates a shape that contains and uses this renderer.
   * 
   * @return a shape that contains and uses this renderer.
   */
  Shape createShape();
  
  /**
   * Invalidates this Renderer, forcing it to recreate
   * the render state (vbos, dl-lists etc.) on the next
   * render.
   * 
   */
  void invalidate();

}
