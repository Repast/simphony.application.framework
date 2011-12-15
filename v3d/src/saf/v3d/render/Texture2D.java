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
import javax.media.opengl.GL;
import javax.vecmath.Point3f;

import saf.v3d.picking.BoundingSphere;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

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
  
  public void bind(GL gl) {
    if (texture == null) {
      createTexture(gl);
    }
    texture.bind();
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
  public void dispose() {
    if (texture!= null) texture.dispose();
    texture = null;
  }

  private void createTexture(GL gl) {
    texture = TextureIO.newTexture(img, true);
    float width = texture.getWidth() / 2f;
    float height = texture.getHeight() / 2f;
    
    listIndex = gl.glGenLists(1);
    gl.glNewList(listIndex, GL.GL_COMPILE);
    gl.glBegin(GL.GL_QUADS);
    
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

  public void draw(GL gl, RenderState rState) {
    gl.glCallList(listIndex);
  }
}
