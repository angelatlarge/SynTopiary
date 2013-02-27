package org.kirill.syntopiary;




/**
 * ParseTopiary listener interface.
 */
public interface TopiaryViewListener {
	
    public static class Adapter implements TopiaryViewListener {
    	public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
    	}
        public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
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

}
