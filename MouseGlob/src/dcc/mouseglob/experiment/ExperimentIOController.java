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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.KeyStroke;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.FileType;
import dcc.mouseglob.ui.FileChooser;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;

public final class ExperimentIOController extends
		AbstractController<ExperimentIO> {
	Action newExperimentAction;
	Action openExperimentAction;
	Action saveExperimentAction;
	Action saveExperimentAsAction;

	private FileChooser fileChooser;

	@Inject
	private ExperimentIOManager manager;
	@Inject
	private ExperimentIOView view;

	ExperimentIOController() {
		newExperimentAction = new NewExperimentAction();
		openExperimentAction = new OpenExperimentAction();
		saveExperimentAction = new SaveExperimentAction();
		saveExperimentAction.setEnabled(false);
		saveExperimentAsAction = new SaveExperimentAsAction();

		fileChooser = new FileChooser(FileType.EXPERIMENT_FILE, "experiment");
	}

	@SuppressWarnings("serial")
	private class NewExperimentAction extends Action {
		public NewExperimentAction() {
			super("New Experiment", ComponentFactory.getIcon("general/New16"),
					KeyStroke
							.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed() {
			manager.newExperiment();
		}
	}

	@SuppressWarnings("serial")
	private class OpenExperimentAction extends Action {
		public OpenExperimentAction() {
			super("Open Experiment...", ComponentFactory
					.getIcon("general/Open16"), KeyStroke.getKeyStroke(
					KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed() {
			String fileName = fileChooser.open();
			if (fileName != null) {
				try {
					manager.openExperiment(fileName);
					saveExperimentAction.setEnabled(true);
				} catch (IOException e) {
					view.showErrorDialog("Error opening experiment",
							e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("serial")
	private class SaveExperimentAction extends Action {
		public SaveExperimentAction() {
			super("Save Experiment",
					ComponentFactory.getIcon("general/Save16"), KeyStroke
							.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed() {
			try {
				manager.saveExperiment();
			} catch (FileNotFoundException e) {
				view.showErrorDialog("Error saving experiment", e.getMessage());
			}
		}
	}

	@SuppressWarnings("serial")
	private class SaveExperimentAsAction extends Action {
		public SaveExperimentAsAction() {
			super("Save Experiment As...", ComponentFactory
					.getIcon("general/SaveAs16"));
		}

		@Override
		public void actionPerformed() {
			System.out
					.println("ExperimentIOController$SaveExperimentAsAction.actionPerformed()");

			String currentName = manager.getCurrentExperiment().getName();
			String fileName = fileChooser.save(currentName);
			if (fileName != null) {
				try {
					manager.saveExperimentAs(fileName);
					saveExperimentAction.setEnabled(true);
				} catch (FileNotFoundException e) {
					view.showErrorDialog("Error saving experiment as \""
							+ fileName + "\"", e.getMessage());
				}
			}
		}
	}

}
