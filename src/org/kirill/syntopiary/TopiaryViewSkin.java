package org.kirill.syntopiary;

//import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.PrintGraphics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.StringCharacterIterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.skin.ComponentSkin;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import org.kirill.syntopiary.ParseTopiary.ParseTopiaryNode;

public class TopiaryViewSkin extends ComponentSkin implements ParseTopiaryListener, TopiaryViewListener {
	private float nNodeXMargin = 4.0f;
	private float nNodeYMargin = 2.0f;
	private float minYNodeSpacing = 7.0f;
    private float lineSlope = 0.125f;
	
	
    private class SkinNode {
    	TopiaryViewSkin skin;
    	
    	final ParseTopiaryNode parseNode;
        private ArrayList<GlyphVector> glyphVectors = null;
        private float nodeTextWidth = 0;		// Width of the text of this node only
        private float nodeTextHeight = 0;		// Height of the text of this node only
        
        private float nodeBoxWidth = 0;			// Text + margin of this node only
        private float nodeBoxHeight = 0;		// Text + margin of this node only
        
        private float childrenWidth = 0;		// Width of the children nodes together
        private float childrenHeight = 0;		// Height of the children nodes together
        
        private float fullWidth = 0;			// Width of this node + children
        private float fullHeight = 0;			// Height of this node + children
        
        private float leftPadding = 0;			// Positive value: pad self, negative value = pad children

		private float connectionPointX = 0;
		private float childXSpacing = 0;
		private float connectHeight = 0;
        
		protected java.util.ArrayList<SkinNode> children = new java.util.ArrayList<SkinNode>(); 
    	
    	public SkinNode(TopiaryViewSkin parentSkin, ParseTopiaryNode parseTopiaryNode) {
    		assert(parentSkin != null);
    		skin = parentSkin;
    		parseNode = parseTopiaryNode;
    		for (ParseTopiaryNode n : parseNode.children()) 
    			children.add(new SkinNode(skin, n));
    	}
    	
    	/* SkinNode */
        public void layout() {
        	assert(parseNode!=null);
            String text = parseNode.getText();
//	    	System.out.format("SkinNode.layout(). Node text: %s\n", text);
	    	
            // Lay out the children
            childrenWidth = 0;
            childrenHeight = 0;
            for (SkinNode childNode : children) {
            	childNode.layout();
            	childrenWidth += childNode.fullWidth;
            	childrenHeight = Math.max(childrenHeight, childNode.fullHeight);
            }

            // Lay out self
            glyphVectors = new ArrayList<GlyphVector>();
            nodeTextHeight = 0;
            nodeTextWidth = 0;
            
            int nTextLength = 0;
            if (text != null) {
            	nTextLength = text.length();
                if (nTextLength > 0) {
                    FontRenderContext fontRenderContext = Platform.getFontRenderContext();
                    appendLine(text, 0, text.length(), fontRenderContext);
                }
            }
            nodeBoxWidth = nodeTextWidth + nNodeXMargin * 2;
            nodeBoxHeight = nodeTextHeight;
            if (nTextLength>0) 
            	nodeBoxHeight += nNodeYMargin * 2;
            
			// Calculate the layout w.r.t. children
			if (children.size() > 1) {
				// At least two children
				SkinNode childFirst = children.get(0); 
				SkinNode childLast = children.get(children.size()-1); 
				float connectLeft = childFirst.connectionPointX;
				float connectRight = childrenWidth - childLast.fullWidth + childLast.connectionPointX;
				connectHeight = (connectRight-connectLeft)*lineSlope;
				if (connectHeight < minYNodeSpacing) {
					// Need to space out the children to maintain line slope
					childXSpacing = (minYNodeSpacing / lineSlope) - (connectRight-connectLeft);
					connectRight += childXSpacing;
					childrenWidth += childXSpacing;
					connectHeight = minYNodeSpacing;
				} else {
					// Children are spaced out enough: no extra spacing necessary
					childXSpacing = 0;
				}
				connectionPointX = (connectLeft+connectRight)/2;
//				System.out.format("layout: 2+ children, lconn:%d rconn:%d, cspace: %d, connself:%d\n", (int)connectLeft, (int)connectRight, (int)childXSpacing, (int)connectionPointX);
				leftPadding = (connectionPointX - (nodeBoxWidth/2));
			} else if (children.size() == 1) {
				// One child
				connectHeight = minYNodeSpacing;
				connectionPointX = children.get(0).connectionPointX;
				leftPadding = (connectionPointX - (nodeBoxWidth/2));
			} else {
				// No children
				connectionPointX = nodeBoxWidth/2;
				leftPadding = 0;
				connectHeight = 0;
			}
            fullWidth = 
            	Math.max(
            		(leftPadding>0)?nodeBoxWidth+leftPadding:nodeBoxWidth, 
            		(leftPadding<0)?childrenWidth-leftPadding:childrenWidth);		
            fullHeight = nodeBoxHeight + connectHeight + childrenHeight;
			connectionPointX = ((leftPadding>0)?leftPadding:0)+nodeBoxWidth/2;
            
        }
    	
