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
package dcc.module;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;

public abstract class AbstractView<T extends AbstractModule<?, ? extends AbstractView<T>, ?>>
		implements View {
	T module;

	public AbstractView(T module) {
		this.module = module;
	}

	public AbstractView() {
		this(null);
	}

	protected final T getModule() {
		return module;
	}

	@Override
	public JComponent makePanel() {
		return null;
	}

	@Override
	public JToolBar makeToolBar() {
		return null;
	}

	@Override
	public JMenu makeMenu() {
		return null;
	}
}
