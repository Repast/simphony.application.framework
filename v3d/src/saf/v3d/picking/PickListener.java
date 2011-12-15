package saf.v3d.picking;


/**
 * Interface for classes that want to be notified of a pick
 * event. A pick is fired when there is a left click on the canvas.
 * 
 * @author Nick Collier
 */
public interface PickListener {
  
  /**
   * Called when a pick event has been generated
   * from the canvas.
   * 
   * @param evt the PickEvent that was fired
   */
  void pickPerformed(PickEvent evt);

}
