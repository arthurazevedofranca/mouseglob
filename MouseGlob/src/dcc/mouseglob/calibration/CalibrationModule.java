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
package dcc.mouseglob.calibration;

import dcc.inject.Inject;
import dcc.module.AbstractModule;
import dcc.module.ModuleNotInitializedException;

public class CalibrationModule extends
		AbstractModule<Calibration, CalibrationView, CalibrationController> {
	private static CalibrationModule instance;

	@Inject
	private CalibrationModule(Calibration model, CalibrationView view,
			CalibrationController controller) {
		super(model, view, controller);

		instance = this;
	}

	public static CalibrationModule getInstance()
			throws ModuleNotInitializedException {
		if (instance == null)
			throw new ModuleNotInitializedException("Calilbration");
		return instance;
	}
}
