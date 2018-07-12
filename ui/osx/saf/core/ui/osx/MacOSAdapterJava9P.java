package saf.core.ui.osx;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.ActionEvent;

import saf.core.ui.GUIConstants;
import saf.core.ui.actions.ActionFactory;

public class MacOSAdapterJava9P implements MacOSAdapter {

    @Override
    public void registerMacOSXApplication() {
	Desktop.getDesktop().setQuitHandler(new QuitHandler() {
	    @Override
	    public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
		ActionFactory.getInstance().getAction(GUIConstants.EXIT_ACTION)
			.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "exit"));
	    }
	});
    }

    @Override
    public void registerDockImage(Image img) {
	if (img != null) Taskbar.getTaskbar().setIconImage(img);
    }

    @Override
    public void enablePrefs() {
	Desktop.getDesktop().setPreferencesHandler(null);
    }
}
