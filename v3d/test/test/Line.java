package test;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.jogamp.common.nio.Buffers;

public class Line {

  private Point3f source = new Point3f();
  private Point3f target = new Point3f();
  private Color3f color = new Color3f();
  private int maxWidth, maxHeight;
  private FloatBuffer data = Buffers.newDirectFloatBuffer(4);
  private IntBuffer indices = Buffers.newDirectIntBuffer(2);
  private float lineWidth = 1;

  public Line(int width, int height) {
    this.maxWidth = width;
    this.maxHeight = height;
    update();
    indices.rewind();
    indices.put(0);
    indices.put(1);
    
    color.x = (float)Math.random();
    color.y = (float)Math.random();
    color.z = (float)Math.random();
    
    double val = Math.random();
    if (val > .9) this.lineWidth = 4;
    else if (val > .8) this.lineWidth = 6;
  }
  
  public float getWidth() {
    return lineWidth;
  }

  public void update() {
    source.x = (float) (maxWidth * Math.random());
    source.y = (float) (maxHeight * Math.random());

    target.x = (float) (maxWidth * Math.random());
    target.y = (float) (maxHeight * Math.random());
  }

  public void draw(GL2 gl) {
    gl.glColor3f(color.x, color.y, color.z);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex2f(source.x, source.y);
    gl.glVertex2f(target.x, target.y);
    gl.glEnd();
  }

  public void vboDraw(GL2 gl, ByteBuffer buf) {
    // update the data
    //int offset = index * 10 * BufferUtil.SIZEOF_FLOAT;;
    //buf.position(offset);
    buf.putFloat(source.x);
    buf.putFloat(source.y);
    
    buf.putFloat(color.x);
    buf.putFloat(color.y);
    buf.putFloat(color.z);
    
    buf.putFloat(target.x);
    buf.putFloat(target.y);
    
    buf.putFloat(color.x);
    buf.putFloat(color.y);
    buf.putFloat(color.z);
  }
 
  public void vboDraw(GL2 gl, int index) {

    // update the data
    data.rewind();
    data.put(source.x);
    data.put(source.y);
    data.put(target.x);
    data.put(target.y);
    data.rewind();

    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, index * Buffers.SIZEOF_FLOAT, index + 4 * Buffers.SIZEOF_FLOAT, data);

    // enable the appropriate state
    /*
    gl.glVertexPointer(2, GL.GL_FLOAT, 0, 0);
    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
    indices.rewind();
    //gl.glDrawArrays(GL.GL_LINES, 0, 4);
    gl.glDrawRangeElements(GL.GL_LINES, 0, 1, 2, GL.GL_UNSIGNED_INT, indices);
    gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
    */
  }

}
