/**
 * 
 */
package saf.v3d.scene;

import java.util.HashSet;

/**
 * Group like layer that renders nodes as a group.
 * 
 * @author Nick Collier
 */
public class VLayer extends VComposite {
  
  public VLayer() {
    children = new HashSet<VSpatial>();
  }

}
