package saf.demo;

import saf.core.ui.*;
import saf.core.ui.SplashScreen;
import saf.core.ui.dock.DockingManager;

import javax.swing.*;
import java.awt.*;

/**
 * Application configurator for the Demo application. The implemented methods are
 * called by the SAF application initialization mechanism at specific points in the
 * applications lifecycle. On application start up, the order in which they
 * are called is:
 *
 * <ol>
 * <li> #preWindowOpen </li>
 * <li> #createLayout </li>
 * <li> #fillBars </li>
 * <li> #postWindowOpen </li>
 * </ol>
 *
 * These methods are used to initialize and configure the application as it
 * starts.<p>
 *
 * This demo uses the GUIInitializer class to perform GUI setup. See that class
 * for more details.
 *
 * @author Nick Collier
 */
public class DemoAppConfigurator implements IAppConfigurator {

  private DemoApplication app;
  private GUIInitializer guiInit;

  // the splash screen components
  private SplashScreen screen;
  private LinePanel linePanel;

  public DemoAppConfigurator(DemoApplication app) {
    this.app = app;
    guiInit = new GUIInitializer();
  }

  /**
	 * This is called immediately prior to the application's main window opening. It sets
   * this application's look and feel, and uses the customizer parameter to set the initial
   * window's size, title and so on. This also starts an example splash screen.
	 *
	 * @param customizer the customizer used to customize the initial application
	 * window
	 *
	 * @return always true in this case, but it could return false to stop
   * application startup.
	 */
  public boolean preWindowOpen(IWindowCustomizer customizer) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      ex.printStackTrace();
    }


    customizer.useStoredFrameBounds(800, 600);
    customizer.useSavedLayout();
    customizer.setTitle("Demo Application");
    customizer.setWindowMenuLabel("Window");

    linePanel = new LinePanel();
    linePanel.setText("Demo Application");
    linePanel.start();
    screen = new SplashScreen(linePanel, true);
    linePanel.setPreferredSize(new Dimension(400, 200));
    screen.setMaxProgress(100000000);
    screen.display();

    // this loop simulates  some time consuming
    // setup so we can see the splash screen
    // updating. 
    for (int i = 0; i < 100000000; i++) {
      if (i % 10000 == 0) screen.setProgress(i);
    }

    return true;
  }


  /**
   * Called to give the application a chance to programmatically
   * add / remove any toolbar / status bar / menu bar items or modify the
   * state (enabled / disabled)
   * of those items.
   *
   * @param guiBarManager the manager used to perform "bar" work
   */
  public void fillBars(GUIBarManager guiBarManager) {
    guiInit.initializeBars(guiBarManager, app);
  }

  /**
   * Called immediately after the main frame of the demo is
   * is opened.
   *
   * @param display the application display
   */
  public void postWindowOpen(ISAFDisplay display) {

    DemoGUI gui = guiInit.initializeFrame(display.getFrame());
    app.initGUI(gui);

    linePanel.stop();
    screen.close();
  }

  /**
   * Called immediately prior to the main application window closing.
   * This can be used to stop application exit by returning false.
   *
   * @return true or false depending on whether the application should exit.
   */
  public boolean preWindowClose() {
    return app.exit();
  }

  /**
   * Called once the main application frame has been closed.
   */
  public void postWindowClose() {

  }

  /**
   * Called by the application startup mechanism to give the application
   * a chance to create the initial layout. That is, load the initial GUI elements.
   *
   * @param dockingManager the docking manager used to create dockable frames etc
   */
  public void createLayout(DockingManager dockingManager) {
    guiInit.initializeLayout(dockingManager);

  }
}
