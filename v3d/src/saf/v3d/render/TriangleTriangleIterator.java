/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import javax.vecmath.Point3f;

/**
 * An iterator that produces triangles from list of vertices
 * in triangles format. 
 * 
 * @author Nick Collier
 */
public class TriangleTriangleIterator implements TriangleIterator {
  
  private FloatBuffer vertices;
  private int index;

  /**
   * @param vertices
   */
  public TriangleTriangleIterator(FloatBuffer vertices) {
    this.vertices = vertices;
    index = 0;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return index < vertices.limit();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#next(javax.vecmath.Point3f[])
   */
  @Override
  public void next(Point3f p0, Point3f p1, Point3f p2) {
    vertices.position(index);
    p0.x = vertices.get();
    p0.y = vertices.get();
    p0.z = vertices.get();
    p1.x = vertices.get();
    p1.y = vertices.get();
    p1.z = vertices.get();
    p2.x = vertices.get();
    p2.y = vertices.get();
    p2.z = vertices.get();
    index += 9;
  }
}
