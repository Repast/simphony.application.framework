/**
 * 
 */
package saf.v3d.picking;

import java.awt.event.MouseEvent;
import java.util.List;

import saf.v3d.scene.VSpatial;

/**
 * Encapsulates pick data.
 * 
 * @author Nick Collier
 */
public class PickEvent {

  private int modifiers;
  private List<VSpatial> items;

  /**
   * @param mask
   * @param items
   */
  public PickEvent(int modifiers, List<VSpatial> items) {
    super();
    this.modifiers = modifiers;
    this.items = items;
  }

  /**
   * Gets the picked items. This can be empty.
   * 
   * @return the picked items. This can be empty.
   */
  public List<VSpatial> getPicked() {
    return items;
  }

  /**
   * Returns whether or not the Shift modifier is down on this event.
   */
  public boolean isShiftDown() {
    return (modifiers & MouseEvent.SHIFT_MASK) != 0;
  }

  /**
   * Returns whether or not the Control modifier is down on this event.
   */
  public boolean isControlDown() {
    return (modifiers & MouseEvent.CTRL_MASK) != 0;
  }

  /**
   * Returns whether or not the Alt modifier is down on this event.
   */
  public boolean isAltDown() {
    return (modifiers & MouseEvent.ALT_MASK) != 0;
  }
  
  public String toString() {
    return String.format("PickEvent[itemsPicked:%d, ctrl: %s, alt: %s, shift: %s]", items.size(), 
        isControlDown(), isAltDown(), isShiftDown());
  }
}