    	/* SkinNode */
	    private void appendLine(String text, int start, int end, FontRenderContext fontRenderContext) {
	        StringCharacterIterator line = new StringCharacterIterator(text, start, end, start);
	        assert(skin != null);
	        assert(skin.getDefaultFont() != null);
	        GlyphVector glyphVector = skin.getDefaultFont().createGlyphVector(fontRenderContext, line);
	        glyphVectors.add(glyphVector);

	        Rectangle2D textBounds = glyphVector.getLogicalBounds();
	        nodeTextHeight += textBounds.getHeight();
	        nodeTextWidth = (float) Math.max(nodeTextWidth, textBounds.getWidth());
	    }
    	
    	/* SkinNode */
	    @SuppressWarnings("unused")
		public void paint(Graphics2D graphics, float x, float y) {
	        TopiaryView topiaryView = (TopiaryView)getComponent();
        	assert(parseNode!=null);
            String text = parseNode.getText();
//	    	System.out.format("SkinNode.paint(). Node text: %s\n", text);

//	        int width = getWidth();
//	        int height = getHeight();

            
            if (topiaryView.getDrawFullBoundaries()) {
            	final BasicStroke strokeBox = new BasicStroke(1.0f);
            	graphics.setStroke(strokeBox);
            	graphics.setColor(Color.BLUE);
            	graphics.drawRect(
            			(int)x, 
            			(int)y, 
            			(int)(fullWidth), 
            			(int)fullHeight);
            }
            if (topiaryView.getDrawNodeBoundaries()) {
            	final BasicStroke strokeBox = new BasicStroke(1.0f);
            	graphics.setStroke(strokeBox);
            	graphics.setColor(Color.GREEN);
            	graphics.drawRect((int)(x + ((leftPadding>0)?leftPadding:0) ), (int)y, (int)(nodeBoxWidth), (int)nodeBoxHeight);
            }
            if (topiaryView.getDrawTextBoundaries()) {
            	final BasicStroke strokeBox = new BasicStroke(1.0f);
            	graphics.setStroke(strokeBox);
            	graphics.setColor(Color.RED);
            	graphics.drawRect((int)(x + nNodeXMargin + ((leftPadding>0)?leftPadding:0)), (int)(y + nNodeYMargin), (int)(nodeTextWidth), (int)nodeTextHeight);
            }
            if (false) {
            	// Draw connection point Xs
            	graphics.drawLine((int)(connectionPointX+x)-1, (int)(y)-1, (int)(connectionPointX+x)+1, (int)(y)+1);
            	graphics.drawLine((int)(connectionPointX+x)-1, (int)(y)+1, (int)(connectionPointX+x)+1, (int)(y)-1);
        		graphics.drawLine((int)(connectionPointX+x)-1, (int)(y+nodeBoxHeight)-1, (int)(connectionPointX+x)+1, (int)(y+nodeBoxHeight)+1);
        		graphics.drawLine((int)(connectionPointX+x)-1, (int)(y+nodeBoxHeight)+1, (int)(connectionPointX+x)+1, (int)(y+nodeBoxHeight)-1);
            }
            
	        // Draw the text
	        if (glyphVectors != null && glyphVectors.getLength() > 0) {
	            graphics.setFont(skin.getDefaultFont());

                graphics.setPaint(skin.getDefaultColor());

	            FontRenderContext fontRenderContext = Platform.getFontRenderContext();
	            LineMetrics lm = skin.getDefaultFont().getLineMetrics("", fontRenderContext);
	            float ascent = lm.getAscent();
	            @SuppressWarnings("unused")
				float lineHeight = lm.getHeight();

//	            float y = height - (textHeight + padding.bottom);
//	            float y = height - textHeight;

	            float nLineY = y+nNodeYMargin;
	            for (int i = 0, n = glyphVectors.getLength(); i < n; i++) {
	                GlyphVector glyphVector = glyphVectors.get(i);

	                Rectangle2D textBounds = glyphVector.getLogicalBounds();
	                @SuppressWarnings("unused")
					float lineWidth = (float)textBounds.getWidth();

//	                float x = padding.left;
//	                float x = 0;

	                if (graphics instanceof PrintGraphics) {
	                    // Work-around for printing problem in applets
	                    if (text != null && text.length() > 0) {
	                        graphics.drawString(text, x + nNodeXMargin + ((leftPadding>0)?leftPadding:0), nLineY + ascent);
	                    }
	                }
	                else {
	                	graphics.drawGlyphVector(glyphVector, x + nNodeXMargin + ((leftPadding>0)?leftPadding:0), nLineY + ascent);
	                	if (false) {
	                		graphics.drawString(String.format("%d", (int)connectionPointX), x + nNodeXMargin + ((leftPadding>0)?leftPadding:0) + nodeTextWidth, nLineY + ascent);
	                	}
	                }

	                nLineY += textBounds.getHeight();
	            }
	            
	        }
	        
            // Paint out the children
	        float nChildStartX = x + ((leftPadding<0)?-leftPadding:0);
			float nChildrenStartY = y+nodeBoxHeight + connectHeight;
            for (SkinNode childNode : children) {
            	// Paint the connection
            	if (true) {
            		graphics.drawLine((int)(connectionPointX+x), (int)(y+nodeBoxHeight), (int)(nChildStartX+childNode.connectionPointX), (int)nChildrenStartY);
            	}
				
				// Paint the child
            	childNode.paint(graphics, nChildStartX, nChildrenStartY);
            	nChildStartX += childNode.fullWidth + ((children.size()>1)?childXSpacing/(children.size()-1):0);
            }
	    }	    
    } // End of SkinNode 
	
