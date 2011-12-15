/**
 * 
 */
package saf.v3d.event;

import java.awt.event.MouseAdapter;

/**
 * Adds enabled flag to a mouse adapter.
 * 
 * @author Nick Collier
 */
public class InputHandler extends MouseAdapter {
  
  protected boolean isEnabled = true;
  
  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }
  
  public boolean isEnabled() {
    return isEnabled;
  }

}
