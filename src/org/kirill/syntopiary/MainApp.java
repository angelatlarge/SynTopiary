package org.kirill.syntopiary;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Theme;

/*
 * TODO: Enter should regenerate the trees
 * TODO: "ENTER" behavior in TestArea should refresh rather an place enter
 * The TextAreaSkin.keyTyped(Component component, char character) method is responsible for processing keystrokes
 * It calls the insertText() method of TextArea, which does the paragraph splitting
 * 		
 * TODO: Other export formats
*/ 

@SuppressWarnings("unused")
public class MainApp implements Application {
    private SyntopWindow window = null;
 
    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
    	/* TODO: Not sure that skin mapping should go here... */
        Theme theme = Theme.getTheme();
        theme.set(TopiaryView.class, TopiaryViewSkin.class);
    	
        BXMLSerializer bxmlSerializer = new BXMLSerializer();
        window = (SyntopWindow)bxmlSerializer.readObject(MainApp.class, "syntop.bxml");
        window.open(display);
        window.applyTreeSpecificationText();
    }
 
    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }
        return false;
    }
 
    @Override
    public void suspend() {
    }
 
    @Override
    public void resume() {
    }
    
    public static void main(String[] args) {
        DesktopApplicationContext.main(MainApp.class, args);
    }    
}
