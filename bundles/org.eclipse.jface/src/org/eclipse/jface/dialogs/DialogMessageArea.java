/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
/**
 * The DialogMessageArea is a resusable component for adding an accessible
 * message area to a dialog.
 * 
 * When the message is normal a CLabel is used but an errors replaces the
 * message area with a non editable text that can take focus for use by screen
 * readers.
 * 
 * @since 3.0
 */
public class DialogMessageArea extends Object {
	private Text messageText;
	private Label messageImageLabel;
	private Composite messageComposite;
	private String lastMessageText;
	private int lastMessageType;
	private CLabel titleLabel;
	/**
	 * Create a new instance of the receiver.
	 */
	public DialogMessageArea() {
		//No initial behaviour
	}
	/**
	 * Create the contents for the receiver.
	 * 
	 * @param parent
	 *            the Composite that the children will be created in
	 */
	public void createContents(Composite parent) {
		Display display = parent.getDisplay();
		Color background = JFaceColors.getBannerBackground(display);
		Color foreground = JFaceColors.getBannerForeground(display);
		// Message label
		titleLabel = new CLabel(parent, SWT.NONE);
		JFaceColors.setColors(titleLabel, foreground, background);
		titleLabel.setFont(JFaceResources.getBannerFont());
		messageComposite = new Composite(parent, SWT.NONE);
		GridLayout messageLayout = new GridLayout();
		messageLayout.numColumns = 2;
		messageLayout.marginWidth = 0;
		messageLayout.marginHeight = 0;
		messageLayout.makeColumnsEqualWidth = false;
		messageComposite.setLayout(messageLayout);
		messageImageLabel = new Label(messageComposite, SWT.NONE);
		messageImageLabel.setImage(JFaceResources
				.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
		messageImageLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_CENTER));
		messageImageLabel.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_YELLOW));
		messageText = new Text(messageComposite, SWT.NONE);
		messageText.setEditable(false);
		messageText.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_RED));
		GridData textData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		messageText.setLayoutData(textData);
		setMessageColors(JFaceColors.getBannerBackground(messageComposite
				.getDisplay()));
	}
	/**
	 * Set the layoutData for the title area. In most cases this will be a copy
	 * of the layoutData used in setMessageLayoutData.
	 * 
	 * @param layoutData
	 *            the layoutData for the title
	 * @see #setMessageLayoutData(Object)
	 */
	public void setTitleLayoutData(Object layoutData) {
		titleLabel.setLayoutData(layoutData);
	}
	/**
	 * Set the layoutData for the messageArea. In most cases this will be a copy
	 * of the layoutData used in setTitleLayoutData.
	 * 
	 * @param layoutData
	 *            the layoutData for the message area composite.
	 * @see #setTitleLayoutData(Object)
	 */
	public void setMessageLayoutData(Object layoutData) {
		messageComposite.setLayoutData(layoutData);
	}
	/**
	 * Show the title.
	 * 
	 * @param titleMessage
	 *            String for the titke
	 * @param titleImage
	 *            Image or <code>null</code>
	 */
	public void showTitle(String titleMessage, Image titleImage) {
		titleLabel.setImage(titleImage);
		titleLabel.setText(titleMessage);
		restoreTitle();
		return;
	}
	/**
	 * Enable the title and disable the message text and image.
	 */
	public void restoreTitle() {
		titleLabel.setVisible(true);
		messageComposite.setVisible(false);
		lastMessageText = null;
		lastMessageType = IMessageProvider.NONE;
	}
	/**
	 * Show the new message in the message text and update the image. Base the
	 * background color on whether or not there are errors.
	 * 
	 * @param newMessage
	 *            The new value for the message
	 * @param newType
	 *            One of the IMessageProvider constants. If newType is
	 *            IMessageProvider.NONE show the title.
	 * @see IMessageProvider
	 */
	public void updateText(String newMessage, int newType) {
		Image newImage = null;
		boolean showingError = false;
		switch (newType) {
			case IMessageProvider.NONE :
				if (newMessage == null)
					restoreTitle();
				else
					showTitle(newMessage, null);
				return;
			case IMessageProvider.INFORMATION :
				newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
				break;
			case IMessageProvider.WARNING :
				newImage = JFaceResources
						.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
				break;
			case IMessageProvider.ERROR :
				newImage = JFaceResources
						.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
				showingError = true;
				break;
		}
		messageComposite.setVisible(true);
		titleLabel.setVisible(false);
		// Any more updates required
		if (newMessage.equals(messageText.getText())
				&& newImage == messageImageLabel.getImage())
			return;
		messageImageLabel.setImage(newImage);
		messageText.setText(newMessage);
		if (showingError)
			setMessageColors(JFaceColors.getErrorBackground(messageComposite
					.getDisplay()));
		else {
			lastMessageText = newMessage;
			setMessageColors(JFaceColors.getBannerBackground(messageComposite
					.getDisplay()));
		}
	}
	/**
	 * Set the colors of the message area.
	 * 
	 * @param color
	 *            The color to be use in the message area.
	 */
	private void setMessageColors(Color color) {
		messageText.setBackground(color);
		messageComposite.setBackground(color);
		messageImageLabel.setBackground(color);
	}
	/**
	 * Clear the error message. Restore the previously displayed message if
	 * there is one, if not restore the title label.
	 *  
	 */
	public void clearErrorMessage() {
		if (lastMessageText == null)
			restoreTitle();
		else
			updateText(lastMessageText, lastMessageType);
	}
}