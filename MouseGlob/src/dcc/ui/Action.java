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
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public abstract class Action extends AbstractAction {
	private String description;
	private KeyStroke accelerator;

	public Action(String name, Icon icon, KeyStroke accelerator) {
		super(name, icon);
		setDescription(name);
		setAccelerator(accelerator);
	}

	public Action(String name, Icon icon) {
		this(name, icon, null);
	}

	public Action(String name, String iconPath) {
		this(name);
		if (iconPath != null) {
			URL iconURL = Action.class.getResource(iconPath);
			if (iconURL != null)
				setIcon(new ImageIcon(iconURL));
		}
	}

	public Action(String name, KeyStroke accelerator) {
		this(name, null, accelerator);
	}

	public Action(String name) {
		this(name, null, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		actionPerformed();
	}

	public abstract void actionPerformed();

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
		updateDescription();
	}

	public final void setAccelerator(KeyStroke accelerator) {
		this.accelerator = accelerator;
		if (accelerator != null) {
			putValue(ACCELERATOR_KEY, accelerator);
			updateDescription();
		}
	}

	public final Icon getIcon() {
		return (Icon) getValue(SMALL_ICON);
	}

	public final void setIcon(Icon icon) {
		putValue(SMALL_ICON, icon);
	}

	public final JButton getIconButton() {
		JButton button = new JButton(this);

		button.setPreferredSize(ComponentFactory.BUTTON_DIMENSION);
		button.setMaximumSize(ComponentFactory.BUTTON_DIMENSION);
		button.setMinimumSize(ComponentFactory.BUTTON_DIMENSION);
		button.setText("");

		return button;
	}

	private void updateDescription() {
		if (accelerator == null)
			putValue(SHORT_DESCRIPTION, description);
		else
			putValue(SHORT_DESCRIPTION, description + " ("
					+ getString(accelerator) + ")");
	}

	private static String getString(KeyStroke keyStroke) {
		String string = "";

		int modifiers = keyStroke.getModifiers();

		if ((modifiers & ActionEvent.CTRL_MASK) != 0)
			string += "Ctrl+";
		if ((modifiers & ActionEvent.ALT_MASK) != 0)
			string += "Alt+";
		if ((modifiers & ActionEvent.SHIFT_MASK) != 0)
			string += "Shift+";

		return string + String.valueOf((char) keyStroke.getKeyCode());
	}
}
