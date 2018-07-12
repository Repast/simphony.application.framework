package saf.core.ui.osx;

import java.awt.Image;

/**
 * Inteface for classes that can adapt an application to MacOS.
 * @author Nick Collier
 */
public interface MacOSAdapter {
    
    public void registerMacOSXApplication();
    public void registerDockImage(Image img);
    public void enablePrefs();

}
