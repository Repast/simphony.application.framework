package saf.v3d.picking;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import saf.v3d.Canvas;
import saf.v3d.scene.VSpatial;
import saf.v3d.util.RayCreator;

public abstract class AbstractPickSupport extends MouseAdapter {

  protected boolean mousePressed = false;
  protected int x;
  protected int y;
  // logical (DPI-unscaled) size of the component the mouse coordinates are
  // relative to, captured at click time so the pick can be converted into
  // render-surface pixels.
  protected int componentWidth;
  protected int componentHeight;
  private int modifiers;
  
  private List<PickListener> listeners = new ArrayList<PickListener>();
  protected RayCreator rayCreator = new RayCreator();
  protected Canvas canvas;
  
  public AbstractPickSupport(Canvas canvas) {
    this.canvas = canvas;
  }
  
  public void addPickListener(PickListener listener) {
    listeners.add(listener);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
      mousePressed = true;
      x = e.getX();
      y = e.getY();
      Component source = e.getComponent();
      componentWidth = source.getWidth();
      componentHeight = source.getHeight();
      modifiers = e.getModifiersEx();
      canvas.update();
    }
  }

  protected void firePickEvent(List<VSpatial> items) {
    PickEvent evt = new PickEvent(modifiers, items);
    for (PickListener listener : listeners) {
      listener.pickPerformed(evt);
    }
  }
}