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

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public abstract class ToggleAction extends Action {
	private boolean state;
	private ArrayList<AbstractButton> buttons;

	public ToggleAction(String name, Icon icon) {
		super(name, icon);
		state = false;
		buttons = new ArrayList<AbstractButton>();
	}

	public ToggleAction(String name, String iconPath) {
		super(name, iconPath);
		state = false;
		buttons = new ArrayList<AbstractButton>();
	}

	public ToggleAction(String name) {
		super(name);
		state = false;
		buttons = new ArrayList<AbstractButton>();
	}

	@Override
	public final void actionPerformed() {
		state = !state;
		setSelected(state);
	}

	public abstract void itemStateChanged(boolean state);

	public final boolean isSelected() {
		return state;
	}

	public final void setSelected(boolean b) {
		state = b;
		for (AbstractButton button : buttons)
			button.setSelected(state);
		itemStateChanged(state);
	}

	/**
	 * Convenience method equivalent to
	 * {@code if(isSelected())setSelected(false)}.
	 */
	public final void clearSelection() {
		if (isSelected())
			setSelected(false);
	}

	public final JToggleButton getToggleButton() {
		JToggleButton button = new JToggleButton(this);
		button.setText("");
		button.setEnabled(isEnabled());
		button.setSelected(isSelected());
		buttons.add(button);
		return button;
	}

	public final JCheckBoxMenuItem getCheckBoxMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(this);
		item.setEnabled(isEnabled());
		item.setSelected(isSelected());
		buttons.add(item);
		return item;
	}

	public final JCheckBox getCheckBox() {
		JCheckBox checkBox = new JCheckBox(this);
		checkBox.setEnabled(isEnabled());
		checkBox.setSelected(isSelected());
		buttons.add(checkBox);
		return checkBox;
	}

}
