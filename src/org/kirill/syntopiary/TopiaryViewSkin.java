/* Copyright 2013 Kirill Shkovsky
 * 
 * This file is part of SynTopiary.
 * 
 * SynTopiary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SynTopiary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SynTopiary.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Portions of this software are under Apache Software License, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 */
 package org.kirill.syntopiary;

//import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.PrintGraphics;
import java.awt.Shape;
import java.awt.datatransfer.Clipboard;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.StringCharacterIterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.skin.ComponentSkin;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import org.kirill.syntopiary.ParseTopiary.ParseTopiaryConnection;
import org.kirill.syntopiary.ParseTopiary.ParseTopiaryNode;

public class TopiaryViewSkin extends ComponentSkin implements ParseTopiaryListener, TopiaryViewListener {
	private float nNodeXMargin = 4.0f;
	private float nNodeYMargin = 2.0f;
	private float minYNodeSpacing = 7.0f;
	private float lineSlope = 0.125f;
	private float arrowTopMargin = 10f;
	private float arrowHeadWidth = 4.0f;
    private float arrowHeadLength = 5.0f;
	
	/** 
	 * This class implements the graphical side of each tree node
	 */
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
        
        private float leftPadding = 0;			// Padding from the left; positive value: pad self, negative value = pad children

		private float connectionPointX = 0;		// X position of the connection point (above and below)
		private float childXSpacing = 0;		// Extra spacing between children 
												// (to make sure that the connection line have uniform slope 
		private float connectHeight = 0;		// Height of the connection lines to the children below
		
		private float x = Float.NEGATIVE_INFINITY;	// Left side of this component (w.r.t. to the paint canvas)
													// NEGATIVE_INFINITY is a sentinel value indicating that x is not valid 
		private float y = Float.NEGATIVE_INFINITY;	// Top side of this component (w.r.t. to the paint canvas)
													// NEGATIVE_INFINITY is a sentinel value indicating that y is not valid 
		
		/** 
		 * List of children node of this node
		 */	
		protected java.util.ArrayList<SkinNode> children = new java.util.ArrayList<SkinNode>(); 
    	
		/** 
		 * SkinNode.SkinNode(TopiaryViewSkin, parseTopiaryNode)
		 * Basic constructor
		 */	
    	public SkinNode(TopiaryViewSkin parentSkin, ParseTopiaryNode parseTopiaryNode) {
    		assert(parentSkin != null);
    		skin = parentSkin;
    		parseNode = parseTopiaryNode;
    		for (ParseTopiaryNode n : parseNode.children()) 
    			children.add(new SkinNode(skin, n));
    	}
    	
