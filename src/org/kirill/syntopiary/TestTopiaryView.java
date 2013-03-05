package org.kirill.syntopiary;

public class TestTopiaryView {

	public static void testLayoutSimple() {
		String[] args;
		
		// Parent node larger than child node
		args = new String[3];
		args[0] = "--treespec=AAAAAAAAAAAAA(B)";
		args[1] = "--svgfile=test_output_1.svg";
		args[2] = "--terminate=true";
		MainApp.main(args);
		
	}
	public static void main(String[] args) {
		testLayoutSimple();
	}

}
