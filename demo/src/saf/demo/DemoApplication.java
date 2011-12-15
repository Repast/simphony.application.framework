package saf.demo;

import saf.core.ui.util.FileChooserUtilities;

import javax.swing.*;
import java.io.File;

/**
 * Main application class for the saf demo application.
 *
 * @author Nick Collier
 */
public class DemoApplication {

  private DemoGUI gui;

  /**
   * Called by the OpenDirectoryAction which has been associated with
   * a menu item and toolbar button in plugin_jpf.xml. As a result,
   * this method is called when the Open Directory menu item or
   * button are clicked.
   */
  public void openDirectory() {
    // demonstrates use of FileChooserUtilities. There are
    // methods for opening files and dirs, setting file filters and
    // so forth in addition to this simple one.
    String home = System.getProperty("user.home");
    // by default this will use the main application frame as its parent component
    File file = FileChooserUtilities.getOpenDirectory(new File(home));
    String msg = file == null ? "Operation canceled" : "You chose: " + file.getAbsolutePath();
    gui.showMessage(msg);


  }

  /**
   * Called by the OpenFileAction which has been associated with
   * a menu item and toolbar button in plugin_jpf.xml. As a result,
   * this method is called when the Open File menu item or
   * button are clicked.
   */
  public void openFile() {
    // demonstrates use of FileChooserUtilities. There are
    // methods for opening files and dirs, setting file filters and
    // so forth in addition to this simple one.
    String home = System.getProperty("user.home");
    // by default this will use the main application frame as its parent component
    File file = FileChooserUtilities.getOpenFile(new File(home));
    String msg = file == null ? "Operation canceled" : "You chose: " + file.getAbsolutePath();
    gui.showMessage(msg);
  }

  /**
   * This is called by the DeomAppConfigurator immediately prior to
   * the main frame closing and the application exiting. See
   * DeomAppConfigurator.preWindowClose.
   *
   * @return true if the application should exit, otherwise false.
   */
  public boolean exit() {

    int retVal = JOptionPane.showConfirmDialog(gui.getFrame(), "Are you sure you want to exit?", "Confirm Exit",
            JOptionPane.OK_CANCEL_OPTION);
    return retVal == JOptionPane.OK_OPTION;
  }

  /**
   * Initializes the GUI. This is called by DemoAppConfigurator
   * as part of the initial application configuration.
   *
   * @param gui the gui mediator for the demo application
   */
  public void initGUI(DemoGUI gui) {
    this.gui = gui;
    gui.appStarted();
  }

  /**
   * Called when an edit menu item is clicked
   *
   * @param actionCommand the action command associated with the
   *                      menu item or button
   */
  public void edit(String actionCommand) {
    gui.showMessage(actionCommand + " was clicked");
  }
}
