package saf.demo;

import saf.core.ui.GUIBarManager;
import saf.core.ui.dock.DockableFrame;
import saf.core.ui.dock.DockingManager;
import static saf.core.ui.dock.DockingManager.MinimizeLocation.*;
import saf.core.ui.dock.Location;
import saf.core.ui.event.*;
import saf.j3d.sphere_motion.SphereMotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;

/**
 * A factory class for creating and initializing the demo
 * GUI. This is not strictly necessary but it does separate
 * out the step by step creation of the DemoGUI from the
 * DemoGUI itself. This means that the DemoGUI should be in
 * a valid state when created rather than in need of step by
 * step initialization before it can be used.
 * <p/>
 * Note that in this simple DEMO case there's no real model that the GUI
 * is a view of. In a real application the application mediator (e.g.
 * DemoApplication) would be / produce / have access to the model and
 * that could be passed in here and used.
 *
 * @author Nick Collier
 */
public class GUIInitializer {

  private DockingManager dockingManager;
  private GUIBarManager barManager;


  // Adds additional panels to the B and C dockable frame groups.
  Action addBCPanels = new AbstractAction("Add Panels to B and C Groups") {
    public void actionPerformed(ActionEvent e) {
      DockableFrame view = dockingManager.createDockable("Panel C", new JScrollPane(new JTree()));
      view.setTitle("Panel C in Group B");
      dockingManager.addDockableToGroup("perspective.one", "B", view);
      dockingManager.dock(dockingManager.getDockable("B"), view, Location.NORTH, .5f);

      view = dockingManager.createDockable("Panel D", new JScrollPane(new JTree()));
      view.setTitle("Panel D in Group C");
      dockingManager.addDockableToGroup("perspective.one", "C", view);

      // only able to call this action once.
      setEnabled(false);
    }
  };

  // Adds the "A" panels to the root group
  Action addAPanels = new AbstractAction("Add A Panels to Root") {
    public void actionPerformed(ActionEvent e) {
      LinePanel panel1 = new LinePanel();
      DockableFrame view = dockingManager.createDockable("APanel1", panel1, TOP,
              DockingManager.FLOAT | DockingManager.MINIMIZE | DockingManager.CLOSE);
      view.setTitle("A Panel 1");
      dockingManager.addDockableToGroup("perspective.one", "A", view);
      
      
      LinePanel panel2 = new LinePanel();
      view = dockingManager.createDockable("APanel2", panel2, LEFT);
      view.setTitle("A Panel 2");
      dockingManager.addDockableToGroup("perspective.one", "A", view);

     
      panel1.start();
     
      panel2.start();
     

      // only able to call this once.
      setEnabled(false);
    }
  };

  // action that switches the current perspective
  Action pSwitch = new AbstractAction("Switch P") {
    private boolean setDefault = false;

    public void actionPerformed(ActionEvent e) {
      String pid = setDefault ? "perspective.one" : "perspective.two";
      dockingManager.setPerspective(pid);
      setDefault = !setDefault;
    }
  };


  /**
   * Initializes the layout by creating some DockableFrame and adding
   * them to the DockingManager.
   *
   * @param dockingManager the applications docking manager
   */
  public void initializeLayout(DockingManager dockingManager) {
    this.dockingManager = dockingManager;
    initializePerspectiveOne();
    initializePerspectiveTwo();
  }

  // initializes perspective.one by adding various dockable frames to it
  // Also adds a toolbar to a dockable together with actions for it.
  private void initializePerspectiveOne() {
    // creates a Dockable frame with a text area that display
    // the output of our dockable frame etc. listeners.
    JTextArea area = new JTextArea();
    area.setAutoscrolls(true);
    dockingManager.addDockableListener(new DebugDockableFrameListener(area));
    dockingManager.addPerspectiveSelectionListener(new DebugPerspectiveSelectionListener(area));
    dockingManager.addDockableSelectionListener(new DebugDockableSelectionListener(area));

    DockableFrame dockable = dockingManager.createDockable("BPanel", new JScrollPane(area), RIGHT);
    dockable.setTitle("B Panel 1");
    dockingManager.addDockableToGroup("perspective.one", "B", dockable);

    // creates a DockableFrame with a toolbar and a label as its content
    // the toolbar bar button will operate on this label and on the
    // content panel itself.
    JLabel label = new JLabel(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()));
    label.setHorizontalAlignment(JLabel.CENTER);
    dockable = dockingManager.createDockable("CPanel", label);
    dockable.setTitle("C Panel 1");

