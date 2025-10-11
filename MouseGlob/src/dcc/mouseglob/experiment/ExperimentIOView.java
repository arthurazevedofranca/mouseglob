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
package dcc.mouseglob.experiment;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.module.AbstractView;

public final class ExperimentIOView extends AbstractView<ExperimentIO> {

	@Inject
	private ExperimentIOController controller;

	@Override
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(controller.newExperimentAction);
		toolBar.add(controller.openExperimentAction);
		toolBar.add(controller.saveExperimentAction);
		toolBar.add(controller.saveExperimentAsAction);

		return toolBar;
	}

	@Override
	public JMenu makeMenu() {
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getPopupMenu().setLightWeightPopupEnabled(false);

		menu.add(controller.newExperimentAction);
		menu.add(controller.openExperimentAction);
		menu.add(controller.saveExperimentAction);
		menu.add(controller.saveExperimentAsAction);

		return menu;
	}

	void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

}
