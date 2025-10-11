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

import dcc.module.AbstractController;
import dcc.mouseglob.FileType;
import dcc.mouseglob.experiment.ExperimentEvent;
import dcc.mouseglob.experiment.ExperimentEvent.ExperimentEventType;
import dcc.mouseglob.experiment.ExperimentListener;
import dcc.mouseglob.ui.FileChooser;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;

public class MazeIOController extends AbstractController<MazeIO> implements
		ExperimentListener {
	Action openMazeAction;
	Action saveMazeAction;
	Action saveMazeAsAction;
	Action exportZonesAction;

	private FileChooser fileChooser;
	private FileChooser csvFileChooser;

	private MazeIOManager manager;
	private MazeIOUI ui;

	MazeIOController() {
		openMazeAction = new OpenMazeAction();
		saveMazeAction = new SaveMazeAction();
		saveMazeAsAction = new SaveMazeAsAction();
		exportZonesAction = new ExportZonesAction();

		saveMazeAction.setEnabled(false);

		fileChooser = new FileChooser(FileType.MAZE_FILE, "maze");
		csvFileChooser = new FileChooser(FileType.MAZE_CSV_FILE, "maze");
	}

	void setManager(MazeIOManager manager) {
		this.manager = manager;
	}

	void setUI(MazeIOUI ui) {
		this.ui = ui;
	}

	@SuppressWarnings("serial")
	private class OpenMazeAction extends Action {
		public OpenMazeAction() {
			super("Open Maze...", ComponentFactory.getIcon("general/Open16"));
		}

		@Override
		public void actionPerformed() {
			String fileName = fileChooser.open();
			if (fileName != null) {
				manager.load(fileName);
				saveMazeAction.setEnabled(true);
			}
		}
	}

	@SuppressWarnings("serial")
	private class SaveMazeAction extends Action {
		public SaveMazeAction() {
			super("Save Maze", ComponentFactory.getIcon("general/Save16"));
		}

		@Override
		public void actionPerformed() {
			try {
				manager.save();
			} catch (FileNotFoundException e) {
				ui.showErrorDialog("Error saving maze", e.getMessage());
			}
		}
	}

	@SuppressWarnings("serial")
	private class SaveMazeAsAction extends Action {
		public SaveMazeAsAction() {
			super("Save Maze As...", ComponentFactory
					.getIcon("general/SaveAs16"));
		}

		@Override
		public void actionPerformed() {
			String fileName = fileChooser.save();
			if (fileName != null) {
				try {
					manager.saveAs(fileName);
					saveMazeAction.setEnabled(true);
				} catch (FileNotFoundException e) {
					ui.showErrorDialog("Error saving maze as \"" + fileName
							+ "\"", e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("serial")
	private class ExportZonesAction extends Action {
		public ExportZonesAction() {
			super("Export Maze As CSV...", ComponentFactory
					.getIcon("general/Export16"));
		}

		@Override
		public void actionPerformed() {
			String fileName = csvFileChooser.save(manager
					.getSuggestedCSVFileName());
			if (fileName != null) {
				try {
					manager.export(fileName);
				} catch (FileNotFoundException e) {
					ui.showErrorDialog("Error exporting maze as \"" + fileName
							+ "\"", e.getMessage());
				}
			}
		}
	}

	@Override
	public void onExperimentEvent(ExperimentEvent event) {
		if (event.getType() == ExperimentEventType.SAVE) {
			try {
				manager.save();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
