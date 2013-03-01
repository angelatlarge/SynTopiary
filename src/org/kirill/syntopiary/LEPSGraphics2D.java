package org.kirill.syntopiary;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class LEPSGraphics2D extends Graphics2D {
	protected StringBuilder content = new StringBuilder();
	protected final int yTop = 500;

	public void stream(Writer writer) throws IOException {
		// Write the prologue
		writer.write("%!PS\n" + "%%Creator: Adobe Illustrator(TM) 3.2\n"
				+ "%%BoundingBox: 132 369 399 543\n" + "%%PageOrigin:0 0\n"
				+ "%%EndComments\n" + "%%BeginProlog\n" + "%%EndProlog\n"
				+ "%%BeginSetup\n" + "%%EndSetup\n" + "\n"
				+ "/Arial findfont\n" + "11 scalefont\n" + "setfont\n" + "");
		// Write the content
		writer.write(content.toString());
		// Write epilogue
		writer.write("showpage\n" + "%%PageTrailer\n" + "%%Trailer\n"
				+ "%%EOF\n" + "");

	}

	private void doNotImplemented() {
		if (true)
			assert false : "Not implemented";
		else
			throw new java.lang.UnsupportedOperationException("Not implemented");
	}

	@Override
	public void addRenderingHints(Map<?, ?> arg0) {
		doNotImplemented();
	}

	@Override
	public void clip(Shape arg0) {
		doNotImplemented();
	}

	@Override
	public void draw(Shape arg0) {
		doNotImplemented();
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		// TODO: Implement this
	}

	@Override
	public void drawGlyphVector(GlyphVector arg0, float arg1, float arg2) {
		doNotImplemented();
	}

	@Override
	public boolean drawImage(Image arg0, AffineTransform arg1,
			ImageObserver arg2) {
		doNotImplemented();
		return false;
	}

	@Override
	public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2,
			int arg3) {
		doNotImplemented();
	}

	@Override
	public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
		doNotImplemented();
	}

	@Override
	public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
		doNotImplemented();
	}

	@Override
	public void drawString(String s, int x, int y) {
		// TODO: Still working on this
		content.append(String.format("%d %d moveto\n", x, yTop - y));
		content.append(String.format("(%s) show\n", s));
	}

	@Override
	public void drawString(String s, float x, float y) {
		drawString(s, Math.round(x), Math.round(y)); 
	}

	@Override
	public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
		doNotImplemented();
	}

	@Override
	public void drawString(AttributedCharacterIterator arg0, float arg1,
			float arg2) {
		doNotImplemented();
	}

	@Override
	public void fill(Shape arg0) {
		doNotImplemented();
	}

	@Override
	public Color getBackground() {
		doNotImplemented();
		return null;
	}

	@Override
	public Composite getComposite() {
		doNotImplemented();
		return null;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		doNotImplemented();
		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		doNotImplemented();
		return null;
	}

	@Override
	public Paint getPaint() {
		doNotImplemented();
		return null;
	}

	@Override
	public Object getRenderingHint(Key arg0) {
		doNotImplemented();
		return null;
	}

	@Override
	public RenderingHints getRenderingHints() {
		doNotImplemented();
		return null;
	}

	@Override
	public Stroke getStroke() {
		doNotImplemented();
		return null;
	}

	@Override
	public AffineTransform getTransform() {
		doNotImplemented();
		return null;
	}

	@Override
	public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
		doNotImplemented();
		return false;
	}

	@Override
	public void rotate(double arg0) {
		doNotImplemented();
	}

	@Override
	public void rotate(double arg0, double arg1, double arg2) {
		doNotImplemented();
	}

	@Override
	public void scale(double arg0, double arg1) {
		doNotImplemented();
	}

	@Override
	public void setBackground(Color arg0) {
		doNotImplemented();
	}

	@Override
	public void setComposite(Composite arg0) {
		doNotImplemented();
	}

	@Override
	public void setPaint(Paint arg0) {
		// TODO: Write this
	}

	@Override
	public void setRenderingHint(Key arg0, Object arg1) {
		doNotImplemented();
	}

	@Override
	public void setRenderingHints(Map<?, ?> arg0) {
		doNotImplemented();
	}

	@Override
	public void setStroke(Stroke arg0) {
		// TODO: Implement this
	}

	@Override
	public void setTransform(AffineTransform arg0) {
		doNotImplemented();

	}

	@Override
	public void shear(double arg0, double arg1) {
		doNotImplemented();

	}

	@Override
	public void transform(AffineTransform arg0) {
		doNotImplemented();
	}

	@Override
	public void translate(int arg0, int arg1) {
		doNotImplemented();
	}

	@Override
	public void translate(double arg0, double arg1) {
		doNotImplemented();
	}

	@Override
	public void clearRect(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void clipRect(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		doNotImplemented();
	}

	@Override
	public Graphics create() {
		doNotImplemented();
		return null;
	}

	@Override
	public void dispose() {
		doNotImplemented();
	}

	@Override
	public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		doNotImplemented();
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		doNotImplemented();
		return false;
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3,
			ImageObserver arg4) {
		doNotImplemented();
		return false;
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
			int arg4, ImageObserver arg5) {
		doNotImplemented();
		return false;
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
			int arg4, Color arg5, ImageObserver arg6) {
		doNotImplemented();
		return false;
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
		doNotImplemented();
		return false;
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9,
			ImageObserver arg10) {
		doNotImplemented();
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO: Still working on this
		content.append(String.format("%d %d moveto\n", x1, yTop - y1));
		content.append(String.format("%d %d rlineto\n", x2 - x1, -(y2 - y1)));
		content.append("closepath\n");
		content.append("stroke\n");
	}

	@Override
	public void drawOval(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
		doNotImplemented();
	}

	@Override
	public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
		doNotImplemented();
	}

	@Override
	public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		doNotImplemented();
	}

	@Override
	public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		doNotImplemented();
	}

	@Override
	public void fillOval(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
		doNotImplemented();
	}

	@Override
	public void fillRect(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4,
			int arg5) {
		doNotImplemented();
	}

	@Override
	public Shape getClip() {
		doNotImplemented();
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		doNotImplemented();
		return null;
	}

	@Override
	public Color getColor() {
		doNotImplemented();
		return null;
	}

	@Override
	public Font getFont() {
		doNotImplemented();
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font arg0) {
		doNotImplemented();
		return null;
	}

	@Override
	public void setClip(Shape arg0) {
		doNotImplemented();
	}

	@Override
	public void setClip(int arg0, int arg1, int arg2, int arg3) {
		doNotImplemented();
	}

	@Override
	public void setColor(Color arg0) {
		// TODO: Write this
	}

	@Override
	public void setFont(Font arg0) {
		// TODO: Write this
	}

	@Override
	public void setPaintMode() {
		doNotImplemented();
	}

	@Override
	public void setXORMode(Color arg0) {
		doNotImplemented();
	}

}
