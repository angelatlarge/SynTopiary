package org.kirill.syntopiary;

import Board;

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
    
	
	public ParseTopiaryNode root;
	
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
						bMoreChildren = ((src.length()>0) && src.substring(0, 1).equals(","));
						if (bMoreChildren) {
							src.delete(0, 1);
						}
					} while (bMoreChildren);
					
				} else if (ch.equals(")")) {
					// End this node
					break;
				} else {
					idxTokenEnd++;
				}
			}
			strText.append(src.substring(0, idxTokenEnd));
			src.delete(0, idxTokenEnd+1);
			text = strText.toString();
		}
		
		public String toString() {
			StringBuilder strResult = new StringBuilder();
			strResult.append(text);
			if (children.size()>0) {
				strResult.append("(");
				for (int i=0;i<children.size();i++) {
					strResult.append(children.get(i).toString());
					if (i<children.size()-1) {
						strResult.append(",");
					}
					strResult.append(")");
				}
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
		root = null;
	}
	
	public ParseTopiary(StringBuilder src) {
		root = new ParseTopiaryNode(src);
	}
	
	public ParseTopiary(String src) {
		StringBuilder s = new StringBuilder();
		s.append(src);
		root = new ParseTopiaryNode(s);
	}
	
	public ParseTopiaryNode getRoot() {
		return root;
	}
	public String toString() {
		return root.toString();
	}
	
	public static void main(String[] args) {
		// Test
		ParseTopiary pt = new ParseTopiary("ABC\\(XYZ");
		System.out.format(">%s<", pt.toString());

	}

}
