/**
 * 
 */
package saf.v3d.scene;

import com.jogamp.opengl.GL2;

/**
 * Interface for classes that implement an appearance.
 * 
 * @author Nick Collier
 */
public interface Appearance {
  
  void applyAppearance(GL2 gl);

}
