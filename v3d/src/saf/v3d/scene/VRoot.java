/**
 * 
 */
package saf.v3d.scene;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.jogamp.vecmath.Matrix3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;


/**
 * Root node in the scene graph. The root is the node right below the camera.
 * 
 * @author Nick Collier
 */
public class VRoot extends VComposite {

  private Camera camera;
  private Vector3f cameraTrans = new Vector3f();

  public VRoot(Camera camera) {
    children = new LinkedHashSet<VSpatial>();
    this.camera = camera;
    this.parent = null;
  }

  private void doUpdateWorldTransformation() {
    if (dirty) {
      worldScale = /*camera.getScale() * */ localScale;
      worldRotation.set(localRotation);
      worldRotation.mul(camera.rotation);

      Matrix3f matrix = new Matrix3f(camera.rotation);
      Vector3f trans = new Vector3f(localTrans);
      matrix.transform(trans);
      trans.scale(camera.getScale());
      camera.getTranslate(cameraTrans);
      worldTrans.set(cameraTrans);
      worldTrans.add(trans);
    }
    dirty = false;
  }
  
  @Override
  protected BoundingSphere doGetBoundingSphere() {
    if (children.isEmpty())
      return new BoundingSphere(new Point3f(), 0);
    Iterator<VSpatial> iter = children.iterator();
    BoundingSphere sphere = new BoundingSphere(iter.next().doGetBoundingSphere());
    while (iter.hasNext()) {
      sphere.merge(iter.next().doGetBoundingSphere());
    }
    return sphere;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VSpatial#updateWorldTransformation()
   */
  @Override
  public void updateWorldTransformation() {
    doUpdateWorldTransformation();
    for (VSpatial child : children) {
      child.updateWorldTransformation();
    }
  }
}
