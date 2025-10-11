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

import org.w3c.dom.Element;

import dcc.inject.Inject;
import dcc.mouseglob.analysis.AnalysesManager;
import dcc.mouseglob.calibration.CalibrationController;
import dcc.mouseglob.keyevent.KeyEventController;
import dcc.mouseglob.maze.io.MazeIOManager;
import dcc.mouseglob.movie.MovieController;
import dcc.mouseglob.tracking.TrackingController;
import dcc.mouseglob.trajectory.TrajectoriesIOManager;
import dcc.mouseglob.trajectory.TrajectoriesXMLCodec;
import dcc.xml.XMLDecoder;
import dcc.xml.XMLNotFoundException;

/**
 * @author Daniel Coelho de Castro
 */
final class ExperimentXMLEvaluator extends XMLDecoder {

	private final String fileName;
	private Experiment experiment;

	private final TrackingController trackingController;
	private final MazeIOManager mazeIOManager;
	private final MovieController movieController;
	private final CalibrationController calibrationController;
	private final TrajectoriesIOManager trajectoriesIOManager;
	private final AnalysesManager analysesManager;
	private final KeyEventController keyEventController;

	@Inject
	ExperimentXMLEvaluator(String fileName,
			TrackingController trackingController, MazeIOManager mazeIOManager,
			MovieController movieController,
			CalibrationController calibrationController,
			TrajectoriesIOManager trajectoriesIOManager,
			AnalysesManager analysesManager,
			KeyEventController keyEventController) {

		this.fileName = fileName;
		this.trackingController = trackingController;
		this.mazeIOManager = mazeIOManager;
		this.movieController = movieController;
		this.calibrationController = calibrationController;
		this.trajectoriesIOManager = trajectoriesIOManager;
		this.analysesManager = analysesManager;
		this.keyEventController = keyEventController;
	}

	@Override
	public void decode(Element root) {
		experiment = new Experiment(fileName);

		decodeChild(root, mazeIOManager, true);
		decodeChild(root, analysesManager, true);
		decodeChild(root, trackingController);
		decodeChild(root, movieController, true);
		decodeChild(root, calibrationController, true);

		try {
			Element trajectoriesElement = getChild(root, "trajectories");
			new TrajectoriesXMLCodec(this, trajectoriesIOManager)
					.decode(trajectoriesElement);
		} catch (XMLNotFoundException e) {
		}

		decodeChild(root, keyEventController, true);
	}

	Experiment getExperiment() {
		return experiment;
	}

}
