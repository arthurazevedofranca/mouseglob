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
package dcc.mouseglob.report;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.module.View;
import dcc.mouseglob.report.ReportsController.ReportAction;

public class ReportsView implements View {

	@Inject
	private ReportsController controller;

	@Override
	public JComponent makePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (ReportAction action : controller.actions) {
			panel.add(action.getDecoratedPanel());
		}
		return panel;
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
