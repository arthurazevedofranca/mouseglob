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
package dcc.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public abstract class TextAction extends Action implements FocusListener {
	private String text;
	private ArrayList<JTextField> fields;
	private ArrayList<JLabel> labels;

	public TextAction(String name) {
		super(name);
		fields = new ArrayList<JTextField>();
		labels = new ArrayList<JLabel>();
	}

	public TextAction(String name, Icon icon) {
		super(name, icon);
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		JTextField field = (JTextField) e.getSource();
		setText(field.getText());
	}

	@Override
	public final void actionPerformed() {
	}

	public abstract void textChanged(String text);

	@Override
	public final void focusLost(FocusEvent e) {
		focusLost();
	}

	public void focusLost() {
	}

	@Override
	public final void focusGained(FocusEvent e) {
		focusGained();
	}

	public void focusGained() {
	}

	public final String getText() {
		return text;
	}

	public final void setText(String text) {
		this.text = text;
		for (JTextField field : fields) {
			field.setText(text);
			field.addFocusListener(this);
		}
		for (JLabel label : labels)
			label.setText(text);
		textChanged(text);
	}

	public final JTextField getTextField(Dimension size) {
		JTextField field = new JTextField(text);
		field.setPreferredSize(size);
		field.setMinimumSize(size);
		field.setMaximumSize(size);
		field.addActionListener(this);
		fields.add(field);

		return field;
	}

	public final JLabel getLabel() {
		JLabel label = new JLabel(text);
		labels.add(label);
		return label;
	}
}
