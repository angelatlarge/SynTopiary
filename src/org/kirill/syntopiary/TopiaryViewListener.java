package org.kirill.syntopiary;
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
     * Request for file output
     */
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file);
}