    private Color backgroundColor = null;
    @SuppressWarnings("unused")
	private float opacity = 1.0f;

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
        topiaryView.getTopiaryViewListeners().add(this);
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
    	System.out.print("getPreferredWidth\n");
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        return (parseTopiary == null) ? 0 : 300;
    }

    @Override
    public int getPreferredHeight(int width) {
    	System.out.print("getPreferredHeight\n");
        TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        return (parseTopiary == null) ? 0 : 300;
    }

    @Override
    public Dimensions getPreferredSize() {
    	System.out.print("getPreferredSize\n");
    	TopiaryView topiaryView = (TopiaryView)getComponent();
        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

        assert(parseTopiary != null);
        return new Dimensions(300, 300);
//        return (parseTopiary == null) ? new Dimensions(0, 0) : new Dimensions(300, 300);
    }

    @Override
    public int getBaseline(int width, int height) {
    	System.out.print("getBaseline\n");
//        TopiaryView topiaryView = (TopiaryView)getComponent();
//        ParseTopiary parseTopiary = topiaryView.getParseTopiary();

//    	int baseline = 300;
        int baseline = 0;

        return baseline;
    }

    @Override
    public void layout() {
//    	System.out.print("TopiaryViewSkin.layout\n");
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
//    	System.out.print("Painting a TopiaryViewSkin\n");

        int width = getWidth();
        int height = getHeight();

        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        if (rootSkinNode != null) {
        	// TODO:Take care of padding here 
        	rootSkinNode.paint(graphics, 0, 0);
        }
    }

    @Override
    public boolean isFocusable() {
//    	System.out.print("isFocusable\n");
        return false;
    }

    @Override
    public boolean isOpaque() {
//    	System.out.print("isOpaque\n");
    	return true;
//        return (backgroundColor != null
//            && backgroundColor.getTransparency() == Transparency.OPAQUE);
    }

    public Color getBackgroundColor() {
//    	System.out.print("getBackgroundColor\n");
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaintComponent();
    }

    public final void setBackgroundColor(String backgroundColor) {
        if (backgroundColor == null) {
            throw new IllegalArgumentException("backgroundColor is null.");
        }

        setBackgroundColor(GraphicsUtilities.decodeColor(backgroundColor));
    }

    public float getOpacity() {
    	System.out.print("getOpacity\n");
    	return 1.0f;
//        return opacity;
    }

    public void setOpacity(float opacity) {
    	System.out.print("setOpacity\n");
        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Opacity out of range [0,1].");
        }

        this.opacity = opacity;
        repaintComponent();
    }

    public final void setOpacity(Number opacity) {
    	System.out.print("setOpacity\n");
        if (opacity == null) {
            throw new IllegalArgumentException("opacity is null.");
        }

        setOpacity(opacity.floatValue());
    }

    // events

	@Override
	public void parseTopiaryChanged(ParseTopiary parseTopiary) {
//		System.out.format("Received change notification: new string is %s...\n", parseTopiary.getParseString());
		reBuildNodes();
        invalidateComponent();
	}
    public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
//    	System.out.print("TopiaryViewSkin.topiaryViewCosmeticOptionsChanged()\n");
        invalidateComponent();
    }
    
	public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
//    	System.out.print("TopiaryViewSkin.topiaryViewLayoutOptionsChanged()\n");
        invalidateComponent();
	}
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
    	// TODO: Write this
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
    	
        // Paint onto the generator
        this.paint(svgGenerator);
        
        // Save to file
        FileOutputStream streamOut;
    	try {
			streamOut = new FileOutputStream(file);
	        Writer writerOut = new OutputStreamWriter(streamOut, "UTF-8");
	        boolean useCSS = true; // we want to use CSS style attributes
	        svgGenerator.stream(writerOut, useCSS);        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
