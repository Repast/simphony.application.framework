/**
 * 
 */
package saf.v3d;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.vecmath.Color4f;

import saf.v3d.scene.Appearance;
import saf.v3d.scene.ColorAppearance;
import saf.v3d.scene.MaterialAppearance;

/**
 * @author Nick Collier
 */
public class AppearanceFactory {
  
  public static final ColorAppearance COLOR_BLUE = new ColorAppearance(Color.BLUE);
  
  public static final Appearance DO_NOTHING_APPEARANCE = new Appearance() {
    @Override
    public void applyAppearance(GL gl) {}
  };
   
  private static Map<Color, ColorAppearance> colors = new HashMap<Color, ColorAppearance>();
  static {
    colors.put(Color.BLUE, COLOR_BLUE);
    
  }
  
  public static ColorAppearance createColorAppearance(Color color) {
    ColorAppearance app = colors.get(color);
    if (app == null) {
      app = new ColorAppearance(color);
      colors.put(color, app);
    }
    return app;
  }
  
  public static MaterialAppearance createMaterialAppearance(Color color) {
    Color4f ambientColor = new Color4f(new float[]{.2f, .2f, .2f, 1});
    Color4f diffuseColor =  new Color4f(color);
    Color4f specularColor =  new Color4f(new float[]{1, 1, 1, 1});
    Color4f emissiveColor = new Color4f();
    float shininess = 64;
    
    return new MaterialAppearance(ambientColor, diffuseColor, specularColor, 
	emissiveColor, shininess);
  }

}
