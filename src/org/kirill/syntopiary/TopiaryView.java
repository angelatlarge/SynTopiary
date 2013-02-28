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

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.WTKListenerList;

/**
 * Component that displays a tree.
 */
@DefaultProperty("image")
public class TopiaryView extends Component {
	private boolean fDrawTextBoundaries = false;
	private boolean fDrawNodeBoundaries = false;
	private boolean fDrawFullBoundaries = false;

    private ParseTopiary parseTopiary;
	
    private static class TopiaryViewListenerList extends WTKListenerList<TopiaryViewListener>
        implements TopiaryViewListener {
        @Override
        public void topiaryViewCosmeticOptionsChanged(TopiaryView topiaryView) {
        	for (TopiaryViewListener listener : this) {
        		listener.topiaryViewCosmeticOptionsChanged(topiaryView);
        	}
        }
        public void topiaryViewLayoutOptionsChanged(TopiaryView topiaryView) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewLayoutOptionsChanged(topiaryView);
            }
        }
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, File file) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewOutputRequestSVG(topiaryView, file);
            }
        }
        public void topiaryViewOutputRequestSVG(TopiaryView topiaryView, Clipboard clipboard) {
            for (TopiaryViewListener listener : this) {
                listener.topiaryViewOutputRequestSVG(topiaryView, clipboard);
            }
        }
    }
    private TopiaryViewListenerList topiaryViewListeners = new TopiaryViewListenerList();


    public TopiaryView() {
        parseTopiary = new ParseTopiary();
        installSkin(TopiaryView.class);
    }

    public ParseTopiary getParseTopiary() {
        return parseTopiary;
    }

    public ListenerList<TopiaryViewListener> getTopiaryViewListeners() {
        return topiaryViewListeners;
    }

    public boolean getDrawTextBoundaries() { return fDrawTextBoundaries; }
    public void setDrawTextBoundaries(boolean newValue) { 
    	fDrawTextBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public boolean getDrawNodeBoundaries() { return fDrawNodeBoundaries; }
    public void setDrawNodeBoundaries(boolean newValue) { 
    	fDrawNodeBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public boolean getDrawFullBoundaries() { return fDrawFullBoundaries; }
    public void setDrawFullBoundaries(boolean newValue) { 
    	fDrawFullBoundaries = newValue;
    	topiaryViewListeners.topiaryViewCosmeticOptionsChanged(this);
    }
    public void generateSVG( File file ) {
    	topiaryViewListeners.topiaryViewOutputRequestSVG(this, file);
    }
    public void copyAsSVG( Clipboard clipboard ) {
    	topiaryViewListeners.topiaryViewOutputRequestSVG(this, clipboard);
    }
}
