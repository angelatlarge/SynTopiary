package org.kirill.syntopiary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TestTopiaryView {
	
	private final String imageNameTemplate_T = "test_output_%d.svg";
	private final String htmlFilename = "TestResults.html";
	private int idxCurrentImage = 0;
	private StringBuilder htmlOutput = new StringBuilder();
	
	protected void makeTreeImage(String specification) throws InterruptedException {
		String[] args = new String[3];
		args[0] = String.format("--treespec=%s", specification);
		String fileSpec = String.format(imageNameTemplate_T, idxCurrentImage++);
		args[1] = String.format("--svgfile=%s", fileSpec);
		args[2] = "--terminate=true";
		MainApp.main(args);
		htmlOutput.append(String.format("<P>%s\n", specification));
		htmlOutput.append(String.format("<BR><IMG SRC=\"%s\" style=\"border: 1px solid red\">\n", fileSpec));
		Thread.sleep(100);
//		Thread.sleep(250);
	}
	
	
	protected void testLayoutSimple() throws InterruptedException {

		htmlOutput.append(String.format("<H1>Simple Layout</H1>\n"));
		
		htmlOutput.append(String.format("<H2>Parent node larger than child node</H2>\n"));
		makeTreeImage("AAAAAAAAAAAAAAAAA(B)");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B(C))");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B(C, D))");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B(C, D, E))");
		
		htmlOutput.append(String.format("<H2>Child node larger than parent node</H2>\n"));
		makeTreeImage("A(BBBBBBBBBBBBBB)");
		makeTreeImage("A(BBBBBBBBBBBBBB(C))");
		makeTreeImage("A(BBBBBBBBBBBBBB(C, D))");
		makeTreeImage("A(BBBBBBBBBBBBBB(C, D, E))");
		makeTreeImage("A(BBBBBBBBBBBBBB(C, D(FFFF), E))");
		
	}
	
	protected void testLayoutHats() throws InterruptedException {
		
		htmlOutput.append(String.format("<H1>Hats</H1>"));
		
		htmlOutput.append(String.format("<H2>Parent node larger than child node: child node less than minimum</H2>\n"));
		makeTreeImage("AAAAAAAAAAAAAAAAA(B[hat])");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B[hat](C))");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B[hat](C, D))");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B[hat](C, D, E))");
		
		htmlOutput.append(String.format("<H2>Child node larger than parent node</H2>\n"));
		makeTreeImage("A(BBBBBBBBBBBBBB[hat])");
		makeTreeImage("A(BBBBBBBBBBBBBB[hat](C))");
		makeTreeImage("A(BBBBBBBBBBBBBB[hat](C, D))");
		makeTreeImage("A(BBBBBBBBBBBBBB[hat](C, D, E))");
		makeTreeImage("A(BBBBBBBBBBBBBB[hat](C, D(FFFF), E))");
		
		htmlOutput.append(String.format("<H2>Parent node larger, hat not larger than minimum</H2>\n"));
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(BBBBBBBBBBBBBB[hat])");
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(BBBBBBBBBBBBBB[hat](C))");
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(BBBBBBBBBBBBBB[hat](C, D))");
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(BBBBBBBBBBBBBB[hat](C, D, E))");
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(BBBBBBBBBBBBBB[hat](C, D(FFFF), E))");
		
		htmlOutput.append(String.format("<H2>real tree?</H2>\n"));
		makeTreeImage("AAAAAAAAAAAAAAAAAAAAAAAAA(CP(C, TP(DP, (T, VP(V, DP(...[hat]");		
	}
	
	protected void testAutoHats() throws InterruptedException {
		
		htmlOutput.append(String.format("<H1>Automatic Hats</H1>"));
		
//		htmlOutput.append(String.format("<H2>Parent node larger than child node: child node less than minimum</H2>\n"));
		makeTreeImage("Root(test node");
		makeTreeImage("Root(test node(sub test node");
		makeTreeImage("Root(test node 1, test node 2");
		makeTreeImage("Root(test node 1, test node 2(test node 3)");
		
		
	}
	
	public void testAll() throws InterruptedException, IOException {
		htmlOutput.append("<html>\n<body>\n");
		try	{
			testLayoutSimple();
			testLayoutHats();
			htmlOutput.append("</body>\n<html>\n");
		} finally {
			// Make an HTML output file
			FileOutputStream fos = new FileOutputStream(new File(htmlFilename));
			OutputStreamWriter writerOut = new OutputStreamWriter(fos, "UTF-8");
			writerOut.write(htmlOutput.toString());
			writerOut.close();
			fos.close();
		}
		Runtime.getRuntime().exec(String.format("firefox %s", htmlFilename));
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		TestTopiaryView ttv = new TestTopiaryView();
		ttv.testAll();
	}

}
