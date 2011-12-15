/**
 * 
 */
package saf.v3d.util;

/**
 * Simple pair class.
 * 
 * @param T the type of the first element of the pair
 * @param U the type of the second element of the pair
 * 
 * @author Nick Collier
 */
public class Pair<T, U> {
  
  private T first;
  private U second;
  
  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  /**
   * @return the first
   */
  public T getFirst() {
    return first;
  }

  /**
   * @return the second
   */
  public U getSecond() {
    return second;
  }
}
