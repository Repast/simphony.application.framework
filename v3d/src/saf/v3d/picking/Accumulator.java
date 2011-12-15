/**
 * 
 */
package saf.v3d.picking;

import java.util.List;

import saf.v3d.scene.VSpatial;

/**
 * Interface for classes that accumulate VNodes.
 * 
 * @author Nick Collier
 */
public interface Accumulator {
  
  /**
   * Adds the specified VNode to this Accumulator.
   * 
   * @param node the node to add
   */
  void add(VSpatial node);
  
  /**
   * Gets an iterator over the accumulated items.
   * 
   * @return the accumulated items.
   */
  Iterable<VSpatial> items();
  
  
  /**
   * Gets the accumulated items.
   * 
   * @return the accumulated items.
   */
  List<VSpatial> getItems();

}
