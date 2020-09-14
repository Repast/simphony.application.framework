/**
 * 
 */
package saf.v3d.grid;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;
import saf.v3d.render.Shape;

import com.jogamp.common.nio.Buffers;

/**
 * @author Nick Collier
 */
public class GridMesh implements Shape {

  private GridColorMap colorMap;
  private boolean rowMajor, update;
  private FloatBuffer colorBuf;
  private BoundingSphere sphere;
  private int rows, cols, major, minor, numVertInStrip;
  private float unitSize;
  private int verticesIndex, colorsIndex;
  private boolean invalid = true;

  public GridMesh(int rows, int cols, float unitSize, GridColorMap colorMap) {
    this.colorMap = colorMap;
    this.rows = rows;
    this.cols = cols;
    this.unitSize = unitSize;
    minor = Math.min(rows, cols);
    major = Math.max(rows, cols);
    // duplicating the verts where the strips meet
    numVertInStrip = (major + 1) * 2;
    rowMajor = rows > cols;
    colorBuf = Buffers.newDirectFloatBuffer( numVertInStrip * minor * 3);

    Point3f center = new Point3f(cols * unitSize / 2f, rows * unitSize / 2f, 0);
    sphere = new BoundingSphere(center, center.distance(new Point3f(0, 0, 0)));
  }
  
  

  /**
   * 2 vbos are created, one each for vertices and colors. Each vbo is divided
   * into "strips" corresponding to a row or column (depending which is larger)
   * of the grid.
   * 
   * @param gl
   */
  private void initializeVBO(GL gl) {
    // 1 each for verts, and colors
    // the indices and colors are divided into "strips"
    int[] vboIndices = new int[2];
    gl.glGenBuffers(vboIndices.length, vboIndices, 0);
    int vertCount = numVertInStrip * minor;

    // calculate the vertices
    FloatBuffer vertices = Buffers.newDirectFloatBuffer(vertCount * 2);
    if (rowMajor) {
      for (int i = 0; i < minor; i++) {
        float x = i * unitSize;
        // TODO this should be the grid's origin
        float y = 0;
        for (int v = 0; v < numVertInStrip / 2; v++) {
          vertices.put(x);
          vertices.put(y);
          vertices.put(x + unitSize);
          vertices.put(y);
          y += unitSize;
        }
      }
    } else {
      for (int i = 0; i < minor; i++) {
        float y = i * unitSize;
        // TODO this should be the grid's origin
        float x = 0;
        for (int v = 0; v < numVertInStrip / 2; v++) {
          vertices.put(x);
          vertices.put(y);
          vertices.put(x);
          vertices.put(y + unitSize);
          x += unitSize;
        }
      }
    }

    verticesIndex = vboIndices[0];
    vertices.rewind();
    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, verticesIndex);
    gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertCount * 2 * Buffers.SIZEOF_FLOAT, vertices,
        GL2.GL_STATIC_DRAW);

  
    updateColors();
    colorsIndex = vboIndices[1];
    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorsIndex);
    colorBuf.rewind();
    gl.glBufferData(GL2.GL_ARRAY_BUFFER, colorBuf.limit() * Buffers.SIZEOF_FLOAT, colorBuf,
        GL2.GL_DYNAMIC_DRAW);
    
    invalid = false;
  }
  
  public void update() {
    update = true;
    updateColors();
  }
  
  private void updateColors() {
    colorBuf.rewind();
    Color3f color = new Color3f();
    if (rowMajor) {
      for (int min = 0; min < minor; min++) {
        int x = min, y = 0;
        colorMap.getColor(x, y, color);
        // do the first two of each strip
        colorBuf.put(color.x);
        colorBuf.put(color.y);
        colorBuf.put(color.z);
        colorBuf.put(color.x);
        colorBuf.put(color.y);
        colorBuf.put(color.z);
       
        for (int i = 2; i < numVertInStrip; i += 2, y++) {
          
          colorMap.getColor(x, y, color);
          
          colorBuf.put(color.x);
          colorBuf.put(color.y);
          colorBuf.put(color.z);
          colorBuf.put(color.x);
          colorBuf.put(color.y);
          colorBuf.put(color.z);
        }
      }
    } else {
      for (int min = 0; min < minor; min++) {
        int y = min, x = 0;
        colorMap.getColor(x, y, color);
        // do the first two of each strip
        colorBuf.put(color.x);
        colorBuf.put(color.y);
        colorBuf.put(color.z);
        colorBuf.put(color.x);
        colorBuf.put(color.y);
        colorBuf.put(color.z);
        
        for (int i = 2; i < numVertInStrip; i += 2, x++) {
         
          colorMap.getColor(x, y, color);
          
          colorBuf.put(color.x);
          colorBuf.put(color.y);
          colorBuf.put(color.z);
          colorBuf.put(color.x);
          colorBuf.put(color.y);
          colorBuf.put(color.z);
          
        }
      }
    }
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.Shape#getLocalBounds()
   */
  @Override
  public BoundingSphere getLocalBounds() {
    return sphere;
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f,
   * javax.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    throw new UnsupportedOperationException("Not Yet Implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.Shape#intersects(javax.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    return (point.x >= 0 && point.x <= cols * unitSize && point.y >= 0 && point.y <= rows * unitSize);
  }
  
  /**
   * Gets the grid cell location for the specified point. The point is 
   * in local coordinates and this returns the cell location in the
   * grid coordinates.
   * 
   * @param point
   * @return grid cell location for the specified point, or null if the 
   * point is outside of the grid.
   */
  public int[] getCellLocation(Point3f point) {
    if (intersects(point)) {
      int x = (int) Math.floor(point.x / unitSize);
      int y = (int) Math.floor(point.y / unitSize);
      return new int[]{x, y};
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.render.Shape#render(com.jogamp.opengl.GL,
   * saf.v3d.render.RenderState)
   */
  @Override
  public void render(GL2 gl, RenderState state) {
    if (invalid) {
	initializeVBO(gl);
    }
    gl.glShadeModel(GL2.GL_FLAT);
    //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE );
    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, verticesIndex);
    gl.glVertexPointer(2, GL2.GL_FLOAT, 0, 0);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    
    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorsIndex);
    if (update) {
      colorBuf.rewind();
      gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, colorBuf.limit() * Buffers.SIZEOF_FLOAT, colorBuf);
      update = false;
    }
    
    gl.glColorPointer(3, GL2.GL_FLOAT, 0, 0);
    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);

    // number of verts in a strip: (major + 1) * 2
    int major = Math.max(rows, cols);
    int minor = Math.min(rows, cols);
    int vertsInStrip = (major + 1) * 2;
    for (int i = 0; i < minor; i++) {
      gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, vertsInStrip * i, vertsInStrip);
    }

    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
    gl.glShadeModel(GL2.GL_SMOOTH);
    state.vboIndex = colorsIndex;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.render.Shape#invalidate()
   */
  @Override
  public void invalidate() {
    invalid = true;
  }
}
