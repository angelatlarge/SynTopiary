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


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pivot.util.ListenerList;

public class ParseTopiary {
	
	// Listeneres
    protected static class ParseTopiaryListenerList extends ListenerList<ParseTopiaryListener>
    implements ParseTopiaryListener {
        public void parseTopiaryChanged(ParseTopiary parseTopiary) {
	        for (ParseTopiaryListener listener : this) {
	            listener.parseTopiaryChanged(parseTopiary);
	        }
	    }
    }
    protected ParseTopiaryListenerList parseTopiaryListeners = new ParseTopiaryListenerList();
    
    public ListenerList<ParseTopiaryListener> getParseTopiaryListeners() {
        return parseTopiaryListeners;
    }
    
	
	protected ParseTopiaryNode root;
	protected String parseString;
	
	public class ParseTopiaryNode {
		protected String text;
		protected ArrayList<ParseTopiaryNode> children = new ArrayList<ParseTopiaryNode>(); 
		
		protected ParseTopiaryNode(StringBuilder src) {
			StringBuilder strText = new StringBuilder();
			int idxTokenEnd = 0;
			while (idxTokenEnd<src.length()) {
				String ch = src.substring(idxTokenEnd, idxTokenEnd+1);
				if (ch.equals("\\")) {
					src.delete(idxTokenEnd, idxTokenEnd+1);
					idxTokenEnd++;
//				} else if (ch.equals("[")) {
//					// Begin options
//				} else if (ch.equals("]")) {
//					// End options
				} else if (ch.equals("(")) {
					// Begin children
					strText.append(src.substring(0, idxTokenEnd));
					src.delete(0, idxTokenEnd+1);
					idxTokenEnd = 0;
					boolean bMoreChildren;
					do {
						children.add(new ParseTopiaryNode(src));
//						System.out.format("Ended a child call. src=\"%s\"\n", src);
						bMoreChildren = false;
						if (src.length()>0) {
							if (src.substring(0, 1).equals(",")) {
								bMoreChildren = true;
								src.delete(0, 1);
							} else { 
								if (src.substring(0, 1).equals(")")) {
									src.delete(0, 1);
								}
							}
						}
					} while (bMoreChildren);
				} else if (ch.equals(")")) { 
					// End this node
					/* If we are a child that is being ended by a parenthesis, 
					 * we want to leave the parenthesis to the parent, 
					 * so that further text will not be our sibling */
					break;
				} else if (ch.equals(",")) {
					// End this node
					break;
				} else {
					idxTokenEnd++;
				}
			}
			strText.append(src.substring(0, idxTokenEnd));
			src.delete(0, idxTokenEnd);
			text = strText.toString().trim();
		}
		
		public String toString() {
			StringBuilder strResult = new StringBuilder();
			strResult.append(text);
			if (children.size()>0) {
				strResult.append("[");
				for (int i=0;i<children.size();i++) {
					strResult.append(children.get(i).toString());
					if (i<children.size()-1) {
						strResult.append(" | ");
					}
				}
				strResult.append("]");
			}
			return strResult.toString();
		}
		
		public String getText() {
			return text;
		}
		
		public Iterable<ParseTopiaryNode> children() {
			return children;
		}
	} // end of ParseTopiaryNode

	public ParseTopiary() {
		parseString = null;
		root = null;
	}
	
	public ParseTopiary(StringBuilder src) {
		parseString = src.toString();
		root = new ParseTopiaryNode(src);
	}
	
	public ParseTopiary(String src) {
		root = null;
		parseString = null;
		setParseString(src);
	}
	
	public ParseTopiaryNode getRoot() {
		return root;
	}
	
	public String getParseString() {
		return parseString;
	}
	
	public void setParseString(String src) {
		if (src.length() == 0) {
			parseString = null;
		} else {
			parseString = src;
		}
		if (parseString != null) {
			StringBuilder s = new StringBuilder();
			s.append(src);
			root = new ParseTopiaryNode(s);
		} else {
			root = null;
		}
//		System.out.format("Notifying listeners of changes: new string is %s...\n", parseString);
		parseTopiaryListeners.parseTopiaryChanged(this);
	}
	
	public String toString() {
		return root.toString();
	}
	
	public static void main(String[] args) {
		// Test
		ParseTopiary pt = new ParseTopiary("A(B,C)");
		System.out.format(">%s<", pt.toString());

	}

}
