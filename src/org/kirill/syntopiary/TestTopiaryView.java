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
		Thread.sleep(1000);
	}
	
	
	protected void testLayoutSimple() throws InterruptedException {
		
		// Parent node larger than child node
		makeTreeImage("AAAAAAAAAAAAAAAAA(B)");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B(C))");
		makeTreeImage("AAAAAAAAAAAAAAAAA(B(C, D))");
		
	}
	
	
	public void testAll() throws InterruptedException, IOException {
		htmlOutput.append("<html>\n<body>\n");
		try	{
			testLayoutSimple();
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
