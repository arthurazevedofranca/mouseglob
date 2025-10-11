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

import java.awt.Frame;

public class Wizard {

	private final WizardController controller;
	private final WizardUI ui;

	public Wizard(Frame owner, String title) {
		controller = new WizardController();
		ui = new WizardUI(owner, title, controller);
	}

	public void registerWizardPanel(WizardPanel wizardPanel) {
		controller.registerWizardPanel(wizardPanel);
	}

	public void registerWizardPanels(WizardPanel... wizardPanels) {
		for (WizardPanel wizardPanel : wizardPanels)
			registerWizardPanel(wizardPanel);
	}

	public void setCurrentPanel(Object id) {
		controller.setCurrentPanel(id);
	}

	public void show() {
		ui.show();
	}

	public void hide() {
		ui.hide();
	}

}
