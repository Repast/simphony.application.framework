/**
 * 
 */
package saf.v3d.util;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Color4f;
import org.jogamp.vecmath.Matrix4f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Tuple3f;
import org.jogamp.vecmath.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.gl2.GLUgl2;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.picking.Box;
import saf.v3d.scene.VSpatial;

/**
 * Static utililty methods for 3D.
 * 
 * @author Nick Collier
 */
public class Utils3D {
  
  public enum Location {NORTH_EAST};
  
  private static final float K_EPSILON = 1.0e-6f;
  private static final float NE_SIN = (float)Math.sin(Math.toRadians(45));
  private static final float NE_COS = (float)Math.cos(Math.toRadians(45));
  
  public static void colorToFloats(float[] array, Color color) {
    array[0] = color.getRed() / 255f;
    array[1] = color.getGreen() / 255f;
    array[2] = color.getBlue() / 255f;
    array[3] = color.getAlpha() / 255f;
  }
  
  /**
   * Puts the contents of the matrix into the buffer
   * and rewinds the buffer so that it is ready to use.
   * 
   * @param matrix
   * @param buf
   */
  public static void matrixToBuff(Matrix4f matrix, FloatBuffer buf) {
    buf.rewind();
    buf.put(matrix.m00);
    buf.put(matrix.m10);
    buf.put(matrix.m20);
    buf.put(matrix.m30);
    buf.put(matrix.m01);    
    buf.put(matrix.m11);
    buf.put(matrix.m21);
    buf.put(matrix.m31);
    buf.put(matrix.m02);
    buf.put(matrix.m12);
    buf.put(matrix.m22);
    buf.put(matrix.m32);
    buf.put(matrix.m03);
    buf.put(matrix.m13);
    buf.put(matrix.m23);
    buf.put(matrix.m33);
    buf.rewind();
  }
  
  /**
   * Checks if equals zero within some epsilon.
   *
   * @param val
   * @return true if the val is close enough to zero otherwise false
   */
  public static boolean isZero(float val) {
    return Math.abs(val) < K_EPSILON;
  }
  
	public static Color floatToColor(float[] color) {
		return new Color(color[0], color[1], color[2], color[3]);
	}

