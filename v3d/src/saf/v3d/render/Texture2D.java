/**
 * 
 */
package saf.v3d.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.vecmath.Point3f;

import saf.v3d.picking.BoundingSphere;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * Encapsulates a 2D texture and the quad on which it is displayed.
 * 
 * @author Nick Collier
 */
public class Texture2D {

  private BufferedImage img;
  private Texture texture;
  private BoundingSphere sphere;
  
  private int listIndex;

  public Texture2D(String path) throws IOException {
    init(ImageIO.read(new File(path)));
  }
  
  public Texture2D(String path, float scale) throws IOException {
    this(ImageIO.read(new File(path)), scale);
  }
  
  public Texture2D(BufferedImage img, float scale) {
    if (scale == 1) init(img);
    int width = (int)(img.getWidth() * scale);
    int height = (int)(img.getHeight() * scale);
    BufferedImage scaledImg = new BufferedImage(width, height, img.getType());
    AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
    Graphics2D graphics = scaledImg.createGraphics();
    graphics.drawRenderedImage(img, at);
    graphics.dispose();
    init(scaledImg);
  }
  
  public Texture2D(BufferedImage img) {
    init(img);
  }
  
  private void init(BufferedImage img) {
    this.img = img;
    sphere = new BoundingSphere(new Point3f(0, 0, 0), Math.max(img.getHeight() / 2f, img.getWidth() / 2f));
  }
  
  public void bind(GL2 gl) {
    if (texture == null) {
      createTexture(gl);
    }
    texture.bind(gl);
  }
  
  /**
   * Gets the BufferedImage drawn on the this Texture2D.
   * 
   * @return the BufferedImage drawn on the this Texture2D.
   */
  public BufferedImage getImage() {
    return img;
  }
  
  /**
   * Disposes of this texture.
   */
  public void dispose(GL2 gl) {
    if (texture!= null) texture.destroy(gl);
    texture = null;
  }

  private void createTexture(GL2 gl) {
    texture = AWTTextureIO.newTexture(gl.getGLProfile(), img, true);
    float width = texture.getWidth() / 2f;
    float height = texture.getHeight() / 2f;
    
    listIndex = gl.glGenLists(1);
    gl.glNewList(listIndex, GL2.GL_COMPILE);
    gl.glBegin(GL2.GL_QUADS);
    
    gl.glTexCoord2f(0, 1);
    gl.glVertex2f(-width, -height);
    
    gl.glTexCoord2f(1, 1);
    gl.glVertex2f(width, -height);
    
    gl.glTexCoord2f(1, 0);
    gl.glVertex2f(width, height);
    
    gl.glTexCoord2f(0, 0);
    gl.glVertex2f(-width, height);
    gl.glEnd();
    gl.glEndList();
  }
  
  public float getWidth() {
    return texture.getWidth();
  }
  
  public float getHeight() {
    return texture.getHeight();
  }
  
  public BoundingSphere getBounds() {
    return sphere;
  }

  public void draw(GL2 gl, RenderState rState) {
    gl.glCallList(listIndex);
  }
}
