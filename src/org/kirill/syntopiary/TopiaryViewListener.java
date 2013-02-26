package org.kirill.syntopiary;




/**
 * ParseTopiary listener interface.
 */
public interface TopiaryViewListener {
	
    public static class Adapter implements TopiaryViewListener {
        public void topiaryViewChanged(TopiaryView topiaryView) {
        }

    }

	public void topiaryViewChanged(TopiaryView topiaryView);

}
