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
		assertTrue(n.getText().equals(text));
	}
	
	@Test public static void testParseTopiary() {
		System.out.print("Testing parsing...");
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
	
	public static void main(String[] args) {
		testParseTopiary();
    }    

}
