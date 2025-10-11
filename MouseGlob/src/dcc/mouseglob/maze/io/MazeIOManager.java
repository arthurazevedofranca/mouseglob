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
package dcc.mouseglob.maze.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import dcc.inject.Inject;
import dcc.mouseglob.FileType;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.experiment.ExperimentIOManager;
import dcc.mouseglob.maze.BoundariesController;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesController;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.ui.StatusUI;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;
import dcc.xml.XMLReader;
import dcc.xml.XMLWriter;

public class MazeIOManager implements XMLEncodable {

	@Inject
	private ExperimentIOManager experimentIOManager;
	@Inject
	private ZonesManager zonesManager;
	@Inject
	private BoundariesManager boundariesManager;
	@Inject
	private Calibration calibrationModel;

	@Inject
	private ZonesController zonesController;
	@Inject
	private BoundariesController boundariesController;

	String fileName;

	String getFileName() {
		if (fileName == null) {
			String experimentName = experimentIOManager.getCurrentExperiment()
					.getExperimentFileName();
			fileName = FileType.MAZE_FILE.replaceExtension(experimentName);
		}
		return fileName;
	}

	boolean isEmpty() {
		return zonesManager.isEmpty() && boundariesManager.isEmpty();
	}

	void load(String fileName) {
		try {
			this.fileName = fileName;
			XMLReader reader = new XMLReader(fileName);
			MazeXMLEvaluator evaluator = new MazeXMLEvaluator(zonesController,
					boundariesController);
			reader.read(evaluator);
		} catch (IOException e) {
			e.printStackTrace();
			StatusUI.setStatusText("Invalid file.");
		}
	}

	void save() throws FileNotFoundException {
		MazeXMLBuilder builder = new MazeXMLBuilder(zonesManager,
				boundariesManager);
		XMLWriter writer = new XMLWriter(getFileName());
		writer.write(builder);
	}

	void saveAs(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		save();
	}

	String getSuggestedCSVFileName() {
		return fileName != null ? FileType.MAZE_CSV_FILE
				.appendExtension(fileName) : null;
	}

	void export(String csvFileName) throws FileNotFoundException {
		MazeCSVWriter writer = new MazeCSVWriter(csvFileName);
		writer.setZones(zonesManager.getZones());
		writer.setBoundaries(boundariesManager.getBoundaries());
		writer.setScale(calibrationModel.getScale());
		writer.writeHeader(boundariesManager.getWidth(),
				boundariesManager.getHeight());
		writer.writeFile();
		writer.close();
	}

	@Override
	public String getTagName() {
		return "maze";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new MazeFileXMLCodec(processor, this);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new MazeFileXMLCodec(processor, this);
	}

}
