package test;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import saf.v3d.ShapeFactory2D;

public class ShapeTest {
  
  public static void main(String[] args) {
    Map<Integer, String> retMap = new HashMap<Integer, String>();
    retMap.put(PathIterator.SEG_CLOSE, "SEG_CLOSE");
    retMap.put(PathIterator.SEG_MOVETO, "SEG_MOVE_TO");
    retMap.put(PathIterator.SEG_LINETO, "SEG_LINE_TO");
    
    Shape shape = new Rectangle2D.Float(10, 10, 20, 30);
    //Shape shape = new Ellipse2D.Float(10, 10, 20, 20);
    
    PathIterator iter = shape.getPathIterator(null);
    float[] coords = new float[6];
    String rule = iter.getWindingRule() == PathIterator.WIND_EVEN_ODD ? "EVEN" : "NON_ZERO";
    System.out.println("Winding Rule: " + rule);
    while (!iter.isDone()) {
      int ret = iter.currentSegment(coords);
      System.out.printf("Segment: %s (%f, %f, %f, %f, %f, %f)%n", retMap.get(ret),
          coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
      Arrays.fill(coords, 0);
      iter.next();
      
    }
    
    new ShapeFactory2D().createShape(shape);
  }

}
