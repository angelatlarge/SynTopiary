package org.kirill.syntopiary;

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.json.JSON;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.BindType;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.WTKListenerList;
import org.apache.pivot.wtk.media.Image;

/**
 * Component that displays an image.
 */
@DefaultProperty("image")
public class TopiaryView extends Component {
    /**
     * Translates between image and context data during data binding.
     */
    public interface ImageBindMapping {
        /**
         * Defines the supported load type mappings.
         */
        public enum Type {
            IMAGE,
            NAME
        }

        /**
         * Returns the load type supported by this mapping.
         */
        public Type getType();

        /**
         * Converts a value from the bind context to an image representation
         * during a {@link Component#load(Object)} operation.
         *
         * @param value
         */
        public Image toImage(Object value);

        /**
         * Converts a value from the bind context to an image resource name
         * during a {@link Component#load(Object)} operation.
         *
         * @param value
         */
        public String toImageName(Object value);

        /**
         * Converts a text string to a value to be stored in the bind context
         * during a {@link Component#store(Object)} operation.
         *
         * @param image
         */
        public Object valueOf(Image image);
    }

    private static class TopiaryViewListenerList extends WTKListenerList<TopiaryViewListener>
        implements TopiaryViewListener {
        @Override
        public void imageChanged(TopiaryView topiaryView, Image previousImage) {
            for (TopiaryViewListener listener : this) {
                listener.imageChanged(topiaryView, previousImage);
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

        @Override
        public void imageBindMappingChanged(TopiaryView topiaryView,
            TopiaryView.ImageBindMapping previousImageBindMapping) {
            for (TopiaryViewBindingListener listener : this) {
                listener.imageBindMappingChanged(topiaryView, previousImageBindMapping);
            }
        }
    }

    private Image image = null;
    private String imageKey = null;
    private BindType imageBindType = BindType.BOTH;
    private ImageBindMapping imageBindMapping = null;

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
    public TopiaryView(Image image) {
        setImage(image);

        installSkin(TopiaryView.class);
    }

    /**
     * Returns the image view's current image.
     *
     * @return
     * The current image, or <tt>null</tt> if no image is set.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image view's current image.
     *
     * @param image
     * The image to set, or <tt>null</tt> for no image.
     */
    public void setImage(Image image) {
        Image previousImage = this.image;

        if (previousImage != image) {
            this.image = image;
            topiaryViewListeners.imageChanged(this, previousImage);
        }
    }


    /**
     * Returns the image view's image key.
     *
     * @return
     * The image key, or <tt>null</tt> if no key is set.
     */
    public String getImageKey() {
        return imageKey;
    }

    /**
     * Sets the image view's image key.
     *
     * @param imageKey
     * The image key, or <tt>null</tt> to clear the binding.
     */
    public void setImageKey(String imageKey) {
        String previousImageKey = this.imageKey;

        if (previousImageKey != imageKey) {
            this.imageKey = imageKey;
            topiaryViewBindingListeners.imageKeyChanged(this, previousImageKey);
        }
    }

    public BindType getImageBindType() {
        return imageBindType;
    }

    public void setImageBindType(BindType imageBindType) {
        if (imageBindType == null) {
            throw new IllegalArgumentException();
        }

        BindType previousImageBindType = this.imageBindType;

        if (previousImageBindType != imageBindType) {
            this.imageBindType = imageBindType;
            topiaryViewBindingListeners.imageBindTypeChanged(this, previousImageBindType);
        }
    }

    public ImageBindMapping getImageBindMapping() {
        return imageBindMapping;
    }

    public void setImageBindMapping(ImageBindMapping imageBindMapping) {
        ImageBindMapping previousImageBindMapping = this.imageBindMapping;

        if (previousImageBindMapping != imageBindMapping) {
            this.imageBindMapping = imageBindMapping;
            topiaryViewBindingListeners.imageBindMappingChanged(this, previousImageBindMapping);
        }
    }

    @Override
    public void load(Object context) {
        if (imageKey != null
            && JSON.containsKey(context, imageKey)
            && imageBindType != BindType.STORE) {
            Object value = JSON.get(context, imageKey);

            if (imageBindMapping != null) {
                switch (imageBindMapping.getType()) {
                    case IMAGE: {
                        value = imageBindMapping.toImage(value);
                        break;
                    }

                    case NAME: {
                        value = imageBindMapping.toImageName(value);
                        break;
                    }
                }
            }

            if (value == null
                || value instanceof Image) {
                setImage((Image)value);
            } else {
                throw new IllegalArgumentException(getClass().getName() + " can't bind to "
                    + value + ".");
            }
        }
    }

    @Override
    public void store(Object context) {
        if (imageKey != null
            && imageBindType != BindType.LOAD) {
            JSON.put(context, imageKey, (imageBindMapping == null) ?
                image : imageBindMapping.valueOf(image));
        }
    }

    @Override
    public void clear() {
        if (imageKey != null) {
            setImage((Image)null);
        }
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
