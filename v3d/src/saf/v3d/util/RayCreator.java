package saf.v3d.util;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import saf.v3d.math.Ray3f;

import com.jogamp.common.nio.Buffers;

/**
 * Takes screen coordinates and creates ray
 * that passes through the world coordinates corresponding
 * to those screen coordinates.
 * 
 * @author Nick Collier
 */
public class RayCreator {
  
  private double[] identity = {1, 0, 0, 0,
                              0, 1, 0, 0,
                              0, 0, 1, 0,
                              0, 0, 0, 1};
  
  private IntBuffer viewport;
  private DoubleBuffer mvMatrix, projMatrix, output;
  private GLUgl2 glu;
  
  public RayCreator() {
    viewport = Buffers.newDirectIntBuffer(4);
    mvMatrix = Buffers.newDirectDoubleBuffer(16);
    projMatrix = Buffers.newDirectDoubleBuffer(16);
    output = Buffers.newDirectDoubleBuffer(3);
    glu = new GLUgl2();
  }
  
  /**
   * Assumes the ortho projection moves 0,0 to center and 
   * do a matrix view translate away.
   * @param gl
   * @param x
   * @param y
   * @return
   */
  public Point3f createOrthoPoint(GL2 gl, int x, int y) {
    int height = gl.getContext().getGLDrawable().getSurfaceHeight();
    int width = gl.getContext().getGLDrawable().getSurfaceWidth();
   
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projMatrix);
    
    mvMatrix.put(identity, 0, identity.length);
    mvMatrix.put(12, -width / 2);
    mvMatrix.put(13, -height / 2);
    ((Buffer)mvMatrix).rewind();
    
    // need opengl y where 0 is the top, 
    int ogY = height - y - 1;
    
    viewport.put(2, width);
    viewport.put(3, height);
    glu.gluUnProject(x, ogY, 0, mvMatrix, projMatrix, viewport, output);
    return new Point3f((float)output.get(0), (float)output.get(1), 0);
  }
  
  public Ray3f createRay(GL2 gl, Point3f viewLocation, int x, int y) {
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
    gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvMatrix);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projMatrix);
    
    // need opengl y where 0 is the top, 
    int ogY = viewport.get(3) - y - 1;
    //System.out.printf("cursor coords: %d, %d%n",x, ogY);
    glu.gluUnProject(x, ogY, 1, mvMatrix, projMatrix, viewport, output);
    //System.out.printf("world coords at z = 1.0 are (%f, %f, %f)%n", output.get(0), output.get(1), output.get(2));
    
    Vector3f rayDirection = new Vector3f();
    rayDirection.x = (float)output.get(0);
    rayDirection.y = (float)output.get(1);
    rayDirection.z = (float)output.get(2);
    
    glu.gluUnProject(x, ogY, 0, mvMatrix, projMatrix, viewport, output);
    rayDirection.x -= (float)output.get(0);
    rayDirection.y -= (float)output.get(1);
    rayDirection.z -= (float)output.get(2);
    
    //System.out.printf("world coords at z = 0.0 are (%f, %f, %f)%n", output.get(0), output.get(1), output.get(2));
    //System.out.printf("ray direction:%s%n", rayDirection);
    
    return new Ray3f(viewLocation, rayDirection);
    
  }

}
