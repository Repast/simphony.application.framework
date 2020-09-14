package saf.v3d.scene;


import java.util.Arrays;

import com.jogamp.opengl.GL2;
import javax.vecmath.Color4f;

public class MaterialAppearance implements Appearance {
  
  // packed as ambient, diffuse, specular and emissive
  private float[] colors = new float[17];
  
  public MaterialAppearance() {
    Arrays.fill(colors, 1);
    colors[12] = 0;
    colors[13] = 0;
    colors[14] = 0;
    colors[15] = 0;
  }
  
  public MaterialAppearance(Color4f ambient, Color4f diffuse, Color4f specular, Color4f emissive,
      float shininess) {
    
    float[] color = new float[4];
    ambient.get(color);
    System.arraycopy(color, 0, colors, 0, 4);
    diffuse.get(color);
    System.arraycopy(color, 0, colors, 4, 4);
    specular.get(color);
    System.arraycopy(color, 0, colors, 8, 4);
    emissive.get(color);
    System.arraycopy(color, 0, colors, 12, 4);
    colors[16] = shininess;
  }
  
  public void setEmissiveColor(Color4f emissive) {
    float[] color = new float[4];
    emissive.get(color);
    System.arraycopy(color, 0, colors, 12, 4);
  }
  
  public void applyAppearance(GL2 gl) {
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colors, 0);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colors, 4);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colors, 8);
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, colors, 12);
    
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, colors, 16);
  }
}
