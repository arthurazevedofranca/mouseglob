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

import java.util.HashMap;
import java.util.Map;

class WizardModel {

	private Map<Object, WizardPanel> panels;
	private WizardPanel current;

	WizardModel() {
		panels = new HashMap<Object, WizardPanel>();
	}

	WizardPanel getCurrentPanel() {
		return current;
	}

	void setCurrentPanel(Object id) {
		current = panels.get(id);
	}

	void registerWizardPanel(WizardPanel panel) {
		if (current == null)
			current = panel;
		panels.put(panel.getId(), panel);
	}

}
