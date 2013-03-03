/* Copyright 2013 Kirill Shkovsky
 * 
 * This file is part of SynTopiary.
 * 
 * SynTopiary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SynTopiary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SynTopiary.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Portions of this software are under Apache Software License, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 */
package org.kirill.syntopiary;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Theme;

/*
 * TODO: Draw target arrows
 * TODO: Automatic empty parents for orphaned children
 * TODO: Javadoc
 * TODO: EPS copy
 * TODO: CGM file support?
 * TODO: Copy and paste text in the tree specification
 * TODO: EMF copy
 * TODO: EMF paste
 * TODO: Options parsing
 * TODO: Text formatting
 * TODO: Caret cursor for the text box
 * TODO: Hat drawing
 * TODO: Non-vector file export?
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
