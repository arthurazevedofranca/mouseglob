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

import dcc.event.Event;

public class ExperimentEvent extends Event {

	public enum ExperimentEventType {
		NEW, OPEN, LOAD, SAVE;
	}

	private final ExperimentEventType type;
	private final Experiment experiment;
	private final String fileName;

	public ExperimentEvent(ExperimentEventType type, Experiment experiment,
			String fileName) {
		this.type = type;
		this.experiment = experiment;
		this.fileName = fileName;
	}

	public ExperimentEventType getType() {
		return type;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public String getFileName() {
		return fileName;
	}

}
