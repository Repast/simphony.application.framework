/**
 * 
 */
package saf.v3d.scene;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import saf.v3d.AppearanceFactory;
import saf.v3d.picking.Accumulator;
import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;

/**
 * Abstract base class for items in the scene graph.
 * 
 * @author Nick Collier
 */
public abstract class VSpatial {
  
  interface AppSetter {
    void set(Appearance app);
  }
  
  static final class EmptyAppSetter implements AppSetter {
    @Override
    public void set(Appearance app) {}
  }
  
  static final class DefAppSetter implements AppSetter {
    
    VSpatial spatial;
    
    public DefAppSetter(VSpatial spatial) {
      this.spatial = spatial;
    }

    @Override
    public void set(Appearance app) {
      spatial.appearance = app;
    }
  }
  
 static final class OneTimeAppSetter implements AppSetter {
    
    VSpatial spatial;
    
    public OneTimeAppSetter(VSpatial spatial) {
      this.spatial = spatial;
    }

    @Override
    public void set(Appearance app) {
      spatial.appearance = app;
      spatial.appSetter = new EmptyAppSetter();
    }
 }

  protected static final int NO_CANVAS_ID = -1;

  protected Vector3f localTrans, worldTrans;
  protected float localScale, worldScale;
  protected AxisAngle4f localRotation;
  protected Matrix3f worldRotation;
  protected Appearance appearance = AppearanceFactory.COLOR_BLUE;
  protected boolean visible = true, dirty = true;
  protected AppSetter appSetter;
  protected VComposite parent;
  protected BoundingSphere boundingSphere;

  protected Map<Object, Object> clientMap = new HashMap<Object, Object>();
  public String id = "";

  public VSpatial() {
    localTrans = new Vector3f();
    worldTrans = new Vector3f();
    localScale = worldScale = 1;

    localRotation = new AxisAngle4f(0, 0, 0, 0);
    worldRotation = new Matrix3f();
    worldRotation.setIdentity();
    appSetter = new DefAppSetter(this);
  }

  /**
   * Gets the local translation.
   * 
   * @return the local translation.
   */
  public Vector3f getLocalTranslation() {
    return localTrans;
  }

  /**
   * Gets the local scale.
   * 
   * @return the local scale.
   */
  public float getLocalScale() {
    return localScale;
  }

  /**
   * Gets the local rotation.
   * 
   * @return the local rotation.
   */
  public AxisAngle4f getLocalRotation() {
    return localRotation;
  }

  /**
   * Gets the current world translation for this VSpatial. This may be invalid
   * if parent transforms have recently changed. 
   * 
   * @return the current world translation for this VSpatial. 
   */
  public Vector3f getWorldTranslation() {
    return worldTrans;
  }

  /**
   * Gets the world scale.
   * 
   * @return the world scale.
   */
  public float getWorldScale() {
    return worldScale;
  }

  public Matrix3f getWorldRotation() {
    return worldRotation;
  }

  /**
   * Puts a client property with the specified key in the VSpatial
   * 
   * @param key
   *          the property key
   * @param val
   *          the property value
   */
  public void putProperty(Object key, Object val) {
    clientMap.put(key, val);
  }

  /**
   * Gets a client property from this VSpatial.
   * 
   * @param key
   *          the property key
   * @return the property value or null if no such property is found.
   */
  public Object getProperty(Object key) {
    return clientMap.get(key);
  }

  /**
   * Does the actual drawing.
   * 
   * @param gl
   */
  protected abstract void doDraw(GL gl, RenderState state);

  /**
   * Invalidates this VSpatial with respect to the specified canvas. The
   * semantics are subclass specific.
   */
  public void invalidate() {}

  /**
   * Draws this Vspatial.
   * 
   * @param gl
   *          the GL used to draw.
   */
  public void draw(GL gl, RenderState state) {
    if (visible) {
      gl.glPushMatrix();
      gl.glTranslatef(localTrans.x, localTrans.y, localTrans.z);
      gl.glRotatef((float) Math.toDegrees(localRotation.angle), localRotation.x, localRotation.y,
          localRotation.z);
      gl.glScalef(localScale, localScale, localScale);
      if (appearance == null) appearance = AppearanceFactory.COLOR_BLUE;
      state.appearance = appearance;
      doDraw(gl, state);
      gl.glPopMatrix();
    }
  }

  /**
   * Updates the world transformation of this VSpatial. This works
   * top down, so parent VSpatials must be called first.
   */
  public void updateWorldTransformation() {
    if (dirty) {
      if (parent != null) {
        worldScale = parent.getWorldScale() * localScale;
        worldRotation.set(localRotation);
        worldRotation.mul(parent.getWorldRotation());

        Matrix3f matrix = new Matrix3f(parent.getWorldRotation());
        Vector3f trans = new Vector3f(localTrans);
        matrix.transform(trans);
        trans.scale(parent.getWorldScale());
        worldTrans.set(parent.getWorldTranslation());
        worldTrans.add(trans);

      } else {
        worldScale = localScale;
        worldRotation.set(localRotation);
        worldTrans.set(localTrans);
      }
    }
    dirty = false;
    // System.out.println("worldRotation = " + worldRotation);
  }

  protected void markAsDirty() {
    this.dirty = true;
  }

  /**
   * Gets whether or not this Vspatial is visible.
   * 
   * @return true if this node is visible, otherwise false.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Rotates this Vspatial by angle degrees around the vector (x, y, z).
   * 
   * @param angle
   *          the angle of rotation, in degrees
   * @param x
   *          the x coordinate of the rotation vector
   * @param y
   *          the y coordinate of the rotation vector
   * @param z
   *          the z coordinate of the rotation vector
   */
  public void rotate(float angle, float x, float y, float z) {
    localRotation.set(x, y, z, (float) Math.toRadians(angle));
    markAsDirty();
    // System.out.println("localRotation = " + localRotation);
  }

