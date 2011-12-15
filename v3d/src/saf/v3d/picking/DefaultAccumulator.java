/**
 * 
 */
package saf.v3d.picking;

import java.util.ArrayList;
import java.util.List;

import saf.v3d.scene.VSpatial;

/**
 * @author Nick Collier
 */
public class DefaultAccumulator implements Accumulator {
  
  private List<VSpatial> items = new ArrayList<VSpatial>();

  /* (non-Javadoc)
   * @see anl.mifs.viz3d.Accumulator#add(anl.mifs.viz3d.VSpatial)
   */
  @Override
  public void add(VSpatial item) {
   items.add(item);
  }
  
  public int size() {
    return items.size();
  }
  
  /* (non-Javadoc)
   * @see anl.mifs.viz3d.Accumulator#items()
   */
  public Iterable<VSpatial> items() {
    return items;
  }

  /* (non-Javadoc)
   * @see repast.simphony.v3d.Accumulator#getItems()
   */
  @Override
  public List<VSpatial> getItems() {
    return new ArrayList<VSpatial>(items);
  }

  public String toString() {
    return items.toString();
  }

}
