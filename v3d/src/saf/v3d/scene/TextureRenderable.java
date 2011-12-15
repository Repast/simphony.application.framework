/**
 * 
 */
package saf.v3d.scene;

import saf.v3d.render.Texture2D;

/**
 * Interface for spatials that are rendered as textures.
 * 
 * @author Nick Collier
 */
public interface TextureRenderable {
  
  /**
   * Gets the texture2D data for this VImage2D.
   * 
   * @return the texture2D data for this VImage2D.
   */
  Texture2D getTextureData(); 
}
