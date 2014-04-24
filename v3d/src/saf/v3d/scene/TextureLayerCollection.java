/**
 * 
 */
package saf.v3d.scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import saf.v3d.render.RenderState;
import saf.v3d.render.Texture2D;

/**
 * Collection implementation used by the TextureLayer to keep textures sorted by
 * their texture info.
 * 
 * @author Nick Collier
 */
public class TextureLayerCollection implements Collection<VSpatial> {

  private Map<Texture2D, Set<VSpatial>> images = new HashMap<Texture2D, Set<VSpatial>>();
  private int size = 0;
  
  public TextureLayerCollection() {
    AWTTextureIO.setTexRectEnabled(false);
  }
  
  public void invalidate(GL2 gl) {
    for (Map.Entry<Texture2D, Set<VSpatial>> entry : images.entrySet()) {
      entry.getKey().dispose(gl);
    }
  }
  
  public void draw(GL2 gl, RenderState state) {
    // texture target -- not always GL_TEXTURE_2D if non-power 2
    // extensions are used, so we need to track and enable / disable 
    // if it changes.
    int target = -1;
    for (Map.Entry<Texture2D, Set<VSpatial>> entry : images.entrySet()) {
      Texture2D tex = entry.getKey();
      if (target == -1) {
	target = tex.enable(gl);
      } else if (target != tex.getTarget(gl)) {
	gl.glDisable(target);
	target = tex.enable(gl);
      }
      
      tex.bind(gl);
      Set<VSpatial> set = entry.getValue();
      for (VSpatial img : set) {
        img.draw(gl, state);
      }
    }
    if (target != -1) gl.glDisable(target);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#add(java.lang.Object)
   */
  @Override
  public boolean add(VSpatial obj) {
    TextureRenderable image = (TextureRenderable)obj;
    Set<VSpatial> set = images.get(image.getTextureData());
    if (set == null) {
      set = new HashSet<VSpatial>();
      images.put(image.getTextureData(), set);
    }
    set.add(obj);
    size++;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll(Collection<? extends VSpatial> c) {
    for (VSpatial image : c) {
      add(image);
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#clear()
   */
  @Override
  public void clear() {
    size = 0;
    images.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object o) {
    if (!(o instanceof TextureRenderable)) return false;
    TextureRenderable image = (TextureRenderable)o;
    Set<VSpatial> set = images.get(image.getTextureData());
    return (set != null && set.contains(image));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object obj : c) {
      if (!contains(obj)) return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#iterator()
   */
  @Override
  public Iterator<VSpatial> iterator() {
    return new Iter();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#remove(java.lang.Object)
   */
  @Override
  public boolean remove(Object o) {
    if (!(o instanceof TextureRenderable)) return false;
    TextureRenderable image = (TextureRenderable)o;
    Set<VSpatial> set = images.get(image.getTextureData());
    if (set != null) {
      if (set.remove(o)) {
        size--;
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#size()
   */
  @Override
  public int size() {
    return size;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#toArray()
   */
  @Override
  public Object[] toArray() {
    Object[] array = new Object[size];
    int i = 0;
    for (VSpatial image : this) {
      array[i] = image;
      i++;
    }
    return array;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#toArray(T[])
   */
  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }
  
  class Iter implements Iterator<VSpatial> {
    
    private Iterator<Set<VSpatial>> valuesIter;
    private Iterator<VSpatial> iter;
    private VSpatial obj = null;
    
    public Iter() {
      valuesIter = images.values().iterator();
      while (obj == null && valuesIter.hasNext()) {
        iter = valuesIter.next().iterator();
        if (iter.hasNext()) {
          obj = iter.next();
        }
      }
    }
    
    private void findNext() {
      obj = null;
      while (obj == null) {
        if (iter.hasNext()) {
          obj = iter.next();
        } else if (valuesIter.hasNext()) {
          iter = valuesIter.next().iterator();
        } else {
          break;
        }
      }
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return obj != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public VSpatial next() {
      if (obj == null) throw new NoSuchElementException();
      VSpatial tmp = obj;
      findNext();
      return tmp;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
