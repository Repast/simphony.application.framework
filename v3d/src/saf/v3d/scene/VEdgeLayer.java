/**
 * 
 */
package saf.v3d.scene;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import saf.v3d.render.RenderState;

import com.jogamp.common.nio.Buffers;

/**
 * Layer specialized for drawing and managing edges.  
 * 
 * @author Nick Collier
 */
public class VEdgeLayer extends VLayer {
  
  // 4 for the vertex coords, 6 for the color of each vertex
  private static int FLOATS_PER_EDGE = 10;
  private static int INIT_CAPACITY = 1000;
  
  private int vboIndex = 0;
  private int capacity = INIT_CAPACITY;
  private boolean invalid = true;
  
  
  private void init(GL gl) {
    if (vboIndex != 0) {
      // size, array, offset
      gl.glDeleteBuffers(1, new int[]{vboIndex}, 0);
    }
    int[] indices = new int[1];
    gl.glGenBuffers(1, indices, 0);
    vboIndex = indices[0];
    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboIndex);
    int bufSize = capacity * FLOATS_PER_EDGE * Buffers.SIZEOF_FLOAT;
    gl.glBufferData(GL2.GL_ARRAY_BUFFER, bufSize, null, GL2.GL_STREAM_DRAW);
    invalid = false;
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.scene.VSpatial#invalidate()
   */
  @Override
  public void invalidate(GL2 gl) {
    invalid = true;
  }
  
  
  /* (non-Javadoc)
   * @see saf.v3d.scene.VComposite#addChild(saf.v3d.scene.VSpatial)
   */
  @Override
  public void addChild(VSpatial child) {
    if (children.size() == capacity) {
      capacity += INIT_CAPACITY / 2;
      invalid = true;
    }
    super.addChild(child);
  }

  /* (non-Javadoc)
   * @see saf.v3d.scene.VComposite#addChildren(saf.v3d.scene.VSpatial[])
   */
  @Override
  public void addChildren(VSpatial... items) {
    super.addChildren(items);
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.AbstractVNode#doDraw(com.jogamp.opengl.GL)
   */
  @Override
  protected void doDraw(GL2 gl, RenderState rState) {
    /*
    for (VSpatial child : children) {
      //((VEdge2D)child).updateBuffer(buf);
      child.doDraw(gl, rState);
    }
    */
   
    if (visible) {
      if (vboIndex == 0 || invalid) {
        init(gl);
      }
      
      if (children.size() > 0) {
        Map<Float, List<VEdge2D>> map = new HashMap<Float, List<VEdge2D>>();
        List<VEdge2D> ones = new ArrayList<VEdge2D>();
        for (VSpatial item : children) {
          VEdge2D edge = (VEdge2D)item;
          // this draws the head of the edge
          edge.doDraw(gl, rState);
          float val = edge.getEdgeWidth();
          if (val == 1.0) ones.add(edge);
          else {
            List<VEdge2D> list = map.get(val);
            if (list == null) {
              list = new ArrayList<VEdge2D>();
              map.put(val, list);
            }
            list.add(edge);
          }
        }
        map.put(1f, ones);
       
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboIndex);
        ByteBuffer buf = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY);
        ((Buffer)buf).position(0);
        
        //System.out.printf("children: %d%n", children.size());
       
        for (List<VEdge2D> list : map.values()) {
          for (VEdge2D edge : list) {
            edge.updateBuffer(buf);
          }
        }
        
        gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);

        // draw the buffer in one go // vertices are separated by a color,
        // stride is the offset from the BEGINNING of one
        // vertex to the beginning of the next --
        // so its 2 (the vertex coords) + 3 (the color components)
        gl.glVertexPointer(2, GL2.GL_FLOAT, 5 * Buffers.SIZEOF_FLOAT, 0);
        gl.glColorPointer(3, GL2.GL_FLOAT, 5 * Buffers.SIZEOF_FLOAT, 2 * Buffers.SIZEOF_FLOAT);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        
        int start = 0;
        for (Map.Entry<Float, List<VEdge2D>> vals : map.entrySet()) {
          gl.glLineWidth(vals.getKey());
          int end =  vals.getValue().size() * 2;
          gl.glDrawArrays(GL2.GL_LINES, start, end);
          start = end;
        }
        
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
      }
    }
   
  }
}
