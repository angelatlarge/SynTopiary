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
    	System.out.format("Got character: \"%s\"\n", text.toString()); 
    	if (text.equals("\n")) {
    		// Enter processing
        	System.out.format("Notifying listeners\n"); 
    		syntopTextAreaListenerList.enterPressed(this);
    	} else {
    		super.insertText(text, index);
    	}
    }
}