	public static void updateColor(Color3f color3f, Color color) {
		color3f.set((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f,
				(float) color.getBlue() / 255.0f);
	}
	
	public static void updateColor(Color4f color4f, Color color) {
		color4f.set((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f,
				(float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
	}

	public static Color getColor(Color3f color) {
		int r = Math.round(color.getX() * 255.0f);
		int g = Math.round(color.getY() * 255.0f);
		int b = Math.round(color.getZ() * 255.0f);

		return new Color(r, g, b);
	}
	
	public static Color getColor(Color4f color) {
		int r = Math.round(color.getX() * 255.0f);
		int g = Math.round(color.getY() * 255.0f);
		int b = Math.round(color.getZ() * 255.0f);
		int a = Math.round(color.getW() * 255.0f);

		return new Color(r, g, b, a);
	}
  
  
  /**
   * Assumes quat is normalized. Rotates in tuple by the
   * rotation defined by the quaterion.
   * 
   * @param rotation
   * @param in
   * @param out
   * @return
   */
  public static Tuple3f mult(Quat4f rotation, Tuple3f in, Tuple3f out) {
    if (out == null) out = new Vector3f();
    float w = rotation.w;
    float x = rotation.x;
    float y = rotation.y;
    float z = rotation.z;
    
    float pMult = w*w - x*x - y*y - z*z;
    float vMult = 2.0f*(x*in.x + y*in.y + z*in.z);
    float crossMult = 2.0f*w;

    out.set( pMult*in.x + vMult*x + crossMult*(y*in.z - z*in.y),
                      pMult*in.y + vMult*y + crossMult*(z*in.x - x*in.z),
                      pMult*in.z + vMult*z + crossMult*(x*in.y - y*in.x) );
    return out;
  }
  
  /**
   * Gets the location relative to the specified VSpatial
   * in terms of the VSpatial's local frame.
   * 
   * @param item the VSpatial to get location relative to
   * @param location the type of location we want
   * @return the location relative to the specified VSpatial.
   */
  public static Point3f getRelativeLocation(VSpatial item, Location location) {
    
    BoundingSphere sphere = item.getLocalBoundingSphere();
    Point3f pt = new Point3f(sphere.getCenterRef());
    float radius = sphere.getRadius();
    if (location == Location.NORTH_EAST) {
      float x = radius * NE_SIN;
      float y = radius * NE_COS;
      pt.x += x;
      pt.y += y;
      
      return pt;
    }
   

    return null;
  }
  
  /**
   * Computes the distance squared between the specified point and the line
   * described by start, end.
   * 
   * @param pt
   * @param origin
   * @param end
   * 
   * @return the distance squared between the specified point and the line
   * described by start, end.
   */
  public static float distanceSquared(Point3f pt, Point3f origin, Point3f end) {
    Vector3f dir = new Vector3f(end);
    dir.sub(origin);
    Vector3f w = new Vector3f();
    w.sub(pt, origin);
    float proj = w.dot(dir);
    
    if (proj <= 0) return w.dot(w);
    else {
      float vsq = dir.dot(dir);
      if (proj >= vsq) return w.dot(w) - 2.0f * proj + vsq;
      else return w.dot(w) - proj * proj / vsq;
    }
    
  }
  
  /**
   * Creates a bounding sphere that will encompass the vertices in the
   * specified buffer. 
   * 
   * @param buf the Buffer containing the vertices' coordinates
   * @param size the number of coordinates per vertex
   * @return
   */
  public static BoundingSphere createBoundingSphere(FloatBuffer buf) {
    // compute minimal and maximal bounds 
    buf.rewind();
    float[] vertex = new float[3];
    buf.get(vertex);
    Point3f min = new Point3f(vertex);
    Point3f max = new Point3f(vertex);
    
    while (buf.hasRemaining()) {
      buf.get(vertex);
      float x = vertex[0];
      float y = vertex[1];
      float z = vertex[2];
      
      if (min.x > x) min.x = x;
      else if (max.x < x) max.x = x;
      
      if (min.y > y) min.y = y;
      else if (max.y < y) max.y = y;
      
      if (min.z > z) min.z = z;
      else if (max.z < z) max.z = z;
    }
    
    // center half-way between min and max
    Point3f center = new Point3f(min);
    center.add(max);
    center.scale(.5f);
    
    // compute radius
    buf.rewind();
    buf.get(vertex);
    Point3f pt = new Point3f(vertex);
    float maxDist = center.distanceSquared(pt);
    while (buf.hasRemaining()) {
      buf.get(vertex);
      pt.set(vertex);
      float dist = center.distanceSquared(pt);
      if (dist > maxDist) {
	maxDist = dist;
      }
    }
    
    buf.rewind();
    return new BoundingSphere(center, (float)Math.sqrt(maxDist));
  }

  /**
   * Computes whether or not point pt intersects with the 
   * triangld described by t1, t2, and t3.
   * @param pt
   * @param t1
   * @param t2
   * @param t3
   * 
   * @return whether or not the point intersects the triangle.
   */
  // from http://www.blackpawn.com/texts/pointinpoly/default.html
  public static boolean triangleIntersect(Point3f pt, Point3f t1, Point3f t2, Point3f t3) {
    Vector3f v0 = new Vector3f();
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    v0.sub(t3, t1);
    v1.sub(t2, t1);
    v2.sub(pt, t1);
    
    // compute dot products
    float dot00  = v0.dot(v0);
    float dot01  = v0.dot(v1);
    float dot02  = v0.dot(v2);
    float dot11  = v1.dot(v1);
    float dot12  = v1.dot(v2);
    
    // compute barycentric coords
    float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    // Check if point is in triangle
    return (u > 0) && (v > 0) && (u + v < 1);
  }
  
  /**
   * Computes whether or not the triangle defined by P0, P1, and P2
   * is intersected by the the ray.
   * 
   * @param p0
   * @param p1
   * @param p2
   * @param rayOrigin
   * @param rayDirection
   * 
   * @return true if the triangle is intersected by the ray, otherwise false.
   */
  public static boolean triangleIntersect(Point3f p0, Point3f p1, Point3f p2,
      Point3f rayOrigin, Vector3f rayDirection) {
    Vector3f e1 = new Vector3f();
    e1.sub(p1, p0);
    Vector3f e2 = new Vector3f();
    e2.sub(p2, p0);
    
    Vector3f p = new Vector3f();
    p.cross(rayDirection, e2);
    float a = e1.dot(p);
    
    // if result zero, no intersection or infinite intersections
    // (ray parallel to triangle plane)
    if (isZero(a)) return false;
    
    // compute denominator
    float f = 1.0f/a;
    
    // compute barycentric coordinates
    Vector3f s = new Vector3f();
    s.sub(rayOrigin, p0); 
    float u = f*s.dot(p);
    
    // ray falls outside triangle
    if (u < 0.0f || u > 1.0f) return false;

    
    Vector3f q = new Vector3f();
    q.cross(s, e1); 
    float v = f* rayDirection.dot(q);
    
    // ray falls outside triangle
    if (v < 0.0f || u+v > 1.0f) return false;

    // compute line parameter
    float t = f*e2.dot(q);
    return (t >= 0.0f);
  }
  
  /**
   * Computes the normal of the specified triangle.
   * 
   * @param pt1
   * @param pt2
   * @param pt3
   */
  public static void triangleNormal(Point3f pt1, Point3f pt2, Point3f pt3, Vector3f result) {
    result.set(pt2);
    result.sub(pt1);
    
    Vector3f v2 = new Vector3f(pt3);
    v2.sub(pt2);
    
    result.cross(result, v2);
    result.normalize();
  }
  
  /**
   * Creates a BoundingSphere that will enclose the rectangle.
   * The z coordinate is assumed to be 0. Center is the rect's
   * center and radius is the distance from center to rect corner.
   * 
   * @param rect the rectangle to bound
   * @return the created sphere
   */
  public static BoundingSphere createBoundingSphere(Rectangle2D rect) {
    Point3f center = new Point3f((float)rect.getCenterX(), (float)rect.getCenterY(), 0f);
    double xExtent = rect.getWidth();
    double yExtent = rect.getHeight();
    // distance from one corner to opposite divided by 2
    float radius = (float)(Math.sqrt(xExtent * xExtent + yExtent * yExtent) / 2f);
    return new BoundingSphere(center, radius);
  }
  
  public static Box getWorldSizeAtZ(GL2 gl, float nearClipZ, float farClipZ, float z) {
    
    IntBuffer viewport = Buffers.newDirectIntBuffer(4);
    DoubleBuffer mvMatrix = Buffers.newDirectDoubleBuffer(16);
    DoubleBuffer projMatrix = Buffers.newDirectDoubleBuffer(16);
    DoubleBuffer output = Buffers.newDirectDoubleBuffer(3);
    GLUgl2 glu = new GLUgl2();
    
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
    gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvMatrix);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projMatrix);
    
    //System.out.printf("cursor coords: %d, %d%n",x, ogY);
    glu.gluUnProject(0, 0, 0, mvMatrix, projMatrix, viewport, output);
    //System.out.printf("world coords at z = 1.0 are (%f, %f, %f)%n", output.get(0), output.get(1), output.get(2));
   
    Point3f nearLower = new Point3f();
    nearLower.x = (float)output.get(0);
    nearLower.y = (float)output.get(1);
    nearLower.z = (float)output.get(2);
    //System.out.println("nearLower = " + nearLower);
    
    glu.gluUnProject(viewport.get(2), viewport.get(3), 0, mvMatrix, projMatrix, viewport, output);
    
    
    Point3f nearUpper = new Point3f();
    nearUpper.x = (float)output.get(0);
    nearUpper.y = (float)output.get(1);
    nearUpper.z = (float)output.get(2);
    
    //System.out.println("nearUpper = " + nearUpper);
    
    glu.gluUnProject(0, 0, 1f, mvMatrix, projMatrix, viewport, output);
    //System.out.printf("world coords at z = 1.0 are (%f, %f, %f)%n", output.get(0), output.get(1), output.get(2));
   
    Point3f farLower = new Point3f();
    farLower.x = (float)output.get(0);
    farLower.y = (float)output.get(1);
    farLower.z = (float)output.get(2);
    //System.out.println("farLower = " + farLower);
    
    glu.gluUnProject(viewport.get(2), viewport.get(3), 1f, mvMatrix, projMatrix, viewport, output);
    
    Point3f farUpper = new Point3f();
    farUpper.x = (float)output.get(0);
    farUpper.y = (float)output.get(1);
    farUpper.z = (float)output.get(2);
    
    float interval = (nearClipZ - z) / Math.abs((farClipZ - nearClipZ));
    
    Vector3f dir = new Vector3f(farLower);
    dir.sub(nearLower);
    
    Point3f lower = new Point3f();
    lower.x = nearLower.x + (dir.x * interval);
    lower.y = nearLower.y + (dir.y * interval);
    lower.z = z;
    
    dir.set(farUpper);
    dir.sub(nearUpper);
    
    Point3f upper = new Point3f();
    upper.x = nearUpper.x + (dir.x * interval);
    upper.y = nearUpper.y + (dir.y * interval);
    upper.z = z;
    
    return new Box(lower, upper);
  }
}
