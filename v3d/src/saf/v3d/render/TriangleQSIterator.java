package saf.v3d.render;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.jogamp.vecmath.Point3f;

/**
 * An iterator that produces triangles from list of vertices in quads format.
 * 
 * @author Nick Collier
 */
public class TriangleQSIterator implements TriangleIterator {

  static class Quad {

    Point3f p0, p1, p2, p3;

    public Quad() {
      p0 = new Point3f();
      p1 = new Point3f();
      p2 = new Point3f();
      p3 = new Point3f();
    }

    void left(Point3f p1, Point3f p2, Point3f p3) {
      p1.set(this.p0);
      p2.set(this.p1);
      p3.set(this.p2);
    }

    void right(Point3f p1, Point3f p2, Point3f p3) {
      p1.set(this.p2);
      p2.set(this.p3);
      p3.set(this.p1);
    }
  }

  private FloatBuffer vertices;
  private Quad quad = new Quad();
  private int index = 0;
  private boolean left = true;

  /**
   * @param vertices
   */
  public TriangleQSIterator(FloatBuffer vertices) {
    this.vertices = vertices;
    if (((Buffer)vertices).limit() > 5) {
      ((Buffer)vertices).rewind();
      quad.p2.x = vertices.get();
      quad.p2.y = vertices.get();
      quad.p2.z = vertices.get();

      quad.p3.x = vertices.get();
      quad.p3.y = vertices.get();
      quad.p3.z = vertices.get();
      
      index = 6;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.TriangleIterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return index < ((Buffer)vertices).limit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.TriangleIterator#next(org.jogamp.vecmath.Point3f,
   * org.jogamp.vecmath.Point3f, org.jogamp.vecmath.Point3f)
   */
  @Override
  public void next(Point3f p1, Point3f p2, Point3f p3) {
    if (left) {
      // get the next quad by setting quad.p0 to quad.p2
      // and quad.p1 to quad.p3, then get the next two points.
      quad.p0.set(quad.p2);
      quad.p1.set(quad.p3);
      ((Buffer)vertices).position(index);
      quad.p2.x = vertices.get();
      quad.p2.y = vertices.get();
      quad.p2.z = vertices.get();

      quad.p3.x = vertices.get();
      quad.p3.y = vertices.get();
      quad.p3.z = vertices.get();
      
      left = false;
      quad.left(p1, p2, p3);
    } else {
      quad.right(p1, p2, p3);
      index += 6;
      left = true;
    }
  }
}
