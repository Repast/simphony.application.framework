package saf.core.ui.actions;

import saf.core.ui.AppPreferences;
import saf.core.ui.IAppConfigurator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Nick Collier
 */
@SuppressWarnings("serial")
public class ExitAction extends AbstractSAFAction {

  private IAppConfigurator configurator;
  private JFrame frame;
  private AppPreferences prefs;

  public ExitAction(IAppConfigurator configurator, JFrame frame, AppPreferences prefs) {
    this.configurator = configurator;
    this.frame = frame;
    this.prefs = prefs;
  }

  public void actionPerformed(ActionEvent e) {
    if (configurator.preWindowClose()) {
      
      prefs.saveApplicationBounds(frame.getBounds());
      if (prefs.usingSavedViewLayout()) prefs.saveViewLayout();
      frame.dispose();
      configurator.postWindowClose();
      System.exit(0);
    }
  }
}