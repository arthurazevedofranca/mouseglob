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
package dcc.mouseglob.analysis;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.module.AbstractView;
import dcc.mouseglob.analysis.AnalysesController.AnalysisAction;

public class AnalysesView extends AbstractView<AnalysesModule> {

	@Inject
	private AnalysesController controller;

	@Override
	public JPanel makePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (AnalysisAction action : controller.actions)
			panel.add(action.getCheckBox());
		return panel;
	}

	public JButton getManageAnalysesButton() {
		return new JButton(controller.manageAnalysesAction);
	}

}