    JToolBar bar = new JToolBar();
    bar.add(new TimeChangeAction(label));
    bar.add(new ChangeColorAction(label, dockingManager));

    bar.add(new ReplaceAction(label, dockingManager));

    dockable.addToolBar(bar);
    dockingManager.addDockableToGroup("perspective.one", "C", dockable);


    /*
    SphereMotion motion = new SphereMotion(new String[]{"-point"});
    JPanel panel = motion.getDrawingPanel();
    panel.setMinimumSize(new Dimension(10, 10));
    */

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new HeavyWeightExample(), BorderLayout.CENTER);
    // this is the key to making heavyweight components work
    // as dockables. There enclosing JComponent's must have
    // a minimum size set. Without this, the heavyweight cannot
    // be resized it seems.
    panel.setMinimumSize(new Dimension(10, 10));
    //dockable = dockingManager.createDockable("heavyEx", panel);
    //dockable.setTitle("Heavy Weight Example");
    //dockingManager.addDockableToGroup("perspective.one", "A", dockable);
  
  }

  // initializes perspective two.
  private void initializePerspectiveTwo() {

    DockableFrame dockable = dockingManager.createDockable("e", new JScrollPane(new JTree()));
    dockable.setTitle("E P2: Bottom");
    dockingManager.addDockableToGroup("perspective.two", "bottom", dockable);

    dockable = dockingManager.createDockable("B2", new JPanel());
    dockable.setTitle("B2: Bottom");
    dockingManager.addDockableToGroup("perspective.two", "bottom", dockable);

    dockable = dockingManager.createDockable("otherA", new JPanel());
    dockable.setTitle("Other A P2: Middle");
    dockingManager.addDockableToGroup("perspective.two", "middle", dockable);

    // top is the root group
    dockable = dockingManager.createDockable("otherB", new JPanel());
    dockable.setTitle("Other B P2: Top");
    dockingManager.addDockableToGroup("perspective.two", "top", dockable);
  }

  /**
   * Performs any necessary initialization of the status bars,
   * menu bars, or toolbar.
   *
   * @param manager the manager to use for initialization.
   * @param app the application object
   */
  public void initializeBars(GUIBarManager manager, final DemoApplication app) {
    this.barManager = manager;
    // programmatically adds some custom actions to the toolbar
    // parameters are: toolbar group id (see ui.properties file),
    // action id, action
    this.barManager.addToolBarAction("other", "add.bc.panels", addBCPanels);
    this.barManager.addToolBarAction("other", "switch.p", pSwitch);
    this.barManager.addToolBarAction("other", "add.a.panels", addAPanels);

    // programmatically adds a menu item
    // "edit" is the id of the menu as defined in plugin_jpf.xml
    this.barManager.addMenuItem("edit.item.3", "edit", new AbstractAction("Edit Menu Item 3") {
      public void actionPerformed(ActionEvent e) {
        app.edit("Edit Menu Item 3");
      }
    });

  }

  /**
   * Initializes the GUI with the main application frame.
   *
   * @param frame the main application frame
   * @return the fully intialized DemoGUI.
   */
  public DemoGUI initializeFrame(JFrame frame) {
    return new DemoGUI(frame, barManager, dockingManager);
  }
}

// changes a label's text to the current time.
class TimeChangeAction extends AbstractAction {

  private JLabel label;

  TimeChangeAction(JLabel label) {
    super("Time");
    this.label = label;
  }

  public void actionPerformed(ActionEvent e) {
    label.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()));
  }
}

