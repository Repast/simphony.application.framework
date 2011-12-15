package saf.v3d.render;

import java.nio.FloatBuffer;

import javax.vecmath.Point3f;

/**
 * An iterator that produces triangles from list of vertices
 * in quads format. 
 * 
 * @author Nick Collier
 */
public class TriangleQIterator implements TriangleIterator {
  
  static class Quad {
    
    Point3f p1, p2, p3, p4;
    
    public Quad() {
      p1 = new Point3f();
      p2 = new Point3f();
      p3 = new Point3f();
      p4 = new Point3f();
    }
    
    void left(Point3f p1, Point3f p2, Point3f p3) {
      p1.set(this.p1);
      p2.set(this.p2);
      p3.set(this.p3);
    }
    
    void right(Point3f p1, Point3f p2, Point3f p3) {
      p1.set(this.p1);
      p2.set(this.p3);
      p3.set(this.p4);
    }
  }
  
  private FloatBuffer vertices;
  private Quad quad = new Quad();
  private int index = 0;
  private boolean left = true;

  /**
   * @param vertices
   */
  public TriangleQIterator(FloatBuffer vertices) {
    this.vertices = vertices;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return index < vertices.limit();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#next(javax.vecmath.Point3f, javax.vecmath.Point3f, javax.vecmath.Point3f)
   */
  @Override
  public void next(Point3f p1, Point3f p2, Point3f p3) {
    if (left) {
      // get the next quad
      vertices.position(index);
      quad.p1.x = vertices.get();
      quad.p1.y = vertices.get();
      quad.p1.z = vertices.get();
      
      quad.p2.x = vertices.get();
      quad.p2.y = vertices.get();
      quad.p2.z = vertices.get();
      
      quad.p3.x = vertices.get();
      quad.p3.y = vertices.get();
      quad.p3.z = vertices.get();
      
      quad.p4.x = vertices.get();
      quad.p4.y = vertices.get();
      quad.p4.z = vertices.get();
      left = false;
      quad.left(p1, p2, p3);
    } else {
      quad.right(p1, p2, p3);
      index += 12;
      left = true;
    }
  }
}
