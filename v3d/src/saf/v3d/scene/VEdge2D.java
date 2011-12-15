/**
 * 
 */
package saf.v3d.scene;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.picking.BoundingSphere;
import saf.v3d.render.RenderState;
import saf.v3d.util.Utils3D;

/**
 * Edge line between to other nodes.
 * 
 * @author Nick Collier
 */
public class VEdge2D extends VGeometry {

  private static final float DELTA = .75f;

  private VSpatial source, target;
  private Point3f sourcePt, targetPt;
  private int edgeWidth = 1;
  private EdgeHead head = new EmptyEdgeHead();

  /**
   * Creates an undirected (no arrow) VEdge2D.
   * 
   * @param source
   *          the source of the edge
   * @param target
   *          the target of the edge
   */
  public VEdge2D(VSpatial source, VSpatial target) {
    this(source, target, false);
  }

  /**
   * Creates a VEdge2D.
   * 
   * @param source
   *          the source of the edge
   * @param target
   *          the target of the edge
   * @param directed
   *          whether or not the edge is directed
   */
  public VEdge2D(VSpatial source, VSpatial target, boolean directed) {
    this.source = source;
    this.target = target;

    sourcePt = new Point3f();
    targetPt = new Point3f();
    if (directed) {
      head = new ArrowHead2D(sourcePt, targetPt);
    }
  }

  /**
   * Sets the line width of the edge.
   * 
   * @param width
   *          the line width
   */
  public void setEdgeWidth(int width) {
    edgeWidth = width;
  }

  /**
   * Gets the line width of the edge.
   * 
   * @return the line width of the edge.
   */
  public int getEdgeWidth() {
    return edgeWidth;
  }

  /**
   * Updates the position of the VEdge to reflect any changes in the position of
   * the source and target.
   */
  public void update() {
    BoundingSphere sourceSphere = source.getLocalBoundingSphere();
    sourcePt.set(sourceSphere.getCenterRef());
    source.transform(sourcePt);
    BoundingSphere targetSphere = target.getLocalBoundingSphere();
    targetPt.set(targetSphere.getCenterRef());
    target.transform(targetPt);

    updatePoint(sourcePt, targetPt, sourceSphere.getRadius() * source.getLocalScale());
    updatePoint(targetPt, sourcePt, targetSphere.getRadius() * target.getLocalScale());
    head.update(edgeWidth);
  }

  private void updatePoint(Point3f pointToUpdate, Point3f otherPt, float radius) {
    double deltaX = pointToUpdate.x - otherPt.x;
    double deltaY = pointToUpdate.y - otherPt.y;
    // don't try to draw to edges if the sphere's overlap
    if (pointToUpdate.distanceSquared(otherPt) > (radius * radius)) {
      if (deltaX == 0) {
        if (deltaY > 0) {
          pointToUpdate.setY(pointToUpdate.y - radius);
        } else {
          pointToUpdate.setY(pointToUpdate.y + radius);
        }
      } else {
        // calc angle on unit circle between two points
        double theta = Math.atan(deltaY / deltaX);
        if (otherPt.x < pointToUpdate.x) {
          theta += Math.PI;
        }
        // remember your trig: cos gives X of theta, sin gives Y of theta
        pointToUpdate.set(pointToUpdate.x + radius * (float) Math.cos(theta), pointToUpdate.y
            + radius * (float) Math.sin(theta), 0);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see anl.mifs.viz3d.VNode#getBoundingBox()
   */
  @Override
  protected BoundingSphere doGetBoundingSphere() {
    BoundingSphere sphere = new BoundingSphere(source.doGetBoundingSphere());
    sphere.merge(target.doGetBoundingSphere());
    return sphere;
  }

  /**
   * Updates the ByteBuffer with the vertices and color of this edge.
   * 
   * @param buf
   *          the buffer to update
   */
  public void updateBuffer(ByteBuffer buf) {
    update();
    ColorAppearance color = (ColorAppearance) appearance;
    buf.putFloat(sourcePt.x);
    buf.putFloat(sourcePt.y);

    buf.putFloat(color.getRed());
    buf.putFloat(color.getGreen());
    buf.putFloat(color.getBlue());

    buf.putFloat(targetPt.x);
    buf.putFloat(targetPt.y);

    buf.putFloat(color.getRed());
    buf.putFloat(color.getGreen());
    buf.putFloat(color.getBlue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.ui.v3d.VSpatial#doDraw(javax.media.opengl.GL,
   * saf.ui.v3d.RenderState)
   */
  @Override
  protected void doDraw(GL gl, RenderState rState) {
    /*
     * gl.glBegin(GL.GL_LINES); gl.glVertex2f(sourcePt.x, sourcePt.y);
     * gl.glVertex2f(targetPt.x, targetPt.y); gl.glEnd();
     */
    head.draw(gl, appearance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.ui.v3d.VGeometry#intersects(javax.vecmath.Point3f,
   * javax.vecmath.Vector3f)
   */
  @Override
  protected boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    throw new UnsupportedOperationException("Not Yet Implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see saf.ui.v3d.VGeometry#intersects(javax.vecmath.Point3f)
   */
  @Override
  protected boolean intersects(Point3f point) {
    float distSq = Utils3D.distanceSquared(point, sourcePt, targetPt);
    if (distSq <= DELTA)
      return true;
    return head.intersects(point);
  }

}
