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

import java.awt.Component;

public interface WizardPanel {

	public static final Object FINISH = "FINISH";

	Component getComponent();

	Object getId();

	Object getNextId();

	Object getBackId();

	void aboutToDisplayPanel();

	void displayingPanel();

	void aboutToHidePanel();

	boolean isBackEnabled();

	boolean isNextEnabled();

}
