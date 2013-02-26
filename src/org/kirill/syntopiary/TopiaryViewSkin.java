package org.kirill.syntopiary;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.PrintGraphics;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.StringCharacterIterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.skin.ComponentSkin;

import org.kirill.syntopiary.ParseTopiary.ParseTopiaryNode;

public class TopiaryViewSkin extends ComponentSkin implements ParseTopiaryListener {
	
	
    private class SkinNode {
    	TopiaryViewSkin skin;
    	final ParseTopiaryNode parseNode;
        private ArrayList<GlyphVector> glyphVectors = null;
        private float textHeight = 0;
		protected ArrayList<SkinNode> children = new ArrayList<SkinNode>(); 
    	
    	public SkinNode(TopiaryViewSkin skin, ParseTopiaryNode parseTopiaryNode) {
    		parseNode = parseTopiaryNode;
    		for (ParseTopiaryNode n : parseNode.children()) 
    			children.add(new SkinNode(skin, n));
    	}
    	
        public void layout() {
            String text = parseNode.getText();

            glyphVectors = new ArrayList<GlyphVector>();
            textHeight = 0;

            if (text != null) {
                int n = text.length();

                if (n > 0) {
                    FontRenderContext fontRenderContext = Platform.getFontRenderContext();

                    appendLine(text, 0, text.length(), fontRenderContext);
                }
            }
            for (SkinNode n : children) {
            	n.layout();
            }
        }
    	
	    private void appendLine(String text, int start, int end, FontRenderContext fontRenderContext) {
	        StringCharacterIterator line = new StringCharacterIterator(text, start, end, start);
	        GlyphVector glyphVector = skin.getDefaultFont().createGlyphVector(fontRenderContext, line);
	        glyphVectors.add(glyphVector);

	        Rectangle2D textBounds = glyphVector.getLogicalBounds();
	        textHeight += textBounds.getHeight();
	    }
    	
	    public void paint(Graphics2D graphics) {

	        int width = getWidth();
	        int height = getHeight();

	        // Draw the background
	        if (backgroundColor != null) {
	            graphics.setPaint(backgroundColor);
	            graphics.fillRect(0, 0, width, height);
	        }

	        // Draw the text
	        if (glyphVectors != null && glyphVectors.getLength() > 0) {
	            graphics.setFont(skin.getDefaultFont());

                graphics.setPaint(skin.getDefaultColor());

	            FontRenderContext fontRenderContext = Platform.getFontRenderContext();
	            LineMetrics lm = skin.getDefaultFont().getLineMetrics("", fontRenderContext);
	            float ascent = lm.getAscent();
	            float lineHeight = lm.getHeight();

//	            float y = height - (textHeight + padding.bottom);
	            float y = height - textHeight;

	            for (int i = 0, n = glyphVectors.getLength(); i < n; i++) {
	                GlyphVector glyphVector = glyphVectors.get(i);

	                Rectangle2D textBounds = glyphVector.getLogicalBounds();
	                float lineWidth = (float)textBounds.getWidth();

//	                float x = padding.left;
	                float x = 0;

	                if (graphics instanceof PrintGraphics) {
	                    // Work-around for printing problem in applets
	                    String text = parseNode.getText();
	                    if (text != null && text.length() > 0) {
	                        graphics.drawString(text, x, y + ascent);
	                    }
	                }
	                else {
	                    graphics.drawGlyphVector(glyphVector, x, y + ascent);
	                }

	                y += textBounds.getHeight();
	            }
	        }
	    }	    
    }
	
    private Color backgroundColor = null;
    private float opacity = 1.0f;

    private boolean fill = false;

    private Font defaultFont;
    private Color defaultColor;

    protected SkinNode rootSkinNode; 
    
    
    public TopiaryViewSkin() {       
        Theme theme = Theme.getTheme();
    	defaultFont = theme.getFont();
        backgroundColor = null;
        defaultColor = Color.BLACK;
    }

    @Override
    public void install(Component component) {
        super.install(component);

        TopiaryView topiaryView = (TopiaryView)component;
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();
        parseTopiary.getParseTopiaryListeners().add(this);
//        topiaryView.getTopiaryViewListeners().add(this);

    }

    public Font getDefaultFont() {
    	return defaultFont;
    }
    
    public Color getDefaultColor() {
    	return defaultColor;
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
//        TopiaryView topiaryView = (TopiaryView)getComponent();
//        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        int baseline = 300;

        return baseline;
    }

    @Override
    public void layout() {
        buildNodes();
        if (rootSkinNode != null) {
        	rootSkinNode.layout();
        }
    }

    protected void buildNodes() {
    	if (rootSkinNode!=null) 
    		return;
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();
        if (parseTopiary != null) {
        	ParseTopiaryNode rootNode = parseTopiary.getRoot();
        	if (rootNode != null) {
        		rootSkinNode = new SkinNode(this, rootNode);
        	}
        }
    }
    protected void reBuildNodes() {
    	rootSkinNode = null;
    	buildNodes();
    }
    
    @Override
    public void paint(Graphics2D graphics) {

        int width = getWidth();
        int height = getHeight();

        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        if (rootSkinNode != null) {
        	rootSkinNode.paint(graphics);
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

    // events

	@Override
	public void parseTopiaryChanged(ParseTopiary parseTopiary) {
		reBuildNodes();
        invalidateComponent();
	}

}
