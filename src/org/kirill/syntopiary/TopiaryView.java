package org.kirill.syntopiary;

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.json.JSON;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.BindType;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.WTKListenerList;
import org.apache.pivot.wtk.media.Image;

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
        public void treeChanged(TopiaryView topiaryView, ParseTopiary previousTree) {
            for (TopiaryViewListener listener : this) {
                listener.treeChanged(topiaryView, previousTree);
            }
        }

        @Override
        public void asynchronousChanged(TopiaryView topiaryView) {
            for (TopiaryViewListener listener : this) {
                listener.asynchronousChanged(topiaryView);
            }
        }
    }

    private static class TopiaryViewBindingListenerList extends WTKListenerList<TopiaryViewBindingListener>
        implements TopiaryViewBindingListener {
        @Override
        public void imageKeyChanged(TopiaryView topiaryView, String previousImageKey) {
            for (TopiaryViewBindingListener listener : this) {
                listener.imageKeyChanged(topiaryView, previousImageKey);
            }
        }

        @Override
        public void imageBindTypeChanged(TopiaryView topiaryView,
            BindType previousImageBindType) {
            for (TopiaryViewBindingListener listener : this) {
                listener.imageBindTypeChanged(topiaryView, previousImageBindType);
            }
        }

    }

    private ParseTopiary tree = null;
    private String imageKey = null;
    private BindType imageBindType = BindType.BOTH;

    private TopiaryViewListenerList topiaryViewListeners = new TopiaryViewListenerList();
    private TopiaryViewBindingListenerList topiaryViewBindingListeners = new TopiaryViewBindingListenerList();


    /**
     * Creates an empty image view.
     */
    public TopiaryView() {
        this(null);
    }

    /**
     * Creates an image view with the given image.
     *
     * @param image
     * The initial image to set, or <tt>null</tt> for no image.
     */
    public TopiaryView(ParseTopiary tree) {
        setTree(tree);

        installSkin(TopiaryView.class);
    }

    /**
     * Returns the image view's current image.
     *
     * @return
     * The current image, or <tt>null</tt> if no image is set.
     */
    public ParseTopiary getTree() {
        return tree;
    }

    /**
     * Sets the image view's current image.
     *
     * @param image
     * The image to set, or <tt>null</tt> for no image.
     */
    public void setTree(ParseTopiary tree) {
    	ParseTopiary previousTree = this.tree;

        if (previousTree != tree) {
            this.tree = tree;
            topiaryViewListeners.treeChanged(this, previousTree);
        }
    }


    @Override
    public void clear() {
        setTree((ParseTopiary)null);
    }

    /**
     * Returns the image view listener list.
     */
    public ListenerList<TopiaryViewListener> getTopiaryViewListeners() {
        return topiaryViewListeners;
    }

    /**
     * Returns the image view binding listener list.
     */
    public ListenerList<TopiaryViewBindingListener> getTopiaryViewBindingListeners() {
        return topiaryViewBindingListeners;
    }
}
