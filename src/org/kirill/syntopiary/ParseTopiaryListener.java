package org.kirill.syntopiary;




/**
 * ParseTopiary listener interface.
 */
public interface ParseTopiaryListener {
	
    public static class Adapter implements ParseTopiaryListener {
        public void parseTopiaryChanged(ParseTopiary parseTopiary) {
        }

    }

    public void parseTopiaryChanged(ParseTopiary parseTopiary);

}
