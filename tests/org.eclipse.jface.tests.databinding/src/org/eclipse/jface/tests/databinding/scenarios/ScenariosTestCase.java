/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.databinding.scenarios;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.jface.databinding.IDataBindingContext;
import org.eclipse.jface.databinding.swt.SWTUpdatableFactory;
import org.eclipse.jface.tests.databinding.scenarios.model.SampleData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract base class of the JFace binding scenario test classes.
 */

abstract public class ScenariosTestCase extends TestCase {

	private Composite composite;

	private IDataBindingContext dbc;

	private Display display;

	private boolean disposeDisplay = false;

	private Shell shell;

	protected Text dummyText;

	private SWTUpdatableFactory swtUpdatableFactory;

	protected Composite getComposite() {
		return composite;
	}

	protected IDataBindingContext getDbc() {
		return dbc;
	}
	
	protected SWTUpdatableFactory getSWTUpdatableFactory() {
		return swtUpdatableFactory;
	}

	public Shell getShell() {
		if (shell != null) {
			return shell;
		}
		Shell result = BindingScenariosTestSuite.getShell();
		if (result == null) {
			display = Display.getDefault();
			if (Display.getDefault() == null) {
				display = new Display();
				disposeDisplay = true;
			}
			shell = new Shell(display, SWT.SHELL_TRIM);
			shell.setLayout(new FillLayout());
			result = shell;
		}
		result.setText(getName()); // In the case that the shell() becomes
		// visible.
		return result;
	}

	protected void spinEventLoop(int secondsToWaitWithNoEvents) {
		if (!composite.isVisible() && secondsToWaitWithNoEvents > 0) {
			composite.getShell().open();
		}
		while (composite.getDisplay().readAndDispatch()) {
			// do nothing, just process events
		}
		try {
			Thread.sleep(secondsToWaitWithNoEvents * 1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	protected void setButtonSelectionWithEvents(Button button, boolean value) {
		if (button.getSelection() == value) {
			return;
		}
		button.setSelection(value);
		pushButtonWithEvents(button);
	}

	protected void pushButtonWithEvents(Button button) {
		button.notifyListeners(SWT.Selection, null);
	}

	protected void setUp() throws Exception {
		composite = new Composite(getShell(), SWT.NONE);
		composite.setLayout(new GridLayout());
		SampleData.initializeData(); // test may manipulate the data... let
		// all start from fresh
		dbc = SampleData.getDatabindingContext(composite);
		swtUpdatableFactory = SampleData.getSWTUpdatableFactory();
		dummyText = new Text(getComposite(), SWT.NONE);
		dummyText.setText("dummy");
	}

	protected void tearDown() throws Exception {
		getShell().setVisible(false); // same Shell may be reUsed across tests
		composite.dispose();
		composite = null;
		if (shell != null) {
			shell.close();
			shell.dispose();
		} else
			dbc.dispose();
		if (display != null && disposeDisplay) {
			display.dispose();
		}
	}

	protected void enterText(Text text, String string) {
		text.notifyListeners(SWT.FocusIn, null);
		text.setText(string);
		text.notifyListeners(SWT.FocusOut, null);
	}

	protected void assertArrayEquals(Object[] expected, Object[] actual) {
		assertEquals(Arrays.asList(expected), Arrays.asList(actual));
	}
		
	public static void invokeNonUI(final Runnable aRunnable){
		
		final RuntimeException[] nonUIException = new RuntimeException[1];
		Thread t = new Thread(aRunnable){
			public void run(){
				try{
					super.run();
				} catch (Exception exc){
					RuntimeException runtimeException = new RuntimeException(exc);
					runtimeException.fillInStackTrace();
					nonUIException[0] = runtimeException; 
					exc.printStackTrace();
				}
			}
		};
		t.start();
		while(t.isAlive()) {
			while(Display.getCurrent().readAndDispatch());
			try {
				t.join(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(nonUIException[0] != null){
			throw nonUIException[0];
		}
	}
}
