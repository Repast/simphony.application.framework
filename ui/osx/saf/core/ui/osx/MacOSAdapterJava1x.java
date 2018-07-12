package saf.core.ui.osx;

import java.awt.Image;
import java.awt.event.ActionEvent;

import saf.core.ui.GUIConstants;
import saf.core.ui.actions.ActionFactory;

import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/**
 * @author Nick Collier
 */
public class MacOSAdapterJava1x implements MacOSAdapter {

  // The main entry-point for this functionality.  This is the only method
  // that needs to be called at runtime.
  public void registerMacOSXApplication() {
    Application.getApplication().setQuitHandler(new QuitHandler() {

      @Override
      public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
	ActionFactory.getInstance().getAction(GUIConstants.EXIT_ACTION).
        actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "exit"));
      }
    });
    //theApplication.addApplicationListener(theAdapter);
  }
  
  public void registerDockImage(Image img) {
    //if (theApplication == null) {
    //  theApplication = new com.apple.eawt.Application();
    //}

    if (img != null) Application.getApplication().setDockIconImage(img);
  }

  // Enables the "Preferences..." menu item in the application menu.
  public void enablePrefs() {
    //if (theApplication == null) {
    //  theApplication = new com.apple.eawt.Application();
    //}
    Application.getApplication().setPreferencesHandler(null);
  }
}
