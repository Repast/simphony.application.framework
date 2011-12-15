/**
 * 
 */
package saf.v3d.scene;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.AppearanceFactory;
import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;
import saf.v3d.render.Texture2D;

/**
 * 
 * @author Nick Collier
 */
public class VImage2D extends VSpatial implements TextureRenderable {
  
  private Texture2D texture;
  
  public VImage2D(Texture2D texture) {
    this.texture = texture;
    appearance = AppearanceFactory.DO_NOTHING_APPEARANCE;
    boundingSphere = texture.getBounds();
  }
  
  /**
   * Gets the texture2D data for this VImage2D.
   * 
   * @return the texture2D data for this VImage2D.
   */
  public Texture2D getTextureData() {
    return texture;
  }

  @Override
  protected void doDraw(GL gl, RenderState rState) {
    texture.draw(gl, rState);
  }

  @Override
  public void intersects(Point3f point, Accumulator accumulator) {
    BoundingSphere sphere = getBoundingSphere();
    if (sphere.intersects(point)) {
      // transform the point to local coordinates
      // using inverse of local to world matrix
      Point3f localPt = new Point3f(point);
      Matrix4f matrix = new Matrix4f();
      matrix.setIdentity();
      matrix.setRotation(worldRotation);
      matrix.setScale(worldScale);
      matrix.setTranslation(worldTrans);
      
      Matrix4f invert = new Matrix4f();
      invert.setIdentity();
      invert.invert(matrix);
      
      matrix.setTranslation(worldTrans);
      invert.setIdentity();
      invert.invert(matrix);
      invert.transform(localPt);
      
      if (new Rectangle2D.Float(-texture.getWidth() / 2f, -texture.getHeight() / 2f, 
          texture.getWidth(), texture.getHeight()).contains(new Point2D.Float(localPt.x, localPt.y))) {
        accumulator.add(this);
      }
    }
  }

  @Override
  public void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator) {
    // todo implement this -- need to remove the assumption that the quad is at 0, so quad
    // would then have vertx with 3 coords.
    throw new UnsupportedOperationException();
    
  }
}
