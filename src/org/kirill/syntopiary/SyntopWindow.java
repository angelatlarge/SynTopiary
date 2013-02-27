package org.kirill.syntopiary;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import java.awt.Desktop;
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

import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
 
@SuppressWarnings("unused")
public class SyntopWindow extends Window implements Bindable {
	@BXML private TextArea treeTextArea = null;
//	@BXML private TextInput treeTextInput = null;
	@BXML private TopiaryView mainView = null;
	@BXML private PushButton btnParse = null;
	@BXML private Checkbox chkDebugDrawBoxText = null;
	@BXML private Checkbox chkDebugDrawBoxNode = null;
	@BXML private Checkbox chkDebugDrawBoxFull = null;
     
    
    private Action applyTreeSpecificationTextAction = new Action(true) {
        @Override
        public void perform(Component source) {
        	applyTreeSpecificationText();
        }
    };    
    
    private Action debugOptionsChangedAction = new Action(true) {
        @Override
        public void perform(Component source) {
        	applyDebugOptionsChanged();
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
    	if (chkDebugDrawBoxText != null) chkDebugDrawBoxText.setAction(debugOptionsChangedAction);
    	if (chkDebugDrawBoxNode != null) chkDebugDrawBoxNode.setAction(debugOptionsChangedAction);
    	if (chkDebugDrawBoxFull != null) chkDebugDrawBoxFull.setAction(debugOptionsChangedAction);
 
    }

    public void applyTreeSpecificationText() {
    	String s = treeTextArea.getText();
//    	String s = treeTextInput.getText();
    	mainView.getParseTopiary().setParseString(s);
    }
    
    public void applyDebugOptionsChanged() {
    	// TODO: Write the code here
        System.out.print("Debug options changed\n");
        if (chkDebugDrawBoxText != null) 
        	mainView.setDrawTextBoundaries(chkDebugDrawBoxText.isSelected());
        if (chkDebugDrawBoxText != null) 
        	mainView.setDrawNodeBoundaries(chkDebugDrawBoxNode.isSelected());
        if (chkDebugDrawBoxFull != null) 
        	mainView.setDrawFullBoundaries(chkDebugDrawBoxFull.isSelected());
    }
    
    public void generateSVGoutput() {
		JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.

        } else {
        	// Open command cancelled by user
        }		
    }

}