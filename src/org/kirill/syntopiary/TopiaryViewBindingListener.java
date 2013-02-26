package org.kirill.syntopiary;

import org.apache.pivot.wtk.BindType;

/**
 * Image view binding listener.
 */
public interface TopiaryViewBindingListener {
    /**
     * Image view binding listener adapter.
     */
    public static class Adapter implements TopiaryViewBindingListener {
        @Override
        public void imageKeyChanged(TopiaryView topiaryView, String previousImageKey) {
            // empty block
        }

        @Override
        public void imageBindTypeChanged(TopiaryView topiaryView,
            BindType previousImageBindType) {
            // empty block
        }

    }

    /**
     * Called when an image view's image key has changed.
     *
     * @param topiaryView
     * @param previousImageKey
     */
    public void imageKeyChanged(TopiaryView topiaryView, String previousImageKey);

    /**
     * Called when a image views's image bind type has changed.
     *
     * @param topiaryView
     * @param previousImageBindType
     */
    public void imageBindTypeChanged(TopiaryView topiaryView,
        BindType previousImageBindType);

}
