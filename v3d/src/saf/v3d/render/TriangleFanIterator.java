/**
 * 
 */
package saf.v3d.render;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.jogamp.vecmath.Point3f;

/**
 * An iterator that produces triangles from list of vertices
 * in triangle fan format. 
 * 
 * @author Nick Collier
 */
public class TriangleFanIterator implements TriangleIterator {
  
  private FloatBuffer vertices;
  private int index;
  private Point3f origin, p2;

  /**
   * @param vertices
   */
  public TriangleFanIterator(FloatBuffer vertices) {
    this.vertices = vertices;
    ((Buffer)vertices).rewind();
    float[] pt = new float[3];
    origin = new Point3f();
    vertices.get(pt);
    origin.set(pt);
    
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
    return index < ((Buffer)vertices).limit();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#next(org.jogamp.vecmath.Point3f[])
   */
  @Override
  public void next(Point3f p0, Point3f p1, Point3f p2) {
    p0.set(origin);
    // put p2 in p1 and get the new p2.
    p1.set(this.p2);
    ((Buffer)vertices).position(index);
    this.p2.x = p2.x = vertices.get();
    this.p2.y = p2.y = vertices.get();
    this.p2.z = p2.z = vertices.get();
    index += 3;
  }
}
