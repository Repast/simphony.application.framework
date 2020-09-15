package saf.v3d.util;

import org.jogamp.vecmath.Point3f;

/**
 * 2D Bezier curve tessellation.
 * 
 * @author Nick Collier
 */
public class BezierCurveTessellator {

  private Point3f p0, p1, p2, p3;

  /**
   * Creates a BCTesselleator using the specified control points.
   * 
   * @param a
   * @param b
   * @param c
   * @param d
   */
  public BezierCurveTessellator(Point3f a, Point3f b, Point3f c, Point3f d) {
    this.p0 = new Point3f(a);
    this.p1 = new Point3f(b);
    this.p2 = new Point3f(c);
    this.p3 = new Point3f(d);
  }

  /**
   * Tessalate the curve into the specified number of edges.
   * 
   * @param edge
   * @return
   */
  public float[] tessellate(int edges) {
    float[] vertices = new float[(edges + 1) * 2];
    for (int i = 0; i <= edges; i++) {
      float a = ((float) i) / edges;
      float b = 1 - a;
      // x coord
      vertices[i * 2] = p0.x * b * b * b + p1.x * 3 * b * b * a + p2.x * 3 * b * a * a + p3.x * a * a * a;
      // y coord
      vertices[i * 2 + 1] = p0.y * b * b * b + p1.y * 3 * b * b * a + p2.y * 3 * b * a * a + p3.y * a * a * a;
    }
    return vertices;
  }
}
