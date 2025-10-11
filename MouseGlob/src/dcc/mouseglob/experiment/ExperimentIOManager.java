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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dcc.inject.Context;
import dcc.mouseglob.experiment.ExperimentEvent.ExperimentEventType;
import dcc.tree.Tree;
import dcc.xml.XMLReader;
import dcc.xml.XMLWriter;

public final class ExperimentIOManager {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExperimentIOManager.class);
	private Experiment currentExperiment;
	private List<ExperimentListener> listeners;

	ExperimentIOManager() {
		currentExperiment = new Experiment(null);
		listeners = new ArrayList<ExperimentListener>();
	}

	public Experiment getCurrentExperiment() {
		return currentExperiment;
	}

	void newExperiment() {
		currentExperiment = new Experiment(null);
		dcc.util.Logs.setExperimentId("unsaved");
		notifyListeners(new ExperimentEvent(ExperimentEventType.NEW,
				currentExperiment, null));
		log.info("New experiment created (unsaved)");
	}

	void saveExperiment() throws FileNotFoundException {
		log.info("Saving experiment...");
		String experimentName = currentExperiment.getExperimentFileName();
		Context context = Context.getGlobal();
		ExperimentXMLBuilder builder = context.getInstance(
				ExperimentXMLBuilder.class, experimentName);
		XMLWriter writer = new XMLWriter(experimentName);
		writer.write(builder);

		notifyListeners(new ExperimentEvent(ExperimentEventType.SAVE,
				currentExperiment, experimentName));
	}

	void saveExperimentAs(String fileName) throws FileNotFoundException {
		log.info("Saving experiment as: {}", fileName);
		currentExperiment.setExperimentFileName(fileName);
		saveExperiment();
	}

	void openExperiment(String fileName) throws IOException {
		notifyListeners(new ExperimentEvent(ExperimentEventType.OPEN,
				currentExperiment, fileName));
		Context context = Context.getGlobal();
		ExperimentXMLEvaluator evaluator = context.getInstance(
				ExperimentXMLEvaluator.class, fileName);
		XMLReader reader = new XMLReader(fileName);
		reader.read(evaluator);

		currentExperiment = evaluator.getExperiment();
		log.info("Opened experiment: {}", currentExperiment.getName());
		dcc.util.Logs.setExperimentId(currentExperiment.getName());
		currentExperiment.updateStructure();
		Tree.getManager().setRoot(currentExperiment);

		notifyListeners(new ExperimentEvent(ExperimentEventType.LOAD,
				currentExperiment, fileName));
	}

	public void addExperimentListener(ExperimentListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(ExperimentEvent event) {
		for (ExperimentListener listener : listeners)
			listener.onExperimentEvent(event);
	}

}