		/** 
		 * SkinNode.layout()
		 * Called to lay out the node on a graphic canvas 
		 */	
        public void layout() {
        	assert(parseNode!=null);
            String text = parseNode.getText();
	    	
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
				if (children.size() % 2 == 0) {
					// Even number of chilren: position between the two extreme connectors
					connectionPointX = (connectLeft+connectRight)/2;
				} else {
					// Odd number of children. Position above the middle child
					float connectMiddle = 0;
					float nCurrentChildrenWidth = 0;
					for (int i=0;i<=(children.size()/2); i++) {
						SkinNode childNode = children.get(i);
	            		connectMiddle = nCurrentChildrenWidth + childNode.connectionPointX;
						nCurrentChildrenWidth += childNode.fullWidth;
	            		nCurrentChildrenWidth += childXSpacing/(children.size()-1);
	            	}
					connectionPointX = connectMiddle;
				}
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
    	
		/** 
		 * SkinNode.appendLine(String, int, int, fontRenderContext)
		 * Called from layout to split the text into lines and 
		 * to store the lines as glyphVector for faster drawing
		 */	
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
    	
	    /** 
	     * SkinNode.setOrigin(float, float)
	     * 
	     * Called to set the origin of this node, after layout is completed
	     * 
	     */
	    protected void setOrigin(float oX, float oY) {
	    	x = oX;
	    	y = oY;
	        float nChildStartX = x + ((leftPadding<0)?-leftPadding:0);
			float nChildrenStartY = y+nodeBoxHeight + connectHeight;
            for (SkinNode childNode : children) {
            	childNode.setOrigin(nChildStartX, nChildrenStartY);
            	nChildStartX += childNode.fullWidth + ((children.size()>1)?childXSpacing/(children.size()-1):0);
            }
	    }
	    
		/** 
		 * SkinNode.paint(graphics, float, float)
		 * 
		 * Called to paint the node onto the canvas
		 * 
		 * @param graphics The canvas onto which we paint the node
		 * Could be a screen or a fake canvas for creating an output file
		 * 
		 */	
		public void paint(Graphics2D graphics) {
	        TopiaryView topiaryView = (TopiaryView)getComponent();
        	assert(parseNode!=null);
        	assert(x != Float.NEGATIVE_INFINITY);
        	assert(y != Float.NEGATIVE_INFINITY);
        	
            String text = parseNode.getText();

            /* The TextBox skin class of Apache Pivot has the following code here
		        int width = getWidth();
		        int height = getHeight();
            */
            
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
            if (topiaryView.getDrawConnectionPoints()) {
            	// Draw connection point Xs
            	graphics.drawLine((int)(connectionPointX+x)-1, (int)(y)-1, (int)(connectionPointX+x)+1, (int)(y)+1);
            	graphics.drawLine((int)(connectionPointX+x)-1, (int)(y)+1, (int)(connectionPointX+x)+1, (int)(y)-1);
        		graphics.drawLine((int)(connectionPointX+x)-1, (int)(y+nodeBoxHeight)-1, (int)(connectionPointX+x)+1, (int)(y+nodeBoxHeight)+1);
        		graphics.drawLine((int)(connectionPointX+x)-1, (int)(y+nodeBoxHeight)+1, (int)(connectionPointX+x)+1, (int)(y+nodeBoxHeight)-1);
            }
            
	        // Draw the text of this node
	        if (glyphVectors != null && glyphVectors.getLength() > 0) {
	            graphics.setFont(skin.getDefaultFont());

                graphics.setPaint(skin.getDefaultColor());

	            FontRenderContext fontRenderContext = Platform.getFontRenderContext();
	            LineMetrics lm = skin.getDefaultFont().getLineMetrics("", fontRenderContext);
	            float ascent = lm.getAscent();
				@SuppressWarnings("unused")
				float lineHeight = lm.getHeight();

	            float nLineY = y+nNodeYMargin;
	            for (int i = 0, n = glyphVectors.getLength(); i < n; i++) {
	                GlyphVector glyphVector = glyphVectors.get(i);

	                Rectangle2D textBounds = glyphVector.getLogicalBounds();
					@SuppressWarnings("unused")
					float lineWidth = (float)textBounds.getWidth();

	                if ((graphics instanceof LEPSGraphics2D) || (graphics instanceof PrintGraphics) ){
	                    // Work-around for printing problem in applets, 
	                	// and LEPSGraphics2D does not know about GlyphVectors
	                    if (text != null && text.length() > 0) {
	                        graphics.drawString(text, x + nNodeXMargin + ((leftPadding>0)?leftPadding:0), nLineY + ascent);
	                    }
	                }
	                else {
	                	graphics.drawGlyphVector(glyphVector, x + nNodeXMargin + ((leftPadding>0)?leftPadding:0), nLineY + ascent);
	                }

	                nLineY += textBounds.getHeight();
	            }
	            
	        }
	        
            // Paint out the children
	        float nChildStartX = x + ((leftPadding<0)?-leftPadding:0);
			float nChildrenStartY = y+nodeBoxHeight + connectHeight;
            for (SkinNode childNode : children) {
            	// Paint the connection
            	graphics.drawLine((int)(connectionPointX+x), (int)(y+nodeBoxHeight), (int)(nChildStartX+childNode.connectionPointX), (int)nChildrenStartY);
				
				// Paint the child
            	childNode.paint(graphics);
            	nChildStartX += childNode.fullWidth + ((children.size()>1)?childXSpacing/(children.size()-1):0);
            }
	    }
	    
		/** 
		 * SkinNode.findNodeForParseNode(ParseTopiaryNode)
		 * 
		 * Called to find a SkinNode for a particular ParseTopiaryNode
		 * 
		 * This function is a bit slow now
		 * 
		 */	
	    SkinNode findNodeForParseNode(ParseTopiaryNode searchNode) {
	    	/* TODO: We could make it faster by creating a hash map of nodes at the skin level,
	    	 * but it probably isn't necessary */
	    	if (parseNode.equals(searchNode)) 
	    		return this;
            for (SkinNode childNode : children) {
            	SkinNode found = childNode.findNodeForParseNode(searchNode);
            	if (found != null) {
            		return found;
            	}
            }
            return null;
	    }
            
		/** 
		 * SkinNode.findNodeForParseNode(ParseTopiaryNode)
		 * 
		 * Called to find a SkinNode for a particular ParseTopiaryNode
		 * 
		 * This function is a bit slow now
		 * 
		 */	
	    SkinNode findLowestNodeBetween(SkinNode A, SkinNode B) {
	    	int compareA = parseNode.compareTo(A.parseNode); 
	    	int compareB = parseNode.compareTo(B.parseNode); 
	    	
	    	boolean thisNodeIsAnAncestor = 
	    			A.parseNode.hasAsAncestor(parseNode) || B.parseNode.hasAsAncestor(parseNode);
	    	
	    	if (!thisNodeIsAnAncestor && (( compareA < 0 ) || ( compareB > 0 ))  ) {
	    		return null;
	    	}
	    	if (children.size()>0) {
	    		// Process chilren
	    		// (we do not need to process self if we have children, 
	    		// the children will always be lower than this node)
	    		SkinNode lowestNode = null;
	    		float nLowY = 0;		// Initialize to pacify Eclipse, though there is no need
	            for (SkinNode childNode : children) {
	            	SkinNode newNode = childNode.findLowestNodeBetween(A, B);
	            	if (newNode != null) {
	            		if (lowestNode==null) {
	            			lowestNode = newNode;
	            			nLowY = newNode.nodeBoxHeight + newNode.y;
	            		} else {
	            			float nNewNodeHeight = newNode.nodeBoxHeight + newNode.y;
	            			if (nNewNodeHeight > nLowY) {
	            				nLowY = nNewNodeHeight;
	            				lowestNode = newNode;
	            			}
	            		}
	            	} // else newNode is null, so don't update lowestNode
	            }
	            return lowestNode;
	    	} else {
	    		if ( (compareA==0) || (compareB==0) ) {
	    			return null;
	    		}
	    		return this;
	    	}
	    }
    } // End of SkinNode 
	
    
	/** 
	 * Class implementing a connection (movement arrow) between two nodes
	 * 
	 */	
    private class SkinConnection {
    	@SuppressWarnings("unused")
		TopiaryViewSkin skin;
    	ParseTopiaryConnection parseConnection = null;
    	SkinNode sourceNode = null;
    	SkinNode targetNode = null;
    	
