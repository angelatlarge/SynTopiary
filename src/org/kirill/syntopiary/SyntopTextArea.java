package org.kirill.syntopiary;

import org.apache.pivot.wtk.TextArea;

public class SyntopTextArea extends TextArea {
    public void insertText(CharSequence text, int index) {
    	System.out.format("Got character: \"%s\"\n", text.toString()); 
    	if (text.equals("\n")) {
    		// Enter processing
    	} else {
    		super.insertText(text, index);
    	}
    }
}
