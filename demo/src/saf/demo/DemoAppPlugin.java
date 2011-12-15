package saf.demo;

import org.java.plugin.Plugin;
import saf.core.runtime.IApplicationRunnable;
import saf.core.ui.GUICreator;
import saf.core.ui.IAppConfigurator;
import saf.core.ui.ISAFDisplay;
import saf.core.ui.Workspace;

/**
 * The SAF plugin class for the DemoApplication. The run method boots
 * the application creation process.
 *
 * @author Nick Collier
 */
public class DemoAppPlugin extends Plugin implements IApplicationRunnable {

  // this makes the application menu etc. appear correctly on
  // OSX
  static {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SAF Demo");
  }

  protected void doStart() throws Exception {}

	protected void doStop() throws Exception {}

	public void run(String[] args) {
		// 5 initial states in creating a SAF-based application

    // 1. create the application object - there is nothing special about this
    // except that it is accessible to SAF created menu and toolbar actions 
    // view the Workspace.
    DemoApplication application = new DemoApplication();

    // 2. create an IAppConfigurator -- this is a callback type interface
    // that is used for the initial configuration of the application
    IAppConfigurator configurator = new DemoAppConfigurator(application);

    // 3. wrap the appication object in a Workspace. This workspace is
    // available to all the actions referenced in the plugin_jpf.xml
    Workspace<DemoApplication> workspace = new Workspace<DemoApplication>(application);

    // 4. Create the display. This uses the configuration info produced by
    // the IAppConfigurator and associates the workspace with actions.
    ISAFDisplay display = GUICreator.createDisplay(configurator, workspace);

    // 5. Run the display -- this is equivalent to JFrame.setVisible(true)
    // in a pure Swing application.
    GUICreator.runDisplay(configurator, display);
	}
}
