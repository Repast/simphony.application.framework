package saf.demo.action;

import saf.demo.DemoApplication;
import saf.core.ui.actions.AbstractSAFAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This action handles the edit menu item cicks. This uses the
 * action command set in the plugin xml.
 *
 * @author Nick Collier
 */
public class EditAction extends AbstractSAFAction<DemoApplication> {

  public void actionPerformed(ActionEvent e) {
    AbstractButton source = (AbstractButton) e.getSource();
    workspace.getApplicationMediator().edit(source.getActionCommand());
  }
}
