package saf.demo.action;

import saf.core.ui.actions.AbstractSAFAction;
import saf.core.ui.dock.DockableFrame;
import saf.core.ui.dock.DockingManager;
import saf.core.ui.dock.Perspective;
import saf.demo.DemoApplication;

import java.awt.event.ActionEvent;

/**
 * An action that will float all the dockables from
 * perspective.one. Its does this by getting the
 * DockingManager from the workspace and then floating
 * all the dockables in that workspace.
 *
 * @author Nick Collier
 */
public class FloatAllAction extends AbstractSAFAction<DemoApplication> {

	public void actionPerformed(ActionEvent e) {
		DockingManager dockingManager = workspace.getDockingManager();
		Perspective perspective = dockingManager.getPerspective("perspective.one");
    if (perspective.isActive()) {
      for (DockableFrame frame : perspective.getDockables()) {
        frame.doFloat();
      }
    }
	}
}
