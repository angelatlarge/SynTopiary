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

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class TopiarySelection implements Transferable, ClipboardOwner {
	DataFlavor[] dfs;
	Image picture;
	InputStream data;

	public TopiarySelection(InputStream s) {
		dfs = new DataFlavor[2];
		dfs[0] = new DataFlavor("image/svg+xml", "SVG");
		try {
			dfs[1] = new DataFlavor (Class.forName ("java.awt.Image"), "Image");
		} catch (ClassNotFoundException e) {
			dfs[1] = null;
			e.printStackTrace();
		}
		data = s;
	}
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable transferable) {
		// TODO Auto-generated method stub
		System.out.print("lost ownership\n");
	}

	@SuppressWarnings("unused")
	@Override
	public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(df)) 
			throw new UnsupportedFlavorException(df);
		
		if (false) {
		    java.util.Scanner s = new java.util.Scanner(data).useDelimiter("\\A");
		    String streamData = s.hasNext() ? s.next() : "";		
			System.out.format("returning SVG data. Data: %s\n", streamData);
			s.close();
			data.reset();
		} else {
			System.out.format("returning SVG data\n");
		}
		return data;
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		System.out.print("getTransferDataFlavors\n");
		return dfs;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor df) {
		System.out.format("isDataFlavorSupported(). Mime type: %s, primary type %s.", df.getMimeType(), df.getPrimaryType());
		assert(dfs != null);
		for (int i=0;i<dfs.length;i++) {
			if (dfs[i] != null) 
				if (dfs[i].equals(df)) {
					System.out.format("Returning true\n");
					return true;
				}
		}
		System.out.format("Returning false\n");
		return false;
	}

}
