/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * Encapsulates a single set of vertices and the modes used to render slices of
 * that set.
 * 
 * @author Nick Collier
 */
public class RenderData {

  private static class Slice {
    int mode, start;

    public Slice(int mode, int start) {
      this.mode = mode;
      this.start = start;
    }
  }

  private FloatBuffer vertices;
  private List<Slice> slices = new ArrayList<Slice>();

  public RenderData(FloatBuffer vertices) {
    this.vertices = vertices;
  }

  /**
   * Defines a the mode and start index for a slice of the vertex data.
   * 
   * @param mode
   *          the mode
   * @param start
   *          the start index
   */
  public void defineSlice(int mode, int start) {
    slices.add(new Slice(mode, start));
    sortSlices();
  }

  /**
   * Performs an immediate mode render over the slices. A call to this should be
   * bounded by the creation of a display list.
   * 
   * @param gl
   */
  public void renderImmediate(GL2 gl) {
    vertices.rewind();
    sortSlices();

    for (int i = 0, n = slices.size(); i < n; i++) {
      Slice slice = slices.get(i);
      int startIndex = slice.start;
      int endIndex = i + 1 == n ? vertices.limit() : slices.get(i + 1).start;

      gl.glBegin(slice.mode);
      for (int j = startIndex; j < endIndex; j += 3) {
        gl.glVertex3f(vertices.get(j), vertices.get(j + 1), vertices.get(j + 2));
      }
      gl.glEnd();
    }
  }

  /**
   * Renders this render data assuming the 
   * vertices have bound in a vbo.
   * 
   * @param gl
   */
  public void renderVBO(GL2 gl) {
    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
    for (int i = 0, n = slices.size(); i < n; i++) {
      Slice slice = slices.get(i);
      int startIndex = slice.start;
      int endIndex = i + 1 == n ? vertices.limit() : slices.get(i + 1).start;
      gl.glDrawArrays(slice.mode, startIndex / 3, (endIndex - startIndex) / 3);
    }
  }

  /**
   * Gets the vertices contains by this RenderData.
   * 
   * @return the vertices contains by this RenderData.
   */
  public FloatBuffer getVertices() {
    return vertices;
  }

  private void sortSlices() {
    Collections.sort(slices, new Comparator<Slice>() {
      @Override
      public int compare(Slice o1, Slice o2) {
        return o1.start < o2.start ? -1 : o1.start > o2.start ? 1 : 0;
      }
    });
  }

  /**
   * Gets an iterator over the triangles that constitute the polygon this
   * renderer renders. This is not for drawing but for intersection testing.
   * 
   * @return an iterator over the triangles that constitute the polygon this
   *         renderer renders.
   */
  public TriangleIterator triangleIterator() {
    vertices.rewind();
    sortSlices();
    List<TriangleIterator> iterators = new ArrayList<TriangleIterator>();
    for (int i = 0, n = slices.size(); i < n; i++) {
      Slice slice = slices.get(i);
      int startIndex = slice.start;
      int endIndex = i + 1 == n ? vertices.limit() : slices.get(i + 1).start;
      int mode = slice.mode;
      float[] buf = new float[endIndex - startIndex];
      vertices.position(startIndex);
      vertices.get(buf, 0, buf.length);
      if (mode == GL2.GL_TRIANGLE_STRIP) {
        iterators.add(new TriangleStripIterator(FloatBuffer.wrap(buf)));
      } else if (mode == GL2.GL_TRIANGLE_FAN) {
        iterators.add(new TriangleFanIterator(FloatBuffer.wrap(buf)));
      } else if (mode == GL2.GL_TRIANGLES) {
        iterators.add(new TriangleTriangleIterator(FloatBuffer.wrap(buf)));
      } else if (mode == GL2.GL_QUADS) {
        iterators.add(new TriangleQIterator(FloatBuffer.wrap(buf)));
      } else if (mode == GL2.GL_QUAD_STRIP) {
        iterators.add(new TriangleQSIterator(FloatBuffer.wrap(buf)));
      } else {
        throw new IllegalArgumentException("Unable to create triangle iterator for mode");
      }
    }

    return new CompositeTriangleIterator(iterators);
  }
}
