/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Renders Vertex data via a vbo.
 * 
 * @author Nick Collier
 */
public class VertexVBORenderer implements PolygonRenderer {
  
  private int vboIndex;
  private RenderData renderData;
  private boolean invalid = true;

  /**
   * Creates a VertexVBORenderer that will render the specified
   * vertices as a vbo.
   * 
   * @param vertices
   */
  public VertexVBORenderer(RenderData data) {
    this.renderData = data;
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Renderer#invalidate()
   */
  @Override
  public void invalidate() {
    invalid = true;
  }
  
  private void initVBO(GL gl) {
 // create the vbo
    int[] indices = new int[1];
    gl.glGenBuffers(1, indices, 0);
    vboIndex = indices[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIndex);
    FloatBuffer buf = renderData.getVertices();
    buf.rewind();
    gl.glBufferData(GL.GL_ARRAY_BUFFER, buf.capacity() * 3 * Buffers.SIZEOF_FLOAT, buf, 
        GL.GL_STATIC_DRAW);
    invalid = false;
  }

  /**
   * Creates a DisplayListRender that will render the specified vertices via
   * a display list.
   * 
   * @param vertices
   * @param mode
   */
  public VertexVBORenderer(FloatBuffer vertices, int mode) {
    renderData = new RenderData(vertices);
    renderData.defineSlice(mode, 0);
  }
  
  /**
   * Deletes the vbo used by this VertexVBORenderer.
   */
  public void dispose(GL2 gl) {
    if (vboIndex != 0) {
      gl.glDeleteBuffers(1, new int[]{vboIndex}, 0);
      invalid = true;
    }
  }
  
  
  /**
   * Draw the geometry contained by this GeometryData.
   * 
   * @param gl
   */
  public void render(GL2 gl, RenderState rState) {
    if (invalid) initVBO(gl);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    if (rState.vboIndex != vboIndex) {
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboIndex);
      rState.vboIndex = vboIndex;
    } 
    renderData.renderVBO(gl);
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
  }
  
  
  /* (non-Javadoc)
   * @see saf.v3d.render.PolygonRenderer#triangleIterator()
   */
  @Override
  public TriangleIterator triangleIterator() {
    return renderData.triangleIterator();
  }

  public FloatBuffer getVertices() {
    return renderData.getVertices();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.Renderer#createShape()
   */
  @Override
  public Shape createShape() {
    return new PolygonShape(this);
  }
}
