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

import javax.swing.JOptionPane;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.tracking.TrackingEvent;
import dcc.mouseglob.tracking.TrackingListener;
import dcc.ui.ToggleAction;

@SuppressWarnings("serial")
public class TrajectoriesIOController extends
		AbstractController<TrajectoriesIO> implements TrackingListener {

	@Inject
	private TrajectoriesIOManager manager;

	final ToggleAction recordAction = new ToggleAction("Record",
			"/resource/record16.png") {
		@Override
		public void itemStateChanged(boolean state) {
			if (state) {
				try {
					manager.startRecording();
				} catch (FileNotFoundException e) {
					recordAction.setSelected(false);
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Error creating trajectories file",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				manager.stopRecording();
			}
			analyzeAction.setEnabled(!state);
		}
	};

	final ToggleAction analyzeAction = new ToggleAction("Analyze",
			"/resource/analyze16.png") {
		@Override
		public void itemStateChanged(boolean state) {
			try {
				manager.analyze(state);
				recordAction.setEnabled(!state);
			} catch (FileNotFoundException e) {
				analyzeAction.setSelected(false);
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Error reading trajectories file",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	@Override
	public void onTrackingEvent(TrackingEvent event) {
		int trackerCount = event.getTrackerCount();

		switch (event.getType()) {
		case TRACKER_ADDED:
			if (trackerCount == 1)
				recordAction.setEnabled(true);
			break;

		case TRACKER_REMOVED:
			if (trackerCount == 0)
				recordAction.setEnabled(false);
			break;
		}

	}

}
