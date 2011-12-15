/**
 * 
 */
package saf.v3d.util;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import saf.v3d.render.DisplayListRenderer;
import saf.v3d.render.PolygonRenderer;
import saf.v3d.render.RenderData;

import com.sun.opengl.util.BufferUtil;

/**
 * Adapts the output of GLU tesselation to a VSpatial.
 * 
 * @author Nick Collier
 */
public class Tessellator {

  private GLUtessellator tess;
  private GLU glu;

  private static class Contour {

    List<Point3d> coords = new ArrayList<Point3d>();

    void addVertex(float x, float y, float z) {
      coords.add(new Point3d(x, y, z));
    }

    void addContour(GLU glu, GLUtessellator tessellator) {
      glu.gluTessBeginContour(tessellator);
      for (Point3d pt : coords) {
        double[] vals = new double[3];
        pt.get(vals);
        glu.gluTessVertex(tessellator, vals, 0, vals);
      }
      glu.gluTessEndContour(tessellator);
    }
  }
  
  private static class SliceData {
    int mode, index;

    public SliceData(int mode, int index) {
      this.mode = mode;
      this.index = index;
    }
  }

  private static class Callback extends GLUtessellatorCallbackAdapter {

    private Integer error = null;
    private List<Float> vertices = new ArrayList<Float>();
    private int vertIndex;
    private List<SliceData> slices = new ArrayList<SliceData>();

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#begin(int)
     */
    @Override
    // where mode is GL_TRIANGLES, GL_TRIANGLE_STRIP etc.
    public void begin(int mode) {
      slices.add(new SliceData(mode, vertIndex));
    }

    public boolean hasError() {
      return error != null;
    }

    public String getError() {
      if (error != null) {
        return GLUErrors.getTessErrorString(error.intValue());
      }
      return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.glu.GLUtessellatorCallbackAdapter#vertex(java.lang
     * .Object)
     */
    @Override
    public void vertex(Object vertex) {
      double[] vArray = (double[]) vertex;
      for (double val : vArray) {
        vertices.add((float)val);
      }
      vertIndex += vArray.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.glu.GLUtessellatorCallbackAdapter#error(int)
     */
    @Override
    public void error(int error) {
      this.error = new Integer(error);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.glu.GLUtessellatorCallbackAdapter#combine(double[],
     * java.lang.Object[], float[], java.lang.Object[])
     */
    @Override
    public void combine(double[] coords, Object[] vertexData, float[] weight, Object[] out) {
      double[] vertex = new double[3];
      System.arraycopy(coords, 0, vertex, 0, 3);
      out[0] = vertex;
    }
  }

  public Tessellator(GLU glu) {
    this.glu = glu;
    tess = glu.gluNewTess();
  }

  public PolygonRenderer createRenderer(Path2D.Float path) {
    int windingRule = getGLUWindingRule(path.getWindingRule());
    glu.gluTessProperty(tess, GLU.GLU_TESS_WINDING_RULE, windingRule);
    Callback callback = new Callback();
    glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, callback);
    glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, callback);
    glu.gluTessCallback(tess, GLU.GLU_TESS_ERROR, callback);
    glu.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, callback);

    List<Contour> contours = new ArrayList<Contour>();
    Contour contour = new Contour();
    float[] coords = new float[6];
    Point3f lastPoint = new Point3f();
    for (PathIterator iter = path.getPathIterator(null); !iter.isDone(); iter.next()) {
      int ret = iter.currentSegment(coords);
      if (ret == PathIterator.SEG_MOVETO || ret == PathIterator.SEG_LINETO) {
        contour.addVertex(coords[0], coords[1], 0);
        lastPoint.set(coords[0], coords[1], 0);
      } else if (ret == PathIterator.SEG_CUBICTO) {
        Point3f b = new Point3f(coords[0], coords[1], 0);
        Point3f c = new Point3f(coords[2], coords[3], 0);
        Point3f d = new Point3f(coords[4], coords[5], 0);
        float[] verts = new BezierCurveTessellator(lastPoint, b, c, d).tessellate(20);
        for (int i = 0, n = verts.length; i < n; i += 2) {
          contour.addVertex(verts[i], verts[i + 1], 0);
        }
        contour.addVertex(d.x, d.y, 0);
        lastPoint.set(d);
      } else if (ret == PathIterator.SEG_CLOSE) {
        contours.add(contour);
        contour = new Contour();
      } else {
        throw new IllegalArgumentException("Unsupported path segment type: " + ret + ". Shape cannot contain quadratic bezier curves.");
      }
    }

    if (contours.size() == 0) {
      throw new IllegalArgumentException("Invalid path: path is not closed");
    }

    glu.gluTessBeginPolygon(tess, null);
    for (Contour cont : contours) {
      cont.addContour(glu, tess);
    }
    glu.gluTessEndPolygon(tess);

    // todo use the callbacks to grab the tesselated geometry
    if (callback.hasError()) {
      throw new IllegalArgumentException(callback.getError());
    }
    
    FloatBuffer buf = BufferUtil.newFloatBuffer(callback.vertices.size());
    for (Float f : callback.vertices) {
      buf.put(f.floatValue());
    }
    buf.rewind();
   
    RenderData data = new RenderData(buf);
    for (SliceData slice : callback.slices) {
      data.defineSlice(slice.mode, slice.index);
    }
    
    return new DisplayListRenderer(data);
  }

  private int getGLUWindingRule(int javaWindingRule) {
    if (javaWindingRule == PathIterator.WIND_EVEN_ODD)
      return GLU.GLU_TESS_WINDING_ODD;
    if (javaWindingRule == PathIterator.WIND_NON_ZERO)
      return GLU.GLU_TESS_WINDING_NONZERO;
    throw new IllegalArgumentException("Invalid winding rule: " + javaWindingRule);
  }

}