    	/**
    	 * Two bezier curves for the arrow
    	 */
    	CubicCurve2D curves[] = null;
    	/**
    	 * Polygon for the arrow head
    	 */
    	Shape arrowHead = null;    	
    	/** 
    	 * SkinConnection.SkinConnection(parentSkin, parseTopiaryConnection)
    	 * 
    	 * Basic constructor
    	 * 
    	 */	
    	public SkinConnection(TopiaryViewSkin parentSkin, ParseTopiaryConnection parseTopiaryConnection) {
    		assert(parentSkin != null);
    		skin = parentSkin;
    		parseConnection = parseTopiaryConnection;
    		
    		// Find the source and target nodes
    		sourceNode = rootSkinNode.findNodeForParseNode(parseConnection.getSourceNode());
    		assert(sourceNode != null);
    		targetNode = rootSkinNode.findNodeForParseNode(parseConnection.getTargetNode());
    		assert(targetNode != null);
    	}
    	
    	/** 
    	 * SkinConnection.layout()
    	 * 
    	 * Lays out the geometry of the connection arrow
    	 * 
    	 */	
    	@SuppressWarnings("unused")
		protected void layout() {
    		/*
    		 * Strategy:
    		 * TODO: Resolve connection points (deal with multiple)
    		 * DONE: Find lowest nodes in between and make sure that we are lower
    		 * TODO: What to do about source nodes that have their own children?
    		 * TODO: Deal with multiple arrows in the same horizontal "corridor". How to resolve
    		 * 			* Completely enclosed arrows are higher
    		 * 			* Shorter arrows are higher
    		 * 			* Arrows that start lower stay lower (what is the right concept of "start")
    		 * TODO: EPS export does not support arrow right now
    		 */
    		curves = new CubicCurve2D[2];
    		
    		// Find the lowest node that we might have to deal with
    		SkinNode left;
    		SkinNode right;
    		if (sourceNode.parseNode.compareTo(targetNode.parseNode) < 0) {
    			left = sourceNode; 
    			right = targetNode; 
    		} else {
    			left = targetNode; 
    			right = sourceNode; 
    		}
    		SkinNode middleLowNode = TopiaryViewSkin.this.rootSkinNode.findLowestNodeBetween(left, right);

    		// Compute the lowest curve point
    		float yCurveBottom = Math.max(sourceNode.y + sourceNode.nodeBoxHeight, targetNode.y + targetNode.nodeBoxHeight);
    		if (middleLowNode != null) {
    			yCurveBottom = Math.max(yCurveBottom, middleLowNode.nodeBoxHeight + middleLowNode.y);
    		}
    		yCurveBottom += arrowTopMargin;
    		
    		// Compute the midpoint of the curve
    		float xCurveCenter =
    				(
    					  (sourceNode.x + sourceNode.connectionPointX) 
    					+ (targetNode.x + targetNode.connectionPointX)
    				) / 2;
    		for (int i=0;i<2;i++) {
    			SkinNode n = (i==0)?sourceNode:targetNode;
	    		curves[i] = new CubicCurve2D.Double(
	    				n.x + n.connectionPointX, n.y + n.nodeBoxHeight, 
	    				n.x + n.connectionPointX, yCurveBottom, 
	    				n.x + n.connectionPointX, yCurveBottom, 
	    				xCurveCenter, yCurveBottom); 
    		}
    		// Polygon for the arrow head
        	float xAH1 = targetNode.x + targetNode.connectionPointX; 
        	float yAH1 = targetNode.y + targetNode.nodeBoxHeight; 
    		if (false) {
	        	arrowHead = new Polygon();
				((Polygon)arrowHead).addPoint(Math.round(xAH1), Math.round(yAH1));
				((Polygon)arrowHead).addPoint(Math.round(xAH1-(arrowHeadWidth/2)), Math.round(yAH1+arrowHeadLength));
				((Polygon)arrowHead).addPoint(Math.round(xAH1+(arrowHeadWidth/2)), Math.round(yAH1+arrowHeadLength));
				((Polygon)arrowHead).addPoint(Math.round(xAH1), Math.round(yAH1));
    		} else {
	        	arrowHead = new Path2D.Float();
	        	((Path2D)arrowHead).moveTo(xAH1, yAH1);
	        	((Path2D)arrowHead).lineTo(xAH1-arrowHeadWidth/2, yAH1+arrowHeadLength);
	        	((Path2D)arrowHead).lineTo(xAH1+arrowHeadWidth/2, yAH1+arrowHeadLength);
//	        	((Path2D)arrowHead).lineTo(xAH1, yAH1);
	        	((Path2D)arrowHead).closePath();
    		}
    	}
    	
