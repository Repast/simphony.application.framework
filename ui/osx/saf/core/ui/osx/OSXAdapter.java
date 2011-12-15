package saf.core.ui.osx;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import saf.core.ui.actions.ActionFactory;
import saf.core.ui.GUIConstants;

import java.awt.event.ActionEvent;

/**
 * @author Nick Collier
 */
public class OSXAdapter extends ApplicationAdapter {

  private static OSXAdapter theAdapter;
  private static com.apple.eawt.Application theApplication;

  public void handleQuit(ApplicationEvent ae) {
    ae.setHandled(false);
    ActionFactory.getInstance().getAction(GUIConstants.EXIT_ACTION).
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "exit"));
  }

  // The main entry-point for this functionality.  This is the only method
  // that needs to be called at runtime, and it can easily be done using
  // reflection (see MyApp.java)
  public static void registerMacOSXApplication() {
    if (theApplication == null) {
      theApplication = new com.apple.eawt.Application();
    }

    if (theAdapter == null) {
      theAdapter = new OSXAdapter();
    }
    theApplication.addApplicationListener(theAdapter);
  }

  // Another static entry point for EAWT functionality.  Enables the
  // "Preferences..." menu item in the application menu.
  public static void enablePrefs(boolean enabled) {
    if (theApplication == null) {
      theApplication = new com.apple.eawt.Application();
    }
    theApplication.setEnabledPreferencesMenu(enabled);
  }
}
