package org.kirill.syntopiary;

import org.apache.pivot.collections.Sequence;

public interface SyntopTextAreaListener {
	
    public static class Adapter implements SyntopTextAreaListener {
        @Override
        public void enterPressed(SyntopTextArea syntopTextArea) {
            // empty block
        }
    }

    public void enterPressed(SyntopTextArea syntopTextArea);
}
