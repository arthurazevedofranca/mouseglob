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

import java.util.Date;

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
import dcc.xml.XMLBuilder;

/**
 * @author Daniel Coelho de Castro
 */
class ExperimentXMLBuilder extends XMLBuilder {

	private final String fileName;
	private final TrackingController trackingController;
	private final MovieController movieController;
	private final TrajectoriesIOManager trajectoriesIOManager;
	private final MazeIOManager mazeIOManager;
	private final CalibrationController calibrationController;
	private final AnalysesManager analysesManager;
	private final KeyEventController keyEventController;

	@Inject
	ExperimentXMLBuilder(String fileName,
			TrackingController trackingController,
			MovieController movieController,
			TrajectoriesIOManager trajectoriesIOManager,
			MazeIOManager mazeIOManager,
			CalibrationController calibrationController,
			AnalysesManager analysesManager,
			KeyEventController keyEventController) {

		this.fileName = fileName;
		this.trackingController = trackingController;
		this.movieController = movieController;
		this.trajectoriesIOManager = trajectoriesIOManager;
		this.mazeIOManager = mazeIOManager;
		this.calibrationController = calibrationController;
		this.analysesManager = analysesManager;
		this.keyEventController = keyEventController;
	}

	@Override
	public void encode(Element root) {
		setAttribute(root, "date", new Date());

		encodeChild(root, trackingController);
		encodeChild(root, movieController);
		root.appendChild(new TrajectoriesXMLCodec(this, trajectoriesIOManager)
				.encode(fileName));
		encodeChild(root, mazeIOManager);
		encodeChild(root, calibrationController);
		encodeChild(root, analysesManager);
		encodeChild(root, keyEventController);
	}

	@Override
	protected String getRootTagName() {
		return "Element";
	}

}
