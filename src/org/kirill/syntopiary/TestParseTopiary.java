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

import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.kirill.syntopiary.ParseTopiary.ParseTopiaryNode;


@SuppressWarnings("unused")
public class TestParseTopiary {
	
	@Test protected static void assertPTnode(ParseTopiaryNode n, String text) {
		assertFalse(n == null);
		assertFalse(n.getText() == null);
		assertFalse(n.getText().isEmpty());
		assertTrue(String.format("Expected node text %s, instead got %s", text, n.getText()), n.getText().equals(text));
	}
	
	@Test public static void testBasicParseNodes() {
		System.out.print("Testing basic node parsing...");
		ParseTopiary pt;
		
		// Single node, existence and text
		pt = new ParseTopiary("My node");
		assertPTnode(pt.getRoot(), "My node");
		
		// Single node, trimming and text
		pt = new ParseTopiary(" My node ");
		assertPTnode(pt.getRoot(), "My node");
		
		// Three nodes
		for (int i=0;i<10;i++) {
			String src = "";
			switch (i) {
			case  0: src="Top node(First Child, Second Child"; break;
			case  1: src="Top node(First Child, Second Child)"; break;
			case  2: src="Top node(First Child, Second Child    )"; break;
			case  3: src="Top node(First Child, Second Child)      "; break;
			case  4: src="Top node(First Child, Second Child    )      "; break;
			case  5: src="Top node(First Child     , Second Child    )      "; break;
			case  6: src="   Top node(First Child     , Second Child    )      "; break;
			case  7: src="Top node      (First Child     , Second Child    )      "; break;
			case  8: src="   Top node      (First Child     , Second Child    )      "; break;
			case  9: src="   Top node      (      First Child     , Second Child    )      "; break;
			default: assertTrue(String.format("i is %d", i), false);
			}
			pt = new ParseTopiary(src);
			assertPTnode(pt.getRoot(), "Top node");
			ParseTopiaryNode c;
			Object o;
			Iterator it = pt.getRoot().children().iterator();
			assertTrue(it != null);
			assertTrue(it.hasNext());
			o = it.next();
			assertTrue(o.getClass()==ParseTopiaryNode.class);
			c = (ParseTopiaryNode)o;
			assertPTnode(c, "First Child");
			
			assertTrue(it.hasNext());
			o = it.next();
			assertTrue(o.getClass()==ParseTopiaryNode.class);
			c = (ParseTopiaryNode)o;
			assertPTnode(c, "Second Child");
			
			assertFalse(it.hasNext());
		}
		
		System.out.print("passed\n");
	}

	@Test public static void testNodeNames(ParseTopiaryNode n, String... args) {
		Iterator<String> itNode = n.names().iterator();
		int i = 0;
	    for (String arg : args) {
	    	assertTrue(String.format("Node is missing %d-th name", i), itNode.hasNext());
	    	String nodeName = itNode.next();
	    	assertTrue(String.format("Expected node name %s, but instead got %s", arg, nodeName), arg.equals(nodeName));
	    	i++;
	    }
	    assertFalse(itNode.hasNext());
	}

	@Test public static void testDefaultNames() {
		System.out.print("Testing default names...");
		ParseTopiary pt;
	
		
		pt = new ParseTopiary("A");
		testNodeNames(pt.getRoot(), "A");
		
		pt = new ParseTopiary("A(B(C), D)");
		testNodeNames(pt.getRoot(), "A");
		testNodeNames(pt.getRoot().children().iterator().next(), "B");
		testNodeNames(pt.getRoot().children().iterator().next().children().iterator().next(), "C");
		
		pt = new ParseTopiary(" Kenneth Pike ( Edward Sapir ( Benjamin Worf ), Kenneth Hale)");
		testNodeNames(pt.getRoot(), "Kenneth Pike");
		testNodeNames(pt.getRoot().children().iterator().next(), "Edward Sapir");
		testNodeNames(pt.getRoot().children().iterator().next().children().iterator().next(), "Benjamin Worf");
		
		System.out.print("passed\n");
		
	}
	
