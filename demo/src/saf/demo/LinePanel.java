package saf.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Nick Collier
 *         Date: Jul 16, 2008 12:11:24 PM
 */
public class LinePanel extends JPanel {

  Timer timer;
  String text;

  private java.util.List<Line2D> lines = new ArrayList<Line2D>();

  public LinePanel() {
    setOpaque(true);
  }

  public void setText(String text) {
    this.text = text;
  }

  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());
    for (Line2D line : lines) {
      Color color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
      g.setColor(color);
      g2.draw(line);
    }

    if (text != null) {
      Font font = g2.getFont();
      g2.setFont(font.deriveFont(Font.BOLD, 20f));
      g2.setColor(Color.BLACK);
      Rectangle2D rect = g2.getFontMetrics().getStringBounds(text, g2);
      int centerW = getWidth() / 2;
      int centerH = getHeight() / 2;
      double x = centerW - rect.getWidth() / 2;
      double y = centerH - rect.getHeight() / 2;
      g2.drawString(text, (float) x, (float) y);
      g2.setFont(font);
    }
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }

  Point2D getPoint() {
    return new Point2D.Double(getWidth() * Math.random(), getHeight() * Math.random());

  }

  public void start() {
    ActionListener runner = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lines.size() > 50) lines.clear();
        for (int i = 0; i < 10; i++) {

          Line2D line = new Line2D.Float(getPoint(), getPoint());
          lines.add(line);
        }

        repaint();
      }
    };

    timer = new Timer(250, runner);
    timer.start();
  }

  public void stop() {
    if (timer != null) timer.stop();
  }
}
