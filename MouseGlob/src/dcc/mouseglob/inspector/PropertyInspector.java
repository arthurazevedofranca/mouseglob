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
package dcc.mouseglob.inspector;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class PropertyInspector<T> {
	static final Dimension FIELD_SIZE = new Dimension(80, 30);

	String name;
	private String format;
	private JTextField field;

	protected PropertyInspector(String name) {
		this.name = name;
		field = new JTextField(0);
		field.setEditable(false);
		field.setMinimumSize(FIELD_SIZE);
		field.setMaximumSize(FIELD_SIZE);
		field.setPreferredSize(FIELD_SIZE);
	}

	protected PropertyInspector(String name, String format) {
		this(name);
		this.format = format;
	}

	void update() {
		if (format != null && !format.isEmpty())
			field.setText(String.format(format, getValue()));
		else
			field.setText(getValue().toString());
	}

	JPanel makePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		if (name != null) {
			panel.add(new JLabel(name + ": "));
			panel.add(Box.createHorizontalGlue());
		}
		panel.add(getComponent());
		return panel;
	}

	Component getComponent() {
		return field;
	}

	protected abstract T getValue();

	public static <T> PropertyInspector<T> getBasic(String name,
			final T property) {
		return new PropertyInspector<T>(name) {
			@Override
			protected T getValue() {
				return property;
			}
		};
	}

	@Override
	public String toString() {
		return name + ": " + getValue();
	}

}
