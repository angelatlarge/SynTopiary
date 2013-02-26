package org.kirill.syntopiary;

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.BindType;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.WTKListenerList;

/**
 * Component that displays a tree.
 */
@DefaultProperty("image")
public class TopiaryView extends Component {
    /**
     * Translates between tree and context data during data binding.
     */

    private static class TopiaryViewListenerList extends WTKListenerList<TopiaryViewListener>
        implements TopiaryViewListener {
        @Override
        public void topiaryViewChanged(TopiaryView topiaryView) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewChanged(topiaryView);
            }
        }

    }

    private ParseTopiary parseTopiary;

    private TopiaryViewListenerList topiaryViewListeners = new TopiaryViewListenerList();


    public TopiaryView() {
        parseTopiary = new ParseTopiary();
        installSkin(TopiaryView.class);
    }

    /**
     * Returns the image view's current image.
     *
     * @return
     * The current image, or <tt>null</tt> if no image is set.
     */
    public ParseTopiary getParseTopiary() {
        return parseTopiary;
    }


    @Override
    public void clear() {
    	/*
    	 * TODO: Implement this
    	 */
    }

    /**
     * Returns the image view listener list.
     */
    public ListenerList<TopiaryViewListener> getTopiaryViewListeners() {
        return topiaryViewListeners;
    }

}
