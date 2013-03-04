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
 * TODO: Hat drawing
 * TODO: I don't think we deal well with empty parenthesis
 * TODO: Error log in the status bar
 * TODO: Support boxes and highlighting?
 * TODO: Replace node data members to support x and y as a part of the bounding box?
 * TODO: More efficient algorithm (line sweep?) for movement arrow intersection?
 * TODO: Move efficient way of finding nodes between?
 * TODO: Option to draw a ll children on the same level vertically aligned?
 * TODO: Example trees?
 * TODO: Larger output?
 * TODO: Different font?
 * TODO: Automatic empty parents for orphaned children
 * TODO: EPS copy
 * TODO: CGM file support?
 * TODO: Copy and paste text in the tree specification
 * TODO: EMF copy
 * TODO: EMF paste
 * TODO: Support for cutting and pasting back into the app
 * TODO: Text formatting
 * TODO: Test connections and automatic names
 * TODO: Caret cursor for the text box
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
