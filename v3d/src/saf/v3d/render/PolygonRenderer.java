/**
 * 
 */
package saf.v3d.render;

/**
 * Interface for Renderers that render polygons and can
 * return an interator over a collection of triangles that 
 * constitute the polygon. Note that these may not be rendered
 * triangles but can be used for intersection testing.
 * 
 * @author Nick Collier
 */
public interface PolygonRenderer extends Renderer {
  
  /**
   * Gets an iterator over the triangles that constitute
   * the polygon this renderer renders.
   * 
   * @return an iterator over the triangles that constitute
   * the polygon this renderer renders.
   */
  TriangleIterator triangleIterator();

}
