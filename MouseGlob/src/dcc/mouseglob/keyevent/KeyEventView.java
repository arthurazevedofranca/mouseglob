/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.mouseglob.keyevent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dcc.inject.Inject;
import dcc.module.AbstractView;

/**
 * Handles all UI features for the Key Event module.
 * 
 * @author Daniel Coelho de Castro
 */
public final class KeyEventView extends AbstractView<KeyEventModule> {
	private JTextField textDescription;
	private JLabel label;

	@Inject
	private KeyEventController controller;

	@Override
	public JPanel makePanel() {
		JPanel panel = new JPanel();

		textDescription = new JTextField(20);

		label = new JLabel();

		panel.add(textDescription);
		panel.add(controller.chooseKeyAction.getIconButton());
		panel.add(label);
		panel.add(controller.addKeyEventClassAction.getIconButton());

		return panel;
	}

	/**
	 * Creates the key event class creation dialog.
	 * 
	 * @return the <code>JDialog</code>
	 */
	public JDialog makeDialog() {
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(makePanel());
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);

		return optionPane.createDialog(null, "Add Key Event Classes...");
	}

	String getDescription() {
		return textDescription.getText();
	}
}
