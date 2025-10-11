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

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;

@SuppressWarnings("serial")
public abstract class RadioAction extends Action {
	private boolean state;
	private ArrayList<AbstractButton> buttons;
	ButtonGroup group;

	public RadioAction(String name, Icon icon, ButtonGroup group) {
		super(name, icon);
		state = false;
		buttons = new ArrayList<AbstractButton>();
		this.group = group;
		if (group != null)
			group.add(this);
	}

	public RadioAction(String name, Icon icon) {
		this(name, icon, null);
	}

	public RadioAction(String name, ButtonGroup group) {
		this(name, null, group);
	}

	public RadioAction(String name) {
		this(name, null, null);
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		state = true;
		setSelected(state);
	}

	/**
	 * Implements the concrete behaviour of the control once it is selected.
	 */
	@Override
	public abstract void actionPerformed();

	public final boolean isSelected() {
		return state;
	}

	public final void setSelected(boolean b) {
		state = b;

		if (group != null && state)
			group.notify(this);

		for (AbstractButton button : buttons)
			button.setSelected(state);

		if (state)
			actionPerformed();
	}

	public final JRadioButton getRadioButton() {
		JRadioButton button = new JRadioButton(this);
		buttons.add(button);
		button.setSelected(state);
		return button;
	}

	public final JRadioButtonMenuItem getRadioButtonMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(this);
		buttons.add(item);
		item.setSelected(state);
		return item;
	}

}
