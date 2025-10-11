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
package dcc.mouseglob.trajectory;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import processing.core.PConstants;
import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.FileType;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.experiment.Experiment;
import dcc.mouseglob.experiment.ExperimentIOManager;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.tracking.TrackingManager;

public class TrajectoriesIOManager implements NewFrameListener, Paintable {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"YYYYMMdd_HHmmss");

	private String trajectoriesName;
	private TrajectoryReader reader;
	private TrajectoryWriter writer;
	private boolean isRecording;

	@Inject
	private ExperimentIOManager experimentIOManager;
	@Inject
	private TrackingManager trackingManager;
	@Inject
	private ZonesManager zonesManager;

	private int width, height;

	TrajectoriesIOManager() {
		writer = null;
		isRecording = false;
	}

	void startRecording() throws FileNotFoundException {
		writer = new TrajectoryWriter(getTrajectoriesFileName(),
				trackingManager, zonesManager);
		writer.writeHeader(width, height);
		isRecording = true;
	}

	void stopRecording() {
		isRecording = false;
		if (writer != null)
			writer.close();
	}

	String getTrajectoriesFileName() {
		if (trajectoriesName == null) {
			Experiment experiment = experimentIOManager.getCurrentExperiment();
			String experimentName = experiment.getExperimentFileName();
			if (experimentName == null)
				experimentName = DATE_FORMAT.format(new Date());
			trajectoriesName = FileType.TRAJECTORIES_FILE
					.replaceExtension(experimentName);
		}
		return trajectoriesName;
	}

	void setTrajectoriesFileName(String fileName) {
		trajectoriesName = fileName;
	}

	@Override
	public void newFrame(Image frame, long time) {
		width = frame.getWidth();
		height = frame.getHeight();
		if (isRecording)
			writer.writeLine(time);
	}

	void analyze(boolean b) throws FileNotFoundException {
		if (b) {
			reader = new TrajectoryReader(trajectoriesName);
			reader.parse();
		} else
			reader = null;
	}

	@Override
	public void paint(PGraphics g) {
		if (reader != null) {
			g.pushStyle();
			g.fill(255, 128);
			g.rectMode(PConstants.CORNERS);
			g.rect(0, 0, g.width, g.height);
			for (Trajectory t : reader.getTrajectories())
				t.paint(g);
			g.popStyle();
		}
	}
}
