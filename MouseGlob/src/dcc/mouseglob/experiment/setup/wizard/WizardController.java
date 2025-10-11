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
package dcc.mouseglob.experiment.setup.wizard;

import dcc.ui.Action;

@SuppressWarnings("serial")
class WizardController {

	private WizardUI ui;
	private WizardModel model;

	WizardController() {
		model = new WizardModel();
	}

	void setWizard(WizardUI wizard) {
		this.ui = wizard;
	}

	void registerWizardPanel(WizardPanel panel) {
		ui.registerWizardPanel(panel);
		model.registerWizardPanel(panel);
		if (panel instanceof DefaultWizardPanel)
			((DefaultWizardPanel) panel).setController(this);
	}

	void setCurrentPanel(Object id) {
		WizardPanel oldPanel = model.getCurrentPanel();
		if (oldPanel != null)
			oldPanel.aboutToHidePanel();
		model.setCurrentPanel(id);

		WizardPanel panel = model.getCurrentPanel();

		backAction.setEnabled(panel.isBackEnabled());
		nextAction.setEnabled(panel.isNextEnabled());
		ui.setFinishActive(panel.getNextId().equals(WizardPanel.FINISH));

		panel.aboutToDisplayPanel();
		ui.setCurrentPanel(id);
		panel.displayingPanel();
	}

	void updateNavigationButtons() {
		WizardPanel panel = model.getCurrentPanel();
		backAction.setEnabled(panel.isBackEnabled());
		nextAction.setEnabled(panel.isNextEnabled());
	}

	final Action backAction = new Action("Back") {
		@Override
		public void actionPerformed() {
			setCurrentPanel(model.getCurrentPanel().getBackId());
		}
	};

	final Action nextAction = new Action("Next") {
		@Override
		public void actionPerformed() {
			setCurrentPanel(model.getCurrentPanel().getNextId());
		}
	};

	final Action finishAction = new Action("Finish") {
		@Override
		public void actionPerformed() {
			System.out.println("Success");
			ui.hide();
		}
	};

	final Action cancelAction = new Action("Cancel") {
		@Override
		public void actionPerformed() {
			ui.hide();
		}
	};

}
