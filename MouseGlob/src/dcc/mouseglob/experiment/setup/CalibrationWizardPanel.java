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
package dcc.mouseglob.experiment.setup;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.calibration.CalibrationController;
import dcc.mouseglob.calibration.CalibrationListener;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.calibration.CalibrationView;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.movie.MovieUI;

public class CalibrationWizardPanel extends DefaultMovieAppletWizardPanel
		implements CalibrationListener {

	static final String ID = "calibration";

	private CalibrationView calibrationView;

	@Inject
	public CalibrationWizardPanel(MovieManager movieManager, MovieUI movieUI,
			Calibration calibrationModel,
			CalibrationController calibrationController,
			CalibrationView calibrationView) {
		super(ID, ChooseVideoWizardPanel.ID, MazeSetupWizardPanel.ID,
				movieManager, movieUI);
		this.calibrationView = calibrationView;

		MouseGlobApplet applet = getApplet();
		applet.addPaintable(calibrationModel);
		applet.addMouseListener(calibrationController);
		calibrationController.addCursorListener(applet);

		calibrationModel.addCalibratrionListener(this);

		// setNextEnabled(false);
	}

	@Override
	public JPanel getComponent() {
		JPanel panel = super.getComponent();
		panel.add(calibrationView.makeToolBar(), BorderLayout.NORTH);
		return panel;
	}

	@Override
	public void onCalibrationSet(double scale) {
		setNextEnabled(true);
	}

}
