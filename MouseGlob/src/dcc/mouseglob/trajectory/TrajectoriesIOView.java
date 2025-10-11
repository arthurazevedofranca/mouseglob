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
package dcc.mouseglob.trajectory;

import javax.swing.JToolBar;

import dcc.module.AbstractView;

public class TrajectoriesIOView extends AbstractView<TrajectoriesIO> {
	@Override
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar("MouseGlob");
		TrajectoriesIOController controller = getModule().getController();
		controller.recordAction.setEnabled(false);
		toolBar.add(controller.recordAction.getToggleButton());
		toolBar.add(controller.analyzeAction.getToggleButton());
		return toolBar;
	}
}
