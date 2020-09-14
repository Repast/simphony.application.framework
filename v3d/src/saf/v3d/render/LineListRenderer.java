/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import javax.vecmath.Point3f;

import saf.v3d.util.Utils3D;


/**
 * Renders lines.
 * 
 * @author Nick Collier
 */
public class LineListRenderer implements Renderer {
  
  private static final float DELTA = 5f;

  private FloatBuffer vertices;
  private int listIndex;
  private int[] moveTo;
  private boolean invalid = true;
  /**
   * Creates a LineListRender that will render the specified vertices via
   * a display list.
   * 
   * @param vertices
   */
  public LineListRenderer(FloatBuffer vertices, int[] moveTo) {
    this.vertices = vertices;
    this.moveTo = moveTo;
    this.vertices.rewind();
  }
  
  // initialize the display list
  private void init(GL2 gl) {
    listIndex = gl.glGenLists(1);
    vertices.rewind();
    gl.glNewList(listIndex, GL2.GL_COMPILE);
    for (int i = 0; i < moveTo.length; i++) {
      int startIndex = moveTo[i];
      int endIndex = i + 1 == moveTo.length ? vertices.limit()  : moveTo[i + 1];
      gl.glBegin(GL.GL_LINE_STRIP);
      for (int j = startIndex; j < endIndex; j += 3) {
        gl.glVertex3f(vertices.get(j), vertices.get(j + 1), vertices.get(j + 2));
      }
      gl.glEnd();
    }
    gl.glEndList();
    invalid = false;
  }
  
  public boolean intersects(Point3f pt) {
    for (int i = 0; i < moveTo.length; i++) {
      int startIndex = moveTo[i];
      int endIndex = i + 1 == moveTo.length ? vertices.limit()  : moveTo[i + 1];
      
      Point3f start = new Point3f(vertices.get(startIndex), vertices.get(startIndex + 1), vertices.get(startIndex + 2));
      Point3f end = new Point3f();
      for (int j = startIndex + 3; j < endIndex; j += 3) {
        end.set(vertices.get(j), vertices.get(j + 1), vertices.get(j + 2));
        float distSq = Utils3D.distanceSquared(pt, start, end);
        System.out.println("distance: " + distSq);
        if (distSq <= DELTA) return true;
        
        start.set(end);
      }
    }
    
    return false;
  }

  /**
   * Disposes of any resources used by this renderer.
   * 
   * @param gl
   */
  public void dispose(GL2 gl) {
    if (listIndex != 0) {
      gl.glDeleteLists(listIndex, 1);
      invalid = true;
    }
  }

  /**
   * Draw the geometry contained by this GeometryData.
   * 
   * @param gl
   */
  public void render(GL2 gl, RenderState rState) {
    if (invalid) init(gl);
    gl.glLineWidth(rState.lineWidth);
    rState.appearance.applyAppearance(gl);
    gl.glCallList(listIndex);
  }
  
  
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Renderer#invalidate()
   */
  @Override
  public void invalidate() {
    invalid = true;
  }

  /**
   * Gets the vertex mode (GL.GL_TRIANGLE_STRIP etc.) of the
   * vertices rendered by this renderer.
   * 
   * @return the vertex mode (GL.GL_TRIANGLE_STRIP etc.) of the
   * vertices rendered by this renderer.
   */
  public int getMode() {
    return GL.GL_LINE_STRIP;
  }

  /**
   * Gets the vertices rendered by the DisplayListRenderer.
   * 
   * @return the vertices rendered by the DisplayListRenderer.
   */
  public FloatBuffer getVertices() {
    return vertices;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Renderer#createShape()
   */
  @Override
  public Shape createShape() {
    return new LineShape(this);
  }
}
