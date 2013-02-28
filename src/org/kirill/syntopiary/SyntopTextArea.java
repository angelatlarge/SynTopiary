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

import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.WTKListenerList;

public class SyntopTextArea extends TextArea {
	
    private static class SyntopTextAreaListenerList extends WTKListenerList<SyntopTextAreaListener>
        implements SyntopTextAreaListener {
        @Override
        public void enterPressed(SyntopTextArea syntopTextArea) {
            for (SyntopTextAreaListener listener : this) {
                listener.enterPressed(syntopTextArea);
            }
        }
    };
    
    private SyntopTextAreaListenerList syntopTextAreaListenerList = new SyntopTextAreaListenerList();
	
    public ListenerList<SyntopTextAreaListener> getSyntopTextAreaListeners() {
        return syntopTextAreaListenerList;
    }
    
    public void insertText(CharSequence text, int index) {
//    	System.out.format("Got character: \"%s\"\n", text.toString()); 
    	if (text.equals("\n")) {
    		// Enter processing
//        	System.out.format("Notifying listeners\n"); 
    		syntopTextAreaListenerList.enterPressed(this);
    	} else {
    		super.insertText(text, index);
    	}
    }
}
