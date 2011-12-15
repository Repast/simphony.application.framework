package saf.demo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Nick Collier
 *         Date: Jul 23, 2008 11:19:22 AM
 */
public class HeavyWeightExample extends Canvas {

  private String text = "Heavyweight AWT Component";
  
  private BufferedImage img;

  public void paint(Graphics g) {

    int width = getWidth();
    int height = getHeight();
    if (img == null || img.getWidth() != width || img.getHeight() != height) {
      img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    Graphics2D g2 = (Graphics2D) img.getGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.LIGHT_GRAY);
    g2.fillRect(0, 0, width, height);


    Font font = g2.getFont();
    g2.setFont(font.deriveFont(Font.BOLD, 20f));
    g2.setColor(Color.BLACK);
    Rectangle2D rect = g2.getFontMetrics().getStringBounds(text, g2);
    int centerW = width / 2;
    int centerH = height / 2;
    double x = centerW - rect.getWidth() / 2;
    double y = centerH - rect.getHeight() / 2;
    g2.drawString(text, (float) x, (float) y);
    g2.setFont(font);

    g2.dispose();
    g.drawImage(img, 0, 0, null);
  }

}
