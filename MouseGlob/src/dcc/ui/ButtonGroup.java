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

/**
 * A <code>ButtonGroup</code> handles a collection of mutually exclusive boolean
 * controls, such as radio buttons.
 * 
 * @author Daniel Coelho de Castro
 */
public class ButtonGroup {

	private final ArrayList<RadioAction> actions;

	public ButtonGroup() {
		actions = new ArrayList<RadioAction>();
	}

	public ButtonGroup(RadioAction... actions) {
		this();
		for (RadioAction a : actions)
			add(a);
	}

	public void add(RadioAction action) {
		actions.add(action);
		action.group = this;
	}

	public void select(int index) {
		actions.get(index).setSelected(true);
	}

	public ArrayList<RadioAction> getRadioActions() {
		return actions;
	}

	void notify(RadioAction action) {
		for (RadioAction a : actions)
			if (!a.equals(action))
				a.setSelected(false);
	}

	public PopupMenu makePopupMenu() {
		PopupMenu popupMenu = new PopupMenu();
		for (RadioAction action : actions)
			popupMenu.add(action.getRadioButtonMenuItem());
		return popupMenu;
	}

}
