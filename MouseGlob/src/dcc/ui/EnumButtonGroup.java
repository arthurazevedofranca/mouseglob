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

import javax.swing.Icon;

public abstract class EnumButtonGroup<E extends Enum<E>> extends ButtonGroup {

	public abstract void valueSelected(E value);

	public void add(String name, E value) {
		add(new EnumRadioAction(name, value));
	}

	public void add(String name, Icon icon, E value) {
		add(new EnumRadioAction(name, icon, value));
	}

	@SuppressWarnings("serial")
	private class EnumRadioAction extends RadioAction {

		private final E value;

		public EnumRadioAction(String name, E value) {
			super(name);
			this.value = value;
		}

		public EnumRadioAction(String name, Icon icon, E value) {
			super(name, icon);
			this.value = value;
		}

		@Override
		public void actionPerformed() {
			valueSelected(value);
		}

	}

}
