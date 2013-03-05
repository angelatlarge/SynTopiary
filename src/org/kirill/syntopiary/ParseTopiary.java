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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

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
    
	
	/** 
	 * Root node of the tree
	 */
	protected ParseTopiaryNode root;
	/** 
	 * String specification for the tree
	 */
	protected String parseString;
	/** 
	 * Mapping of node names to nodes themselves. 
	 */
	protected HashMap<String, ParseTopiaryNode> nameMapping = null;
	
	/** 
	 * List of connections between nodes
	 */
	protected ArrayList<ParseTopiaryConnection> connections = null;
	
	protected enum ParseTokenType {
		pttTEXT, pttOPTIONS
	};

	public class ParseTopiaryConnection {
		protected String targetName = null;
		protected ParseTopiaryNode sourceNode = null;
		protected ParseTopiaryNode targetNode = null;
		
		protected ParseTopiaryConnection(ParseTopiaryNode src, String name) {
			assert(src!=null);
			sourceNode = src;
			
			assert(name!=null);
			targetName = name;
		}
		public ParseTopiaryNode getTargetNode() {
			return targetNode;
		}
		public ParseTopiaryNode getSourceNode() {
			return sourceNode;
		}
	}

	
	public class ParseTopiaryNode {
		
		protected String text;
		protected ArrayList<ParseTopiaryNode> children = new ArrayList<ParseTopiaryNode>(); 
		protected ArrayList<String> names = new ArrayList<String>();
		protected ArrayList<ParseTopiaryConnection> targets = new ArrayList<ParseTopiaryConnection>();
		protected ParseTopiaryNode parent = null;
		protected int level = -1; 		// Level of the node, will be memoized;
		protected int isMultiWord = -1;	// Is this a multi-word node, will be memoized
		protected boolean hatRequested = false;

		/* Node parser clas
		 * 
		 */
		class NodeParser {
			StringBuilder nodeText = new StringBuilder();
			String optionName = null;
			int idxTokenEnd = 0;
			StringBuilder src = null;
			StringBuilder strText = null;
			String strOptionName = null;
			
			class ParseStackEntry {
				public ParseTokenType parse;
				ParseStackEntry(ParseTokenType p) {
					parse =p;
				}
			};
			Stack<ParseStackEntry> stackParse = new Stack<ParseStackEntry>();

			String extractCurrentToken(int start, int end) {
				assert(start<src.length()) : String.format("Start index %d out of range in string \"%s\"", start, src);
				assert(start+end<=src.length()) : String.format("End index %d out of range in string \"%s\"", end, src);
				String result = src.substring(start, end); 
				src.delete(start, end);
				return result;
			}
			
			// Modifies ALL data members: this function exists to avoid code dupliction
			// a nested function would be better, if only it were possible
			void parseEndCategory() { 
				if (idxTokenEnd==0)
					return;
				String strNewToken = extractCurrentToken(0, idxTokenEnd).trim();
				switch (stackParse.peek().parse) {
				case pttTEXT:
					strText.append(strNewToken);
					break;
				case pttOPTIONS:
					if (strOptionName != null) {
						if (strOptionName.equalsIgnoreCase("name")) {
							if (nameMapping.containsKey(strNewToken)) {
								// TODO: log the error
							} else {
								if (strNewToken.isEmpty()) {
									// TODO: log the error
								} else {
									names.add(strNewToken);
									nameMapping.put(strNewToken, ParseTopiaryNode.this);
								}
							}
						} else if (strOptionName.equalsIgnoreCase("target")) {
							if (strNewToken.isEmpty()) {
								// TODO: log the error
							} else {
								ParseTopiaryConnection conn = new ParseTopiaryConnection(ParseTopiaryNode.this, strNewToken); 
								targets.add(conn);
								connections.add(conn);
							}
						}
					} else {
						// Name is null, the token has the entire option
						if (strNewToken.equalsIgnoreCase("hat")) {
							// Requesting a hat
							hatRequested = true;
						} else {
							// Unknown option specification
							// TODO: Log the error
						}
					}
					strOptionName = null;
					break;
				default:
					assert(false);
				}
				idxTokenEnd = 0;
			}
			
			NodeParser(StringBuilder s) {
				src = s;
				strText = new StringBuilder();
				strOptionName = null;
				stackParse.push(new ParseStackEntry(ParseTokenType.pttTEXT)); // By default start with parsing text
				
				while (idxTokenEnd<src.length()) {
					String ch = src.substring(idxTokenEnd, idxTokenEnd+1);
					if (ch.equals("\\")) {	// Escape sequences apply to everything
						src.delete(idxTokenEnd, idxTokenEnd+1);
						idxTokenEnd++;
					} else if ((stackParse.peek().parse == ParseTokenType.pttTEXT) && ch.equals("(")) {
							// Begin children
							parseEndCategory();
							src.delete(0, 1);
							boolean bMoreChildren;
							do {
								children.add(new ParseTopiaryNode(src, ParseTopiaryNode.this));
//								System.out.format("Ended a child call. src=\"%s\"\n", src);
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
					} else if ((stackParse.peek().parse == ParseTokenType.pttTEXT) && ch.equals(")")) {
						// End this node
						/* If we are a child that is being ended by a parenthesis, 
						 * we want to leave the parenthesis to the parent, 
						 * so that further text will not be our sibling */
						break;
					} else if ((stackParse.peek().parse == ParseTokenType.pttTEXT) && ch.equals(",")) {
						// End this node
						break;
					} else if ((stackParse.peek().parse == ParseTokenType.pttTEXT) && ch.equals("[")) {
						// Extract name
						parseEndCategory();
						// Delete opening brace
						src.delete(0, 1);
						// Begin options processing
						stackParse.push(new ParseStackEntry(ParseTokenType.pttOPTIONS));
						strOptionName = null;
					} else if ((stackParse.peek().parse == ParseTokenType.pttOPTIONS) && ch.equals(";")) {
						// End of one option, start of another
						parseEndCategory();
						// Delete the separator
						src.delete(0, 1);
						// Continuing in the options mode, so we don't pop the parse type stack
					} else if ((stackParse.peek().parse == ParseTokenType.pttOPTIONS) && ch.equals(":")) {
						// End of option name
						strOptionName = extractCurrentToken(0, idxTokenEnd).trim();
						idxTokenEnd = 0;
						// Delete the separator
						src.delete(0, 1);
						// Continuing in the options mode, so we don't pop the parse type stack
					} else if ((stackParse.peek().parse == ParseTokenType.pttOPTIONS) && ch.equals("]")) {
						// End options
						parseEndCategory();	// Extracts token, ends it in the right place
						// Delete closing brace
						src.delete(0, 1);
						idxTokenEnd = 0;
						// Pop the parse state
						stackParse.pop();
					} else {
						idxTokenEnd++;
					}
					
				}
				
				parseEndCategory();
				stackParse.pop();
				
				// Sets the node text
				text = strText.toString().trim();
			}
		}
		
		protected ParseTopiaryNode(StringBuilder src, ParseTopiaryNode nodeParent) {
			// We use the node parses to actually do the parsing
			parent = nodeParent;
			new NodeParser(src);
			
		}
		
		public boolean equals(Object other) {
			if (other == null) return false;
			if (other == this) return true;
			return false;
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

		public Iterable<String> names() {
			return names;
		}
		
		public Iterable<ParseTopiaryConnection> targets() {
			return targets;
		}
		
		protected void getNodeTexts(TreeMap<String, LinkedList<ParseTopiaryNode>> map, boolean includeNamedNodes) {
//			System.out.format("names.size()=%d\n",names.size());
			if ( (text!=null) && !text.isEmpty() ) { 
				if (names.size() == 0 || includeNamedNodes ) {
					LinkedList<ParseTopiaryNode> ll = map.get(text);
					if (ll == null) {
						ll = new LinkedList<ParseTopiaryNode>();
						map.put(text, ll);
					}
					ll.add(this);
				}
			}
			for (ParseTopiaryNode c : children) {
				c.getNodeTexts(map, includeNamedNodes);
			}
		}
		
		protected void findNodeTargets() {
//			System.out.format("Finding target nodes\n");
			assert(nameMapping != null);
			
			// Find own target nodes
			for (Iterator<ParseTopiaryConnection> it = targets.iterator(); it.hasNext() ; ) {
				ParseTopiaryConnection conn = it.next(); 
				ParseTopiaryNode node = nameMapping.get(conn.targetName);
				if (node == null) {
					// TODO: Log this error
//					int nCountBefore = targets.size();
					System.out.format("Unable to find target node with name %s\n" , conn.targetName);
					it.remove();
					// We also need to remove it from the connections list
					connections.remove(conn);
//					assert targets.size() == nCountBefore-1 : "Tried to remove a connection an failed";
//					System.out.format("New targets size: %d\n", targets.size());
				} else {
//					System.out.format("Found target correctly\n");
					conn.targetNode = node;
				}
			}
			
			// Find children target nodes
			for (ParseTopiaryNode c : children) {
				c.findNodeTargets();
			}
		}

		/**
		 * Returns the parent of this node
		 */
		public ParseTopiaryNode getParent() {
			return parent;
		}
		
		/**
		 * Returns the level of the node.  Root has level zero
		 */
		public int getLevel() {
			if (level == -1) { 
				if (parent == null) {
					level = 0;
				} else {
					level = parent.getLevel()+1;
				}
			}
			return level;
		}

		/**
		 * Returns true if this node has the other node as an ancestor
		 */
		public boolean hasAsAncestor (ParseTopiaryNode other) {
			if (other==this) return false; 	// Nodes are not considered their own parents
			ParseTopiaryNode ancestor = parent;
			while (ancestor != null) {
				if (ancestor.equals(other))
					return true;
				ancestor = ancestor.parent;
			}
			return false;
		}

		/**
		 * Compares children nodes based on linearity
		 */
		public int compareChildren(ParseTopiaryNode child1, ParseTopiaryNode child2) {
			int i1 = children.indexOf(child1);
			assert(i1 >= 0);
			int i2 = children.indexOf(child2);
			assert(i2 >= 0);
			return i1 - i2; 
		}
		
		/**
		 * Index of the node in the children list
		 */
		public int indexOfChild(ParseTopiaryNode child) {
			return children.indexOf(child);
		}
		
		/**
		 * Compares nodes based on their linearity
		 * Nodes that are the same, or single parents of single children are equal
		 * Nodes to the left of other nodes are "less than" those other nodes
		 * Nodes to the right of other nodes are "greater than" those other nodes
		 */
		public int compareTo(ParseTopiaryNode other) {
			assert(other != null);
			if (other == this) return 0;			// Same nodes are equal
			ParseTopiaryNode nodeParent = null;
			ParseTopiaryNode nodeChild = null;
			if (hasAsAncestor(other)) { 
				nodeParent = other;
				nodeChild = this;
			} else if (other.hasAsAncestor(this)) {
				nodeParent = this;
				nodeChild = other;
			} else {
				// One node is not parented by the other
				int l1 = this.getLevel();
				int l2 = other.getLevel();
				ParseTopiaryNode ancestor1 = this;
				ParseTopiaryNode ancestor2 = other;
				while (l1>l2) { ancestor1 = ancestor1.getParent(); l1--; }
				while (l2>l1) { ancestor2 = ancestor2.getParent(); l2--; }
				/* Now the level of ancestor1 is the same as the level of ancestor2
				 * because "this" and "other" are not in a parent/child relationship
				 * ancestor1 is guaranteed to not be equal to ancestor2 */
				assert(!ancestor1.equals(ancestor2));
				do {
					assert(ancestor1 != null);
					assert(ancestor2 != null);
					if ( ancestor1.getParent() == ancestor2.getParent() ) {
						// Children have the same parent.  Now we can compare them
						return ancestor1.getParent().compareChildren(ancestor1, ancestor2);
					} 
					// Move to the higher parent
					ancestor1 = ancestor1.getParent();
					ancestor2 = ancestor2.getParent();
				} while (true);
			}
			// We are here only if one node is parented by another
			int returnValue = 0;
			ParseTopiaryNode childCurrent = nodeChild;
			ParseTopiaryNode parentCurrent;
			do {
				parentCurrent = childCurrent.getParent();
				int childIndex = parentCurrent.indexOfChild(childCurrent);
				int halfSize = parentCurrent.children.size() / 2;
				int addend = 1 - parentCurrent.children.size() % 2;
				int newRetVal = (childIndex - halfSize) * 2 + addend;	// This works for both even and odd number of children.
				if (nodeParent == this)
					newRetVal = - newRetVal;
				if (newRetVal != 0) {
					returnValue = newRetVal;
				}
				childCurrent = parentCurrent;
			} while (!nodeParent.equals(parentCurrent));
			return returnValue;
		}
		
		public boolean isMultiWord() {
			if (isMultiWord == -1) {
				// Need to compute whether this node is multi-word
				isMultiWord = 0;
				// Search for all whitespace characters
				String strWhitespace = " \t\n\r";
				for (int i=0;i<strWhitespace.length();i++) {
					if (text.indexOf(strWhitespace.charAt(i)) > -1) {
						isMultiWord = 1;
						break;
					}
				}
			}
			return isMultiWord>0;
		}
		
		public boolean getHatRequested() {
			return hatRequested;
		}
		
	} // end of ParseTopiaryNode

	protected void doParse(StringBuilder src) {
		if (src!=null) {
			nameMapping = new HashMap<String, ParseTopiaryNode>();
			connections = new ArrayList<ParseTopiaryConnection>();
			root = new ParseTopiaryNode(src, null);
			assert(root != null);
			
			// Assign default names to nodes
			// 1. Get node texts
			TreeMap<String,  LinkedList<ParseTopiaryNode>> nodeTexts = new TreeMap<String, LinkedList<ParseTopiaryNode>>();
			root.getNodeTexts(nodeTexts, false);
			// 2. Now node texts contains all nodes that do not have a name
			for (String text : nodeTexts.keySet()) {
				
				LinkedList<ParseTopiaryNode> llText = nodeTexts.get(text);
				assert(llText != null);
				ParseTopiaryNode node = llText.getFirst(); 
				if (node == llText.getLast()) {
					// There is only a single element with this text
					// Go ahead and name it after the text
					node.names.add(text);
					nameMapping.put(text, node);
				} else {
					/* More than one element with this name
					 * If the name does not end with a number, then add a running number
					 * If the name DOES end with a number, then add an underscore + number */
					String strNamePrefix_T;
					if (Character.isDigit(text.charAt(text.length()-1))) {
						strNamePrefix_T = String.format("%s_%s", text, "%d");
					} else {
						strNamePrefix_T = String.format("%s%s", text, "%d");
					}
//					System.out.format("Using name prefix %s\n", strNamePrefix_T);
					int idxNode = 1;
					while (!llText.isEmpty()) {
						// Get the node to work on
						node = llText.removeFirst();
						// Assign it a name
						String strName = String.format(strNamePrefix_T, idxNode++); 
						node.names.add(strName);
						// Add the name mapping
						nameMapping.put(strName, node);
					}
				}
			} // Each unique node text loop
			
			// Get nodes to find their target nodes
			root.findNodeTargets();
		} else {
			// Empty tree
			root = null;
			nameMapping = null;
			connections = null;
		}
		
	}
	
	public ParseTopiary(StringBuilder src) {
		parseString = src.toString();
		doParse(src);
	}
	
	protected ParseTopiary() {
		parseString = null;
		root = null;
	}
	
	public ParseTopiary(String src) {
		root = null;
		parseString = null;
		setParseString(src);
	}
	
	public ParseTopiaryNode getRoot() {
		return root;
	}
	
	public ParseTopiaryNode getNodeByName(String nodeName) {
		return nameMapping.get(nodeName);
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
			doParse(s);
		} else {
			doParse(null);
		}
		parseTopiaryListeners.parseTopiaryChanged(this);
	}
	
	public String toString() {
		return root.toString();
	}

	public Iterable<ParseTopiaryConnection> connections() {
		return connections;
	}

	public static void main(String[] args) {
		// Test
		ParseTopiary pt = new ParseTopiary("A(B,C)");
		System.out.format(">%s<", pt.toString());

	}

}
