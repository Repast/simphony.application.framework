/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import org.jogamp.vecmath.Point3f;

/**
 * An iterator that produces triangles from list of vertices
 * in triangle strip format. 
 * 
 * @author Nick Collier
 */
public class TriangleStripIterator implements TriangleIterator {
  
  private FloatBuffer vertices;
  private int index;
  private Point3f p1, p2;

  /**
   * @param vertices
   */
  public TriangleStripIterator(FloatBuffer vertices) {
    this.vertices = vertices;
    vertices.rewind();
    float[] pt = new float[3];
    p1 = new Point3f();
    vertices.get(pt);
    p1.set(pt);
    
    p2 = new Point3f();
    vertices.get(pt);
    p2.set(pt);
    
    index = 6;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return index < vertices.limit();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#next(org.jogamp.vecmath.Point3f[])
   */
  @Override
  public void next(Point3f p0, Point3f p1, Point3f p2) {
    // p0 from p1, p1 from p2, and p2 is the new point.
    p0.set(this.p1);
    this.p1.set(this.p2);
    p1.set(this.p1);
    vertices.position(index);
    this.p2.x = p2.x = vertices.get();
    this.p2.y = p2.y = vertices.get();
    this.p2.z = p2.z = vertices.get();
    index += 3;
  }
}
