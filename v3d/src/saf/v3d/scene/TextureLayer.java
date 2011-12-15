/**
 * 
 */
package saf.v3d.scene;

import javax.media.opengl.GL;

import saf.v3d.render.RenderState;

/**
 * Layer that contains TextureLayers. This sets the opengl state necessary for
 * drawing any texture and then draws the individual textures.
 * 
 * @author Nick Collier
 */
public class TextureLayer extends VComposite {
  
  public TextureLayer() {
    children = new TextureLayerCollection();
  }
  
  
  /* (non-Javadoc)
   * @see saf.v3d.scene.VComposite#invalidate(int)
   */
  @Override
  public void invalidate() {
    ((TextureLayerCollection)children).invalidate();
  }


  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.scene.VSpatial#draw(javax.media.opengl.GL,
   * saf.v3d.render.RenderState)
   */
  @Override
  public void draw(GL gl, RenderState state) {
    gl.glEnable(GL.GL_TEXTURE_2D);
    //gl.glEnable(GL.GL_BLEND);
    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    
    ((TextureLayerCollection)children).draw(gl, state);

    gl.glDisable(GL.GL_TEXTURE_2D);
    //gl.glDisable(GL.GL_BLEND);
  }
}
