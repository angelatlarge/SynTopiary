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

import java.awt.datatransfer.Clipboard;
import java.io.File;

/**
 * ParseTopiary listener interface.
 */
public interface TopiaryViewListener {
	
    public static class Adapter implements TopiaryViewListener {
    	public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
    	}
    	public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
    	}
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
        }
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard) {
        }
    	public void topiaryViewOutputRequestEPS(TopiaryView topiaryView, File file) {
    	}
    }

    /**
     * Changes that affect how things look, but do not affect the layout
     */
    public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView);
    
    /**
     * Changes that affect how nodes are layed out
     */
	public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView);

	/**
	 * Request for SVG file output
	 */
	public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file);
	
    /**
     * Request for SVG file to be placed on the clipboard
     */
    public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard);
    
	/**
	 * Request for EPS file output
	 */
	public void topiaryViewOutputRequestEPS(TopiaryView topiaryView, File file);
	
    
}
