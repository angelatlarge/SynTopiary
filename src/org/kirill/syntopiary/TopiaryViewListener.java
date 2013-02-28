package org.kirill.syntopiary;
import java.awt.datatransfer.Clipboard;
import java.io.File;




/**
 * ParseTopiary listener interface.
 */
public interface TopiaryViewListener {
	
    public static class Adapter implements TopiaryViewListener {
    	public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
    	}
    	public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
    	}
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
        }
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard) {
        }
    }

    /**
     * Changes that affect how things look, but do not affect the layout
     */
    public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView);
    
    /**
     * Changes that affect how nodes are layed out
     */
	public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView);

	/**
	 * Request for SVG file output
	 */
	public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file);
	
    /**
     * Request for SVG file to be placed on the clipboard
     */
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard);
}
