package org.kirill.syntopiary;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Transparency;

import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.skin.ComponentSkin;

public class TopiaryViewSkin extends ComponentSkin implements ParseTopiaryListener {
    private Color backgroundColor = null;
    private float opacity = 1.0f;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private boolean fill = false;
    private boolean preserveAspectRatio = true;

    private int imageX = 0;
    private int imageY = 0;
    private float scaleX = 1;
    private float scaleY = 1;

    @Override
    public void install(Component component) {
        super.install(component);

        TopiaryView topiaryView = (TopiaryView)component;
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();
        parseTopiary.getParseTopiaryListeners().add(this);
//        topiaryView.getTopiaryViewListeners().add(this);

    }

    @Override
    public int getPreferredWidth(int height) {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        return (parseTopiary == null) ? 0 : 300;
    }

    @Override
    public int getPreferredHeight(int width) {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        return (parseTopiary == null) ? 0 : 300;
    }

    @Override
    public Dimensions getPreferredSize() {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        return (parseTopiary == null) ? new Dimensions(0, 0) : new Dimensions(300, 300);
    }

    @Override
    public int getBaseline(int width, int height) {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        int baseline = 300;

        return baseline;
    }

    @Override
    public void layout() {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        if (parseTopiary != null) {
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        int width = getWidth();
        int height = getHeight();

        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        if (parseTopiary != null) {
            Graphics2D imageGraphics = (Graphics2D)graphics.create();
            imageGraphics.translate(imageX, imageY);
            imageGraphics.scale(scaleX, scaleY);

            // Apply an alpha composite if the opacity value is less than
            // the current alpha
            float alpha = 1.0f;

            Composite composite = imageGraphics.getComposite();
            if (composite instanceof AlphaComposite) {
                AlphaComposite alphaComposite = (AlphaComposite)composite;
                alpha = alphaComposite.getAlpha();
            }

            if (opacity < alpha) {
                imageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            }

            /*
             * TODO: paint here
             */
            imageGraphics.dispose();
        }
    }

    /**
     * @return
     * <tt>false</tt>; image views are not focusable.
     */
    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return (backgroundColor != null
            && backgroundColor.getTransparency() == Transparency.OPAQUE);
    }

    /**
     * Returns the color that is painted behind the image
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the color that is painted behind the image
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaintComponent();
    }

    /**
     * Sets the color that is painted behind the image
     * @param backgroundColor Any of the
     * {@linkplain GraphicsUtilities#decodeColor color values recognized by Pivot}.
     */
    public final void setBackgroundColor(String backgroundColor) {
        if (backgroundColor == null) {
            throw new IllegalArgumentException("backgroundColor is null.");
        }

        setBackgroundColor(GraphicsUtilities.decodeColor(backgroundColor));
    }

    /**
     * Returns the opacity of the image, in [0,1].
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Sets the opacity of the image.
     * @param opacity A number between 0 (transparent) and 1 (opaque)
     */
    public void setOpacity(float opacity) {
        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Opacity out of range [0,1].");
        }

        this.opacity = opacity;
        repaintComponent();
    }

    /**
     * Sets the opacity of the image.
     * @param opacity A number between 0 (transparent) and 1 (opaque)
     */
    public final void setOpacity(Number opacity) {
        if (opacity == null) {
            throw new IllegalArgumentException("opacity is null.");
        }

        setOpacity(opacity.floatValue());
    }

    /**
     * Returns the horizontal alignment of the image.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment of the image.
     * Ignored if the <code>fill</code> style is true.
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException("horizontalAlignment is null.");
        }

        this.horizontalAlignment = horizontalAlignment;
        layout();
        repaintComponent();
    }

    /**
     * Returns the vertical alignment of the image.
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment of the image.
     * Ignored if the <code>fill</code> style is true.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException("verticalAlignment is null.");
        }

        this.verticalAlignment = verticalAlignment;
        layout();
        repaintComponent();
    }

    /**
     * Returns a boolean indicating whether the image will be scaled to fit
     * the space in which it is placed.
     */
    public boolean getFill() {
        return fill;
    }

    /**
     * Sets a boolean indicating whether the image will be scaled to fit
     * the space in which it is placed.  Note that for scaling to occur,
     * the TopiaryView must specify a preferred size or be placed
     * in a container that constrains its size.
     */
    public void setFill(boolean fill) {
        this.fill = fill;
        layout();
        repaintComponent();
    }

    /**
     * Returns a boolean indicating whether, when the image is scaled,
     * its aspect ratio is preserved.
     */
    public boolean getPreserveAspectRatio() {
        return preserveAspectRatio;
    }

    /**
     * Sets a boolean indicating whether, when the image is scaled,
     * its aspect ratio is preserved.
     * Ignored if the <code>fill</code> style is false.
     */
    public void setPreserveAspectRatio(boolean preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
        layout();
        repaintComponent();
    }

    // events

	@Override
	public void parseTopiaryChanged(ParseTopiary parseTopiary) {
        invalidateComponent();
	}

}
