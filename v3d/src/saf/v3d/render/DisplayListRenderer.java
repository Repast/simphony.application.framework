/**
 * 
 */
package saf.v3d.render;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;


/**
 * Renders vertices via a display list.
 * 
 * @author Nick Collier
 */
public class DisplayListRenderer implements PolygonRenderer {
  
  private RenderData renderData;
  private int listIndex = 0;
  private boolean invalid = true;
  
  /**
   * Creates a DisplayListRenderer that will render a some
   * geometry using the specified renderdata via a display list.
   * 
   * @param data contains the data (geometry etc.) used to do
   * the actual render
   */
  public DisplayListRenderer(RenderData data) {
    this.renderData = data;
  }

  /**
   * Creates a DisplayListRender that will render the specified vertices via
   * a display list.
   * 
   * @param vertices
   * @param mode
   */
  public DisplayListRenderer(FloatBuffer vertices, int mode) {
    renderData = new RenderData(vertices);
    renderData.defineSlice(mode, 0);
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Renderer#dispose(com.jogamp.opengl.GL, int)
   */
  // no-op here because all disposal is done through the DLRenderManager
  @Override
  public void dispose(GL2 gl) {
    if (listIndex != 0) gl.glDeleteLists(listIndex, 1);
    invalid = true;
    listIndex = 0;
  }
  
  private void init(GL2 gl) {
    listIndex = gl.glGenLists(1);
    gl.glNewList(listIndex, GL2.GL_COMPILE);
    renderData.renderImmediate(gl);
    gl.glEndList();
  }

  /**
   * Draw the geometry contained by this GeometryData.
   * 
   * @param gl
   */
  public void render(GL2 gl, RenderState rState) {
    if (invalid) {
      //DLRenderManager dlManager = GLRegistry.getInstance().getDLRenderManager(rState.canvasId);
      //listIndex = dlManager.getDisplayList(gl, renderData);
      dispose(gl);
      init(gl);
      invalid = false;
    }
    
    rState.border.drawBorder(gl, listIndex, rState);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, rState.PolyMode);
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
   * Gets an iterator over the triangles that constitute
   * the polygon this renderer renders.
   * 
   * @return an iterator over the triangles that constitute
   * the polygon this renderer renders.
   */
  public TriangleIterator triangleIterator() {
    return renderData.triangleIterator();
  }

  /**
   * Gets the vertices rendered by the DisplayListRenderer.
   * 
   * @return the vertices rendered by the DisplayListRenderer.
   */
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
