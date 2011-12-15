/**
 * 
 */
package saf.v3d.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Point3f;

/**
 * Triangle iterator that iterates over the triangles
 * provided by other triangle iterators.
 * 
 * @author Nick Collier
 */
public class CompositeTriangleIterator implements TriangleIterator {
  
  private List<TriangleIterator> iterators = new ArrayList<TriangleIterator>();
  private int listIndex = 0;
  private TriangleIterator iterator;
  
  public CompositeTriangleIterator(Collection<TriangleIterator> iterators) {
    this.iterators.addAll(iterators);
    if (iterators.size() > 0) iterator = this.iterators.get(0);
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return iterator != null && iterator.hasNext();
  }

  /* (non-Javadoc)
   * @see saf.v3d.render.TriangleIterator#next(javax.vecmath.Point3f, javax.vecmath.Point3f, javax.vecmath.Point3f)
   */
  @Override
  public void next(Point3f p1, Point3f p2, Point3f p3) {
    iterator.next(p1, p2, p3);
    if (!iterator.hasNext()) {
      listIndex++;
      if (listIndex < iterators.size()) {
        iterator = iterators.get(listIndex);
      }
    }
  }
}
