/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.tests.internal.databinding.internal.swt;

import junit.framework.Test;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.conformance.ObservableDelegateTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.conformance.swt.SWTMutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.swt.SWTObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.util.SuiteBuilder;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.internal.swt.SWTProperties;
import org.eclipse.jface.internal.databinding.internal.swt.SpinnerObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * @since 3.2
 */
public class SpinnerObservableValueMinTest extends ObservableDelegateTest {
	private Delegate delegate;

	private Spinner spinner;

	private IObservableValue observable;

	public SpinnerObservableValueMinTest() {
		this(null);
	}

	public SpinnerObservableValueMinTest(String testName) {
		super(testName, new Delegate());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		delegate = (Delegate) getObservableContractDelegate();
		observable = (IObservableValue) getObservable();
		spinner = delegate.spinner;
	}
	
	protected IObservable doCreateObservable() {
		return getObservableContractDelegate().createObservable(SWTObservables.getRealm(Display.getDefault()));
	}

	public void testGetValue() throws Exception {
		int min = 100;
		spinner.setMinimum(min);
		assertEquals(new Integer(min), observable.getValue());
	}

	public void testSetValue() throws Exception {
		int min = 100;
		observable.setValue(new Integer(min));
		assertEquals(min, spinner.getMinimum());
	}

	public static Test suite() {
		Delegate delegate = new Delegate();
		return new SuiteBuilder().addTests(SpinnerObservableValueMinTest.class)
				.addObservableContractTest(
						SWTObservableValueContractTest.class, delegate)
				.addObservableContractTest(
						SWTMutableObservableValueContractTest.class, delegate)
				.build();
	}

	/* package */static class Delegate extends
			AbstractObservableValueContractDelegate {
		private Shell shell;

		Spinner spinner;

		public void setUp() {
			shell = new Shell();
			spinner = new Spinner(shell, SWT.NONE);
			spinner.setMaximum(1000);
		}

		public void tearDown() {
			shell.dispose();
		}

		public IObservableValue createObservableValue(Realm realm) {
			return new SpinnerObservableValue(realm, spinner, SWTProperties.MIN);
		}

		public void change(IObservable observable) {
			IObservableValue observableValue = (IObservableValue) observable;
			observableValue.setValue(createValue(observableValue));
		}

		public Object getValueType(IObservableValue observable) {
			return Integer.TYPE;
		}

		public Object createValue(IObservableValue observable) {
			return createIntegerValue(observable);
		}

		private Integer createIntegerValue(IObservableValue observable) {
			return new Integer(((Integer) observable.getValue()).intValue() + 1);
		}
	}
}
