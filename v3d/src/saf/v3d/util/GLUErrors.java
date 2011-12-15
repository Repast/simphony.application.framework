/**
 * 
 */
package saf.v3d.util;

import javax.media.opengl.glu.GLU;

/**
 * JOGL is missing the GLU errors so we add them here.
 * 
 * @author Nick Collier
 */
public class GLUErrors {

  /*
  private static final String[] gluNurbsErrors = { " ", "spline order un-supported",
      "too few knots", "valid knot range is empty", "decreasing knot sequence knot",
      "knot multiplicity greater than order of spline",
      "gluEndCurve() must follow gluBeginCurve()", "gluBeginCurve() must precede gluEndCurve()",
      "missing or extra geometric data", "can't draw piecewise linear trimming curves",
      "missing or extra domain data", "missing or extra domain data",
      "gluEndTrim() must precede gluEndSurface()",
      "gluBeginSurface() must precede gluEndSurface()",
      "curve of improper type passed as trim curve",
      "gluBeginSurface() must precede gluBeginTrim()", "gluEndTrim() must follow gluBeginTrim()",
      "gluBeginTrim() must precede gluEndTrim()", "invalid or missing trim curve",
      "gluBeginTrim() must precede gluPwlCurve()",
      "piecewise linear trimming curve referenced twice",
      "piecewise linear trimming curve and nurbs curve mixed", "improper usage of trim data type",
      "nurbs curve referenced twice", "nurbs curve and piecewise linear trimming curve mixed",
      "nurbs surface referenced twice", "invalid property",
      "gluEndSurface() must follow gluBeginSurface()", "intersecting or misoriented trim curves",
      "intersecting trim curves", "UNUSED", "unconnected trim curves", "unknown knot error",
      "negative vertex count encountered", "negative byte-stride encounteed",
      "unknown type descriptor", "null control point reference",
      "duplicate point on piecewise linear trimming curve", };

  public static String getGluNURBSErrorString(int errno) {
    errno = errno - (GLU.GL_ERROR1 - 1);
    return gluNurbsErrors[errno];
  }
  */

  private static final String gluTessErrors[] = { " ",
      "gluTessBeginPolygon() must precede a gluTessEndPolygon()",
      "gluTessBeginContour() must precede a gluTessEndContour()",
      "gluTessEndPolygon() must follow a gluTessBeginPolygon()",
      "gluTessEndContour() must follow a gluTessBeginContour()", "a coordinate is too large",
      "need combine callback", };

  public static String getTessErrorString(int errno) {
    errno = errno - (GLU.GLU_TESS_ERROR1 - 1);
    return gluTessErrors[errno];
  }

}
