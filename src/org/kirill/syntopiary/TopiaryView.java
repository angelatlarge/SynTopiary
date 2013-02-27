package org.kirill.syntopiary;

import java.io.File;

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.WTKListenerList;

/**
 * Component that displays a tree.
 */
@DefaultProperty("image")
public class TopiaryView extends Component {
	private boolean fDrawTextBoundaries = false;
	private boolean fDrawNodeBoundaries = false;
	private boolean fDrawFullBoundaries = false;

    private ParseTopiary parseTopiary;
	
    private static class TopiaryViewListenerList extends WTKListenerList<TopiaryViewListener>
        implements TopiaryViewListener {
        @Override
        public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
        	for (TopiaryViewListener listener : this) {
        		listener.topiaryViewCosmeticOptionsChanged(topiaryView);
        	}
        }
        public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewLayoutOptionsChanged(topiaryView);
            }
        }
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewOutputRequestSVG(topiaryView, file);
            }
        }

    }
    private TopiaryViewListenerList topiaryViewListeners = new TopiaryViewListenerList();


    public TopiaryView() {
        parseTopiary = new ParseTopiary();
        installSkin(TopiaryView.class);
    }

    public ParseTopiary getParseTopiary() {
        return parseTopiary;
    }

    public ListenerList<TopiaryViewListener> getTopiaryViewListeners() {
        return topiaryViewListeners;
    }

    public boolean getDrawTextBoundaries() { return fDrawTextBoundaries; }
    public void setDrawTextBoundaries(boolean newValue) { 
    	fDrawTextBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public boolean getDrawNodeBoundaries() { return fDrawNodeBoundaries; }
    public void setDrawNodeBoundaries(boolean newValue) { 
    	fDrawNodeBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public boolean getDrawFullBoundaries() { return fDrawFullBoundaries; }
    public void setDrawFullBoundaries(boolean newValue) { 
    	fDrawFullBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public void generateSVG( File file ) {
    	topiaryViewListeners.topiaryViewOutputRequestSVG(this, file);
    }
}
