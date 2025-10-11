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

import dcc.inject.Context;
import dcc.inject.Indexer;
import dcc.mouseglob.experiment.setup.wizard.Wizard;
import dcc.tree.Tree;

public class WizardTest {

	public static void main(String[] args) {
		Context globalContext = Context.getGlobal();
		globalContext.inject(Indexer.load("/dcc/mouseglob/classes.txt"));
		System.out.println(globalContext);

		Context wizardContext = new Context(globalContext);
		wizardContext.putInstance(new Tree(null));

		ExperimentNameWizardPanel experimentName = new ExperimentNameWizardPanel();
		ChooseVideoWizardPanel chooseVideo = wizardContext
				.getInstance(ChooseVideoWizardPanel.class);
		CalibrationWizardPanel calibrationPanel = wizardContext
				.getInstance(CalibrationWizardPanel.class);
		MazeSetupWizardPanel mazeSetup = wizardContext
				.getInstance(MazeSetupWizardPanel.class);
		TrackingSetupWizardPanel trackingSetup = wizardContext
				.getInstance(TrackingSetupWizardPanel.class);

		Wizard wizard = new Wizard(null, "Experiment Setup");
		wizard.registerWizardPanels(experimentName, chooseVideo,
				calibrationPanel, mazeSetup, trackingSetup);
		wizard.show();
	}

}