	@Test public static void testExplicitNames() {
		System.out.print("Testing explicit names...");
		ParseTopiary pt;
		ParseTopiaryNode n;
		Iterator<ParseTopiaryNode> it;

		// Regular names
		pt = new ParseTopiary("Apple[name:orange]");
		testNodeNames(pt.getRoot(), "orange");
		pt = new ParseTopiary("Apple(pear[name:orange], banana[ name: Peach ]");
		testNodeNames(pt.getRoot(), "Apple");
		testNodeNames(pt.getRoot().children().iterator().next(), "orange");
		it = pt.getRoot().children().iterator();
		assertTrue(it.hasNext()); n = it.next();
		assertTrue(it.hasNext()); n = it.next();
		testNodeNames(n, "Peach");
		
		// Multiple names
		pt = new ParseTopiary("Animal on the table[name:pachyderm; name:elephant]");
		testNodeNames(pt.getRoot(), "pachyderm", "elephant");
		
		System.out.print("passed\n");
	}

	@Test public static void testTargets() {
		System.out.print("Testing targets...");
		ParseTopiary pt;
		ParseTopiaryNode n1, n2, n3;
		Iterator<ParseTopiaryNode> itNodes;
		Iterator<ParseTopiary.ParseTopiaryConnection> itTargets;
		
		pt = new ParseTopiary("Root(A, B[target:A]");
		assertFalse(pt.getRoot().targets().iterator().hasNext());
		itNodes = pt.getRoot().children().iterator();
		assertTrue(itNodes.hasNext()); n1 = itNodes.next();
		assertTrue(itNodes.hasNext()); n2 = itNodes.next();
		assertFalse(n1.targets().iterator().hasNext());
		itTargets = n2.targets().iterator();
		assertTrue(itTargets.hasNext());
		n3 = itTargets.next().getTargetNode();
		assertFalse(n3==null);
		assertTrue(n3.equals(n1));
		assertFalse(itTargets.hasNext());
		
		pt = new ParseTopiary("Root(A[target:peach], B[name:peach]");
		assertFalse(pt.getRoot().targets().iterator().hasNext());
		itNodes = pt.getRoot().children().iterator();
		assertTrue(itNodes.hasNext()); n1 = itNodes.next();
		assertTrue(itNodes.hasNext()); n2 = itNodes.next();
		itTargets = n1.targets().iterator();
		assertTrue(itTargets.hasNext());
		n3 = itTargets.next().getTargetNode();
		assertFalse(n3==null);
		assertTrue(n3.equals(n2));
		
		pt = new ParseTopiary("Root[name:watermelon](A[name:peach], B[target:watermelon]");
		assertFalse(pt.getRoot().targets().iterator().hasNext());
		itNodes = pt.getRoot().children().iterator();
		assertTrue(itNodes.hasNext()); n1 = itNodes.next();
		assertTrue(itNodes.hasNext()); n2 = itNodes.next();
		assertFalse(n1.targets().iterator().hasNext());
		itTargets = n2.targets().iterator();
		assertTrue(itTargets.hasNext());
		n3 = itTargets.next().getTargetNode();
		assertFalse(n3==null);
		assertTrue(n3.equals(pt.getRoot()));
		
		System.out.print("passed\n");
	}
	
	@Test public static void testParseOptions() {
		System.out.print("Testing node parsing with options...");
		ParseTopiary pt;
		
		// Basic options
		pt = new ParseTopiary("My node[options]");
		assertPTnode(pt.getRoot(), "My node");
		
		// Options with whitespace
		pt = new ParseTopiary("My node[options]   ");
		assertPTnode(pt.getRoot(), "My node");
		pt = new ParseTopiary("My node    [options]");
		assertPTnode(pt.getRoot(), "My node");
		pt = new ParseTopiary("My node[   options   ]");
		assertPTnode(pt.getRoot(), "My node");
		
		// Multiple options
		pt = new ParseTopiary("My node[optionA]");
		assertPTnode(pt.getRoot(), "My node");
		pt = new ParseTopiary("My node[optionA;optionB]");
		assertPTnode(pt.getRoot(), "My node");
		pt = new ParseTopiary("My node[optionA;  optionB  ]");
		assertPTnode(pt.getRoot(), "My node");
		
		System.out.print("passed\n");
	}
	
	public static void main(String[] args) {
		testBasicParseNodes();
		testDefaultNames();
		testExplicitNames();
		testTargets();
//		testParseOptions();
    }    

}
