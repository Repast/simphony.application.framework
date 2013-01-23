package saf.v3d.scene;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import saf.v3d.util.Utils3D;

/**
 * Simple arrow head for edges.
 * 
 * @author Nick Collier
 */
public class ArrowHead2D implements EdgeHead {
  
  private float topX, topY, botX, botY;
  private Point3f target, source;
  
  public ArrowHead2D(Point3f source, Point3f target) {
    this.target = target;
    this.source = source;
  }
  
  /**
   * Updates this arrow head to reflect any changes in the
   * source and target points.
   */
  public void update(int edgeWidth) {
    float x1 = target.x;
    float y1 = target.y;

    float x2 = source.x;
    float y2 = source.y;
    
    float lineLen = target.distance(source);

    float sinTheta = (y1 - y2) / lineLen;
    float cosTheta = (x1 - x2) / lineLen;

    float length = 10;
    
    float midY = y1 - ((sinTheta * length) / 2);
    float midX = x1 - ((cosTheta * length) / 2);

    double theta = Math.acos(cosTheta);

    double topTheta = theta + (Math.PI / 2);
    double botTheta = theta - (Math.PI / 2);

    double sinTopTheta = Math.sin(topTheta);
    double cosTopTheta = Math.cos(topTheta);

    double sinBotTheta = Math.sin(botTheta);
    double cosBotTheta = Math.cos(botTheta);
    
    
    if (y2 > y1) {
      topY = (float)(midY + ((sinTopTheta * length) / 2));
      botY = (float)(midY + ((sinBotTheta * length) / 2));
    } else {
      topY = (float)(midY - ((sinTopTheta * length) / 2));
      botY = (float)(midY - ((sinBotTheta * length) / 2));
    }

    topX = (float)(midX - ((cosTopTheta * length) / 2));
    botX = (float)(midX - ((cosBotTheta * length) / 2));
  }

  /**
   * Draws the arrow head.
   */
  @Override
  public void draw(GL2 gl, Appearance appearance) {
    appearance.applyAppearance(gl);
    gl.glBegin(GL.GL_TRIANGLES);
    gl.glVertex2f(topX, topY);
    gl.glVertex2f(target.x, target.y);
    gl.glVertex2f(botX, botY);
    gl.glEnd();
  }

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#intersects(javax.vecmath.Point3f, javax.vecmath.Vector3f)
   */
  @Override
  public boolean intersects(Point3f rayOrigin, Vector3f rayDirection) {
    throw new UnsupportedOperationException("Not Yet Implemented");
  }

  /* (non-Javadoc)
   * @see saf.ui.v3d.EdgeHead#intersects(javax.vecmath.Point3f)
   */
  @Override
  public boolean intersects(Point3f point) {
    return Utils3D.triangleIntersect(point, new Point3f(topX, topY, 0), new Point3f(target.x, target.y, 0), 
        new Point3f(botX, botY, 0));
  }
}
