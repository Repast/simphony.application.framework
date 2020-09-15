package saf.v3d.scene;

import java.util.Collection;
import java.util.Iterator;

import com.jogamp.opengl.GL2;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;

/**
 * Scene graph element node that can have child components.
 * 
 * @author Nick Collier
 */
public abstract class VComposite extends VSpatial {

  protected Collection<VSpatial> children;
  protected boolean boundsDirty = false;

  public VComposite() {
    boundingSphere = new BoundingSphere(new Point3f(), 0);
  }
  
  /* (non-Javadoc)
   * @see saf.v3d.scene.VSpatial#invalidate()
   */
  @Override
  public void invalidate(GL2 gl) {
    for (VSpatial child : children) {
      child.invalidate(gl);
    }
  }
  
  public void markBoundsDirty() {
    boundsDirty = true;
    dirty = true;
    if (parent != null) parent.markBoundsDirty();
  }

  public void addChild(VSpatial child) {
    markBoundsDirty();
    if (child.getParent() != null) {
      child.getParent().removeChild(child);
    }
    child.setParent(this);
    children.add(child);
  }

  public Iterable<VSpatial> children() {
    return children;
  }

  public void addChildren(VSpatial... items) {
    for (VSpatial child : items) {
      addChild(child);
    }
  }

  public void removeChild(VSpatial child) {
    children.remove(child);
    child.setParent(null);
    markBoundsDirty();
  }

  public int getChildCount() {
    return children.size();
  }

  public void removeAllChildren() {
    for (VSpatial child : children) {
      child.setParent(null);
    }
    children.clear();
    boundsDirty = true;
  }

  @Override
  protected BoundingSphere doGetBoundingSphere() {
    if (boundsDirty) {
      if (children.isEmpty())
        boundingSphere = new BoundingSphere(new Point3f(), 0);
      else {
        Iterator<VSpatial> iter = children.iterator();
        boundingSphere = new BoundingSphere(iter.next().doGetBoundingSphere());
        while (iter.hasNext()) {
          boundingSphere.merge(iter.next().doGetBoundingSphere());
        }
      }
    }
    return boundingSphere;
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VSpatial#markAsDirty()
   */
  @Override
  protected void markAsDirty() {
    super.markAsDirty();
    for (VSpatial child : children) {
      child.markAsDirty();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VSpatial#updateWorldTransformation()
   */
  @Override
  public void updateWorldTransformation() {
    super.updateWorldTransformation();
    for (VSpatial child : children) {
      child.updateWorldTransformation();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.AbstractVNode#doDraw(com.jogamp.opengl.GL)
   */
  @Override
  protected void doDraw(GL2 gl, RenderState rState) {
    if (visible) {
      for (VSpatial child : children) {
        child.draw(gl, rState);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * anl.mifs.viz3d.VSpatial#intersects(org.jogamp.vecmath.Point3f,anl.mifs.viz3d
   * .Accumulator)
   */
  @Override
  public void intersects(Point3f point, Accumulator accumulator) {
    // point is in world coordinates, so first check if the point intersects with
    // the BoundingSphere if not, then do nothing, if it does then pass to
    // children.
    BoundingSphere sphere = getBoundingSphere();
    if (sphere.intersects(point)) {
      for (VSpatial child : children) {
        child.intersects(point, accumulator);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VSpatial#intersects(org.jogamp.vecmath.Point3f,
   * org.jogamp.vecmath.Vector3f, anl.mifs.viz3d.Accumulator)
   */
  @Override
  public void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator) {
    // ray is in world coordinates, so first check if the ray intersects with
    // the BoundingSphere if not, then do nothing, if it does then pass to
    // children.
    BoundingSphere sphere = getBoundingSphere();
    if (sphere.intersects(rayOrigin, rayDirection)) {
      for (VSpatial child : children) {
        child.intersects(rayOrigin, rayDirection, accumulator);
      }
    }
  }
}