  /**
   * Sets the angle of rotation clock-wise in 2D space. The object is rotate
   * around the z vector.
   * 
   * @param angle
   *          the angel of rotation, in degrees.
   */
  public void rotate2D(float angle) {
    rotate(angle, 0, 0, -1);
  }

  /**
   * Rotates this VSpatial by the specified quaternion.
   * 
   * @param rotation
   *          the amount to rotate
   */
  public void rotate(Quat4f rotation) {
    localRotation.set(rotation);
    markAsDirty();
  }

  /**
   * Scales this Vspatial along the x, y, and z axes.
   * 
   * @param x
   *          the x scale factor
   * @param y
   *          the y scale factor
   * @param z
   *          the z scale factor
   */
  public void scale(float scale) {
    if (localScale != scale) {
      localScale = scale;
      markAsDirty();
    }
  }

  /**
   * Sets the {@link MaterialAppearance} of this VSpatial.
   * 
   * @param app
   *          the new appearence
   */
  public void setAppearance(Appearance app) {
    appSetter.set(app);
  }

  /**
   * Gets the {@link MaterialAppearance} of this VSpatial.
   * 
   * @return the {@link MaterialAppearance} of this VSpatial.
   */
  public Appearance getAppearance() {
    return appearance;
  }

  /**
   * Sets the visibility of this Vspatial.
   * 
   * @param visible
   *          the new visibility property
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Translates this Vspatial by x, y and z.
   * 
   * @param x
   *          the x coordinate of the translation vector
   * @param y
   *          the z coordinate of the translation vector
   * @param z
   *          the y coordinate of the translation vector
   */
  public void translate(float x, float y, float z) {
    localTrans.set(x, y, z);
    markAsDirty();
  }

  /**
   * Translates this Vspatial by the x, y, and z components of the specified
   * point.
   * 
   * @param pt
   *          the point to translate with
   */
  public void translate(Tuple3f tuple) {
    localTrans.set(tuple);
    markAsDirty();
  }

  /**
   * Gets the parent of this Vspatial.
   * 
   * @return the parent of this Vspatial.
   */
  public VComposite getParent() {
    return parent;
  }

  /**
   * Sets the parent for this Vspatial.
   * 
   * @param parent
   *          the parent node
   */
  public void setParent(VComposite parent) {
    this.parent = parent;
  }

  /**
   * Transforms the specified point from the local coordinates of this Vspatial
   * to those of its top most parent. This will alter passed in point.
   * 
   * @param pt
   *          the point to transform.
   * @return the transformed point
   * 
   *         public Point3f localToWorld(Point3f pt) { if (dirty)
   *         updateWorldTransformation();
   * 
   *         }
   */

  /**
   * Gets the world to local transform for this VSpatial.
   * 
   */
  public Matrix4f getWorldToLocalTransform() {
    percolateDirtyUp();

    Matrix4f matrix = new Matrix4f();
    matrix.setIdentity();

    matrix.setRotation(worldRotation);
    matrix.setScale(worldScale);
    matrix.setTranslation(worldTrans);
    matrix.invert();

    return matrix;
  }

  /**
   * Transforms the specified point using this Vspatial's local transforms.
   * 
   * @param pt
   *          the point to transform.
   * @return the transformed point
   */
  public Point3f transform(Point3f pt) {
    Matrix3f mat = new Matrix3f();
    mat.set(localRotation);
    mat.mul(localScale);
    mat.transform(pt);
    pt.add(localTrans);
    return pt;
  }

  /**
   * Gets this VSpatial's BoundingSphere in its local frame.
   * 
   * @return this VSpatial's BoundingSphere in its local frame.
   */
  public BoundingSphere getLocalBoundingSphere() {
    return boundingSphere;
  }

  protected void percolateDirtyUp() {
    if (dirty) {
      // find the top most ancestor and updateWorldTransform
      // which will eventually update this
      if (parent == null) {
        updateWorldTransformation();
      } else {
        VSpatial ancestor = parent;
        while (ancestor.parent != null) {
          ancestor = ancestor.parent;
        }
        ancestor.updateWorldTransformation();
      }
    }

  }

  /**
   * Gets this VSpatial's BoundingSphere in the world frame.
   * 
   * @return this VSpatial's BoundingSphere in the world frame.
   */
  public BoundingSphere getBoundingSphere() {
    percolateDirtyUp();
    return doGetBoundingSphere();
  }

  /**
   * Gets the actual bounding sphere. This is called by getBoundingSphere once
   * the worldTransforms have been updated.
   * 
   * @return this VSpatial's BoundingSphere.
   */
  protected BoundingSphere doGetBoundingSphere() {
    Quat4f rotation = new Quat4f();
    rotation.set(worldRotation);
    rotation.normalize();
    BoundingSphere sphere = new BoundingSphere(boundingSphere);
    return sphere.transform(worldScale, rotation, worldTrans);
  }

  /**
   * Performs an intersection test on this VSpatial with the specified point. If
   * the intersection passes, this VSpatial should be added to the Accumulator.
   * 
   * @param point
   *          the point to test
   * @param accumulator
   *          the accumulator to add the VSpatial to
   */
  public abstract void intersects(Point3f point, Accumulator accumulator);

  /**
   * Performs an intersection test on this VSpatial with the specified ray. If
   * the intersection passes, this VSpatial should be added to the Accumulator.
   * 
   * @param rayOrigin
   *          the ray's origin
   * @param rayDirection
   *          the ray's direction
   * @param accumulator
   *          the accumulator to add the VSpatial to
   */
  public abstract void intersects(Point3f rayOrigin, Vector3f rayDirection, Accumulator accumulator);
}
