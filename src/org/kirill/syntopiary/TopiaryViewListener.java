package org.kirill.syntopiary;



import org.apache.pivot.wtk.media.Image;

/**
 * Image view listener interface.
 */
public interface TopiaryViewListener {
    /**
     * Tree view listener adapter.
     */
    public static class Adapter implements TopiaryViewListener {
        @Override
        public void imageChanged(TopiaryView topiaryView, Image previousImage) {
            // empty block
        }

        @Override
        public void asynchronousChanged(TopiaryView topiaryView) {
            // empty block
        }
    }

    /**
     * Called when an tree view's image has changed.
     *
     * @param topiaryView
     * @param previousTree
     */
    public void imageChanged(TopiaryView topiaryView, Image previousImage);

    /**
     * Called when an image view's asynchronous flag has changed.
     *
     * @param imageView
     */
    public void asynchronousChanged(TopiaryView topiaryView);
}
