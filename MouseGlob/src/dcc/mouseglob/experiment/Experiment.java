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

import java.io.File;

import dcc.inject.Context;
import dcc.mouseglob.FileType;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.keyevent.KeyEventManager;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.tracking.TrackingManager;
import dcc.mouseglob.visit.VisitEventManager;
import dcc.tree.DefaultTreeable;
import dcc.tree.EditableTreeable;
import dcc.tree.TreeNode;

public final class Experiment extends DefaultTreeable implements
		EditableTreeable {

	private File experimentDirectory;
	private String experimentName;

	final TreeNode node;

	Experiment(String experimentFileName) {
		super("Experiment", "/resource/experiment16.png");
		node = getNode();
		setExperimentFileName(experimentFileName);
	}

	void setExperimentFileName(String experimentFileName) {
		if (experimentFileName != null) {
			File experimentFile = new File(experimentFileName);
			experimentDirectory = experimentFile.getParentFile();
			experimentName = FileType.removeExtension(experimentFile.getName());
			node.setName(experimentName);
		}
	}

	public String getExperimentFileName() {
		if (experimentDirectory == null)
			return null;
		return experimentDirectory.getPath() + File.separator
				+ FileType.EXPERIMENT_FILE.appendExtension(experimentName);
	}

	public String getName() {
		return experimentName;
	}

	@Override
	public void setName(String experimentName) {
		this.experimentName = experimentName;
	}

	public void updateStructure() {
		node.removeAllChildren();

		Context context = Context.getGlobal();
		node.add(context.getInstance(TrackingManager.class));
		node.add(context.getInstance(ZonesManager.class));
		node.add(context.getInstance(BoundariesManager.class));
		node.add(context.getInstance(Calibration.class));
		node.add(context.getInstance(KeyEventManager.class));
		node.add(context.getInstance(VisitEventManager.class));
	}

}