		/** 
		 * SkinConnection.paint(graphics, int, int)
		 * 
		 * Called to paint the node onto the canvas
		 * 
		 * @param graphics The canvas onto which we paint the node
		 * Could be a screen or a fake canvas for creating an output file
		 * 
		 */	
    	protected void paint(Graphics2D graphics) {
    		// TODO: Paint arrows
    		assert(curves != null);
    		for (CubicCurve2D curve : curves) {
    			assert(curve != null);
    			graphics.draw(curve);
    		}
    		assert(arrowHead != null);
    		graphics.setPaint(Color.black);
    		graphics.fill(arrowHead);
    	}
    	
    }; // SkinConnection
    
    private Color backgroundColor = null;

    private Font defaultFont;
    private Color defaultColor;

    protected SkinNode rootSkinNode = null;
    protected ArrayList<SkinConnection> connections = null;    
    
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
        buildNodes();
        if (rootSkinNode != null) {
        	// Lay out the nodes
        	rootSkinNode.layout();
        	// Position the nodes
        	// TODO:Take care of padding here 
        	rootSkinNode.setOrigin(0, 0);
        	// Lay out and position the connections
        	for (SkinConnection conn : connections) {
        		conn.layout();
        	}
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
        		connections = new ArrayList<SkinConnection>();
            	for (ParseTopiaryConnection conn : parseTopiary.connections()) {
            		connections.add(new SkinConnection(this, conn));
            	}
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
        	for (SkinConnection conn : connections) {
        		conn.paint(graphics);
        	}
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
	
	/**
	 * This class exists to make is possible to factor out Graphics2D streaming code
	 * Because the classes we use declare their serialization methods [like stream()]
	 * independently of any interface, we use this class to encapsulate the streaming
	 */
	abstract class Graphics2DStreamThunk {
		abstract void StreamOut(Writer writer)  throws SVGGraphics2DIOException, IOException;
		abstract Graphics2D getGraphics2D();
	}
	/**
	 * Instance of Graphics2DStreamThunk for Apache's Batik Graphics2D -> SVG library
	 * 
	 */
	class SVGGraphics2DStreamThunk extends Graphics2DStreamThunk {
		protected SVGGraphics2D svgGenerator = null;
		protected boolean useCSS;
		SVGGraphics2DStreamThunk(SVGGraphics2D svgGen, boolean css) {
			svgGenerator = svgGen;
			useCSS = css;
		}
		Graphics2D getGraphics2D() { return svgGenerator; }
		void StreamOut(Writer writer) throws SVGGraphics2DIOException {
			svgGenerator.stream(writer, useCSS);
		}
	}
	/**
	 * Instance of Graphics2DStreamThunk for our own EPS output
	 * 
	 */
	class LEPSGraphics2DStreamThunk extends Graphics2DStreamThunk {
		protected LEPSGraphics2D epsGenerator = null;
		LEPSGraphics2DStreamThunk(LEPSGraphics2D epsGen) {
			epsGenerator = epsGen;
		}
		Graphics2D getGraphics2D() { return epsGenerator; }
		void StreamOut(Writer writer) throws IOException {
			epsGenerator.stream(writer);
		}
	}

	/**
	 * Internal method for serializing the graphics into a file or a memory stream
	 *
	 * @param file Target file. If null, the graphics are serialized onto a memory stream
	 */
	protected OutputStream streamTopiaryGraphics(File file, String filetypeName, Graphics2DStreamThunk thunk) {
        Writer writerOut = null;
        OutputStream streamOut = null;
    	try {
            // Paint onto the generator
            this.paint(thunk.getGraphics2D());

            // Stream out 
            if (file != null) {
            	// File is provided
            	streamOut = new FileOutputStream(file);
            } else {
            	// No file, use ByteArrayOutputStream
            	streamOut = new ByteArrayOutputStream();
            }
	        writerOut = new OutputStreamWriter(streamOut, "UTF-8");
	        thunk.StreamOut(writerOut);
		} catch (Exception e) {
			String msg = String.format("Could not save as %s\nException of type %s\nwith the message\n\"%s\"", filetypeName, e.getClass().toString(), e.getMessage()); 
			System.out.print(msg);
			System.out.print("\n");
			e.printStackTrace();
			Alert.alert(MessageType.INFO, msg, null);
		} finally {
			try {
				if (writerOut != null) writerOut.close();
				if (streamOut != null) streamOut.close();
			} catch (Exception e) {
				// Do nothing
			} finally {
				writerOut = null;
			}
		}
    	
    	return streamOut;
	}
	
	/**
	 * Paint the tree into an SVG file
	 *
	 */
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
    	
        // Paint the graphics and stream out
        streamTopiaryGraphics(file, "SVG file", new SVGGraphics2DStreamThunk(svgGenerator, true));
    }

	/**
	 * Paint the tree into an EPS file
	 *
	 */
    public void topiaryViewOutputRequestEPS(TopiaryView topiaryView, File file) {
        // Create an instance of the SVG Generator.
        LEPSGraphics2D epsGenerator = new LEPSGraphics2D();
        
        streamTopiaryGraphics(file, "EPS file", new LEPSGraphics2DStreamThunk(epsGenerator));
    }
    
	/**
	 * Put SVG representation of the tree onto the system clipboard
	 *
	 */
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard) {
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
    	
        // Paint and stream out
        OutputStream streamOut = streamTopiaryGraphics(null, "SVG", new SVGGraphics2DStreamThunk(svgGenerator, true));
        
        assert(streamOut instanceof ByteArrayOutputStream);		// HUGE cludge!
        ByteArrayOutputStream baStreamOut = (ByteArrayOutputStream)streamOut;
        
        // Get the bytes
        ByteArrayInputStream streamIn = new ByteArrayInputStream(baStreamOut.toByteArray());
        // Generate the selection object
        TopiarySelection topiarySelection = new TopiarySelection(streamIn);
		// Set clipboard contents
        clipboard.setContents(topiarySelection, topiarySelection);
        
    }
    
    

}
