package org.kirill.syntopiary;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Theme;

/*
 * TODO: Copy and paste
 * TODO: Copyright notices
 * TODO: In ternary branching, the middle lines needs to be vertical
 * TODO: Options parsing
 * TODO: Text formatting
 * TODO: github
 * TODO: Other export formats
 * TODO: Caret cursor for the text box
 * TODO: Hat option
 * 
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