// action to change the color of a panel
// and its label. This is demonstration of

// DockableFrame.getContentPane()
class ChangeColorAction extends AbstractAction {

  private DockingManager dockingManager;
  private JLabel label;

  ChangeColorAction(JLabel label, DockingManager manager) {
    super("Color");
    this.label = label;
    this.dockingManager = manager;
  }

  public void actionPerformed(ActionEvent e) {
    JPanel panel = dockingManager.getDockable("CPanel").getContentPane();
    Color color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    panel.setBackground(color);

    color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    label.setForeground(color);
  }
}

// replaces  the contents of the CPanel's

// content pane with some other content
class ReplaceAction extends AbstractAction {

  private DockingManager dockingManager;
  private JLabel label;

  ReplaceAction(JLabel label, DockingManager manager) {
    super("Replace");
    this.label = label;
    this.dockingManager = manager;
  }

  public void actionPerformed(ActionEvent e) {
    JPanel panel = dockingManager.getDockable("CPanel").getContentPane();
    panel.removeAll();
    panel.setLayout(new GridLayout(2, 1));
    panel.add(new JScrollPane(new JTree()));
    panel.add(label);
    panel.validate();
  }
}

// example DockableSelectionListener
class DebugDockableSelectionListener implements DockableSelectionListener {

  JTextArea area;

  DebugDockableSelectionListener(JTextArea area) {
    this.area = area;
  }

  public void selectionGained(DockableSelectionEvent evt) {

    area.append("SELECTION GAINED: " + evt + "\n");
  }

  public void selectionLost(DockableSelectionEvent evt) {

    area.append("SELECTION LOST: " + evt + "\n");
  }
}

// example PerspectiveSelectionListener
class DebugPerspectiveSelectionListener implements PerspectiveSelectionListener {

  JTextArea area;

  DebugPerspectiveSelectionListener(JTextArea area) {
    this.area = area;
  }


  public void perspectiveChanged(PerspectiveSelectionEvent evt) {
    area.append("PERSPECTIVE CHANGED: " + evt + "\n");
  }


  public void perspectiveChanging(PerspectiveSelectionEvent evt) {
    area.append("PERSPECTIVE CHANGING: " + evt + "\n");
  }
}

// example DockableFrameListener
class DebugDockableFrameListener implements DockableFrameListener {

  JTextArea area;

  DebugDockableFrameListener(JTextArea area) {
    this.area = area;
  }

  public void dockableHidden(DockableFrameEvent evt) {
    area.append("HIDDEN: " + evt.getDockable().getID() + "\n");
  }

  public void dockableFloated(DockableFrameEvent evt) {
    area.append("FLOATED: " + evt.getDockable().getID() + "\n");
  }

  public void dockableMaximized(DockableFrameEvent evt) {

    area.append("MAXIMIZED: " + evt.getDockable().getID() + "\n");
  }

  public void dockableRestored(DockableFrameEvent evt) {

    area.append("RESTORED: " + evt.getDockable().getID() + "\n");
  }

  public void dockableClosing(DockableFrameEvent evt) {

    area.append("CLOSING: " + evt.getDockable().getID() + "\n");
  }

  public void dockableFloating(DockableFrameEvent evt) {

    area.append("FLOATING: " + evt.getDockable().getID() + "\n");
  }

  public void dockableMaximizing(DockableFrameEvent evt) {

    area.append("MAXIMIZING: " + evt.getDockable().getID() + "\n");
  }

  public void dockableMinimized(DockableFrameEvent evt) {

    area.append("MINIMIZED: " + evt.getDockable().getID() + "\n");
  }

  public void dockableMinimizing(DockableFrameEvent evt) {

    area.append("MINIMIZING: " + evt.getDockable().getID() + "\n");
  }

  public void dockableRestoring(DockableFrameEvent evt) {

    area.append("RESTORING: " + evt.getDockable().getID() + "\n");
  }

  public void dockableClosed(DockableFrameEvent evt) {

    area.append("CLOSED: " + evt.getDockable().getID() + "\n");
  }
}




