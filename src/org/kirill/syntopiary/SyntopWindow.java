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
 
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
 
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.json.JSON;
import org.apache.pivot.serialization.CSVSerializer;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentKeyListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextAreaContentListener;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.TextAreaContentListener;

import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
 
@SuppressWarnings("unused")
public class SyntopWindow extends Window implements Bindable {
	@BXML private SyntopTextArea treeTextArea = null;
//	@BXML private TextInput treeTextInput = null;
	@BXML private TopiaryView mainView = null;
	@BXML private PushButton btnParse = null;
	@BXML private Checkbox chkAutomaticHats = null;
	@BXML private Checkbox chkDebugDrawBoxText = null;
	@BXML private Checkbox chkDebugDrawBoxNode = null;
	@BXML private Checkbox chkDebugDrawBoxFull = null;
     
	
    private Action applyTreeSpecificationTextAction = new Action(true) {
        @Override
        public void perform(Component source) {
        	applyTreeSpecificationText();
        }
    };    

	private SyntopTextAreaListener treeTextAreaEventSync = new SyntopTextAreaListener.Adapter() {
        @Override
        public void enterPressed(SyntopTextArea syntopTextArea) {
        	applyTreeSpecificationText();
        }
	};
	
	private Action debugOptionsChangedAction = new Action(true) {
		@Override
		public void perform(Component source) {
			applyDebugOptionsChanged();
		}
	};    

    private Action automaticHatsChangedAction = new Action(true) {
        @Override
        public void perform(Component source) {
        	applyAutomaticHatsOptionChanged();
        }
    };    
        
    public SyntopWindow() { 
        // Add action mapping to refresh the symbol table view
    	/*
        Keyboard.Modifier commandModifier = Platform.getCommandModifier();
        Keyboard.KeyStroke refreshKeystroke = new Keyboard.KeyStroke(Keyboard.KeyCode.R,
            commandModifier.getMask());
        getActionMappings().add(new ActionMapping(refreshKeystroke, refreshTableAction));
        */
    	Action.getNamedActions().put("generateSVG", new Action() {
    		@Override
    		public void perform(Component source) {
    			generateSVGoutput();
    		}
    	});    	
        Action.getNamedActions().put("copyAsSVG", new Action() {
            @Override
            public void perform(Component source) {
            	copyAsSVG();
            }
        });    	
    	Action.getNamedActions().put("generateEPS", new Action() {
    		@Override
    		public void perform(Component source) {
    			generateEPSoutput();
    		}
    	});    	
    	
    }
 
    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        // Add stocks table view event handlers
 
 
        // Add symbol text input event handler
/*    	
    	treeTextArea.getTextAreaContentListeners().add(new TextAreaContentListener.Adapter() {
            @Override
            public void textChanged(TextArea textArea) {
                // TODO: respond to text changes, maybe
            }
    	});
*/    	
        // Assign actions to add and remove symbol buttons
    	btnParse.setAction(applyTreeSpecificationTextAction);
    	if (chkAutomaticHats != null) chkAutomaticHats.setAction(automaticHatsChangedAction);
    	if (chkDebugDrawBoxText != null) chkDebugDrawBoxText.setAction(debugOptionsChangedAction);
    	if (chkDebugDrawBoxNode != null) chkDebugDrawBoxNode.setAction(debugOptionsChangedAction);
    	if (chkDebugDrawBoxFull != null) chkDebugDrawBoxFull.setAction(debugOptionsChangedAction);
    	treeTextArea.getSyntopTextAreaListeners().add(treeTextAreaEventSync);
    }

    public void setTreeSpecificationText(String treeSpecification) {
    	assert(treeSpecification!=null);
    	treeTextArea.setText(treeSpecification);
//    	mainView.getParseTopiary().setParseString(treeSpecification);
    }
    
    public void applyTreeSpecificationText() {
    	String s = treeTextArea.getText();
    	mainView.getParseTopiary().setParseString(s);
    }
    
    public void makeSVGfile(String filename) {
        File file = new java.io.File(filename);
        mainView.generateSVG(file);
    }
    
    public void applyDebugOptionsChanged() {
    	if (chkDebugDrawBoxText != null) 
    		mainView.setDrawTextBoundaries(chkDebugDrawBoxText.isSelected());
    	if (chkDebugDrawBoxText != null) 
    		mainView.setDrawNodeBoundaries(chkDebugDrawBoxNode.isSelected());
    	if (chkDebugDrawBoxFull != null) 
    		mainView.setDrawFullBoundaries(chkDebugDrawBoxFull.isSelected());
    }
    
    // TODO: Change name of these methods, it is a bit confusing now
    public void applyAutomaticHatsOptionChanged() {
        if (chkAutomaticHats != null) 
        	mainView.setDrawAutomaticHats(chkAutomaticHats.isSelected());
    }
    
    public void generateSVGoutput() {
		JFileChooser fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("SVG files", "svg");
	    fc.setFileFilter(filter);
//	    fc.setDialogType(JFileChooser.SAVE_DIALOG);
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            mainView.generateSVG(file);
        } else {
        	// Open command cancelled by user
        }		
    }
    
    public void copyAsSVG() {
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        mainView.copyAsSVG(clipboard);
    }

    public void generateEPSoutput() {
		JFileChooser fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("EPS files", "eps");
	    fc.setFileFilter(filter);
//	    fc.setDialogType(JFileChooser.SAVE_DIALOG);
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            mainView.generateEPS(file);
        } else {
        	// Open command cancelled by user
        }		
    }
    
}