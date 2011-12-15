/**
 * 
 */
package saf.v3d.scene;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.picking.DefaultAccumulator;

/**
 * 
 * @author Nick Collier
 */
public class VNode extends VComposite {

  /**
   * Creates a VNode that will draw the specified shape.
   * 
   * @param shape
   *          the shape to draws
   */
  public VNode(VShape shape) {
    children = new ArrayList<VSpatial>();
    addChild(shape);
  }

  /**
   * Creates a VNode that will draw the specified VImage2D.
   * 
   * @param image
   */
  public VNode(VImage2D image) {
    children = new ArrayList<VSpatial>();
    addChild(image);
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.scene.VSpatial#setAppearance(saf.v3d.render.Renderable)
   */
  @Override
  public void setAppearance(Appearance app) {
    super.setAppearance(app);
    for (VSpatial child : children) {
      child.setAppearance(app);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VNode#getBoundingBox()
   */
  @Override
  protected BoundingSphere doGetBoundingSphere() {
    Quat4f rotation = new Quat4f();
    rotation.set(worldRotation);
    rotation.normalize();
    BoundingSphere sphere = getLocalBoundingSphere();
    return sphere.transform(worldScale, rotation, worldTrans);
  }

  /**
   * Gets the bounding sphere in local coordinates.
   * 
   * @return the bounding sphere in local coordinates.
   */
  @Override
  public BoundingSphere getLocalBoundingSphere() {
    // todo need to invalidate the bounding sphere is the children
    // of this node have translated etc., or children added, or removed
    //if (boundingSphere == null) {
      List<VSpatial> childList = (List<VSpatial>) children;
      boundingSphere = new BoundingSphere(((List<VSpatial>) children).get(0)
          .getLocalBoundingSphere());
      for (int i = 1, n = childList.size(); i < n; i++) {
        VSpatial child = childList.get(i);
        BoundingSphere childSphere = new BoundingSphere(child.getLocalBoundingSphere());
        Quat4f rotation = new Quat4f();
        rotation.set(child.getLocalRotation());
        rotation.normalize();
        childSphere.transform(child.getLocalScale(), rotation, child.getLocalTranslation());
        boundingSphere.merge(childSphere);
      }

    //}
    return boundingSphere;
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.scene.VComposite#intersects(javax.vecmath.Point3f,
   * saf.v3d.picking.Accumulator)
   */
  @Override
  public void intersects(Point3f point, Accumulator accumulator) {
    Accumulator tmp = new DefaultAccumulator();
    super.intersects(point, tmp);
    if (!tmp.getItems().isEmpty())
      accumulator.add(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.v3d.scene.VComposite#intersects(javax.vecmath.Point3f,
   * javax.vecmath.Vector3f, saf.v3d.picking.Accumulator)
   */
  @Override
  public void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator) {
    Accumulator tmp = new DefaultAccumulator();
    super.intersects(rayOrigin, rayDirection, accumulator);
    if (!tmp.getItems().isEmpty())
      accumulator.add(this);
  }
}
