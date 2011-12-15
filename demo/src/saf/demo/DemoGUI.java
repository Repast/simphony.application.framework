package saf.demo;

import saf.core.ui.GUIBarManager;
import saf.core.ui.dock.DockingManager;

import javax.swing.*;

/**
 * Mediates access to the demo application GUI.
 *
 * @author Nick Collier
 */
public class DemoGUI {

  private GUIBarManager barManager;
  private JFrame frame;
  private DockingManager manager;

  public DemoGUI(JFrame frame, GUIBarManager barManager, DockingManager manager) {
    this.frame = frame;
    this.barManager = barManager;
    this.manager = manager;
  }

  /**
   * Called when the application has started
   */
  public void appStarted() {
    // selects the "CPanel"
    manager.getDockable("CPanel").toFront();
  }

  /**
   * Displays the message in a message dialog and in the first status bar.
   *
   * @param msg the message to show
   */
  public void showMessage(String msg) {
    JOptionPane.showMessageDialog(frame, msg);
    barManager.setStatusBarText("first", msg);
  }

  /**
   * Gets the main application frame.
   *
   * @return the main application frame
   */
  public JFrame getFrame() {
    return frame;
  }
}
