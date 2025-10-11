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
package dcc.mouseglob.tracking;

import dcc.mouseglob.inspector.EditablePropertyInspector;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.inspector.EditablePropertyInspector.EnumEditor;
import dcc.mouseglob.inspector.EditablePropertyInspector.SliderEditor;
import dcc.mouseglob.tracking.TrackingManager.ThresholdMode;
import dcc.mouseglob.tracking.TrackingManager.TrackingMode;

class TrackingInspector extends Inspector {

	protected TrackingInspector(final TrackingManager manager) {
		super("Tracking");

		add(new EditablePropertyInspector<Integer>("Threshold",
				new SliderEditor(0, 256, TrackingManager.DEFAULT_THRESHOLD)) {
			@Override
			protected void setValue(Integer value) {
				manager.setThreshold(value);
			}

			@Override
			protected Integer getValue() {
				return manager.getThreshold();
			}
		});

		add(new EditablePropertyInspector<ThresholdMode>("Threshold mode",
				EnumEditor.getEditor(ThresholdMode.class)) {
			@Override
			protected void setValue(ThresholdMode value) {
				manager.setThresholdMode(value);
			}

			@Override
			protected ThresholdMode getValue() {
				return manager.getThresholdMode();
			}
		});

		add(new EditablePropertyInspector<Integer>("Tracker size",
				new SliderEditor(0, 100, TrackingManager.DEFAULT_TRACKER_SIZE)) {
			@Override
			protected void setValue(Integer value) {
				manager.setTrackerSize(value);
			}

			@Override
			protected Integer getValue() {
				return manager.getTrackerSize();
			}
		});

		add(new EditablePropertyInspector<TrackingMode>("Tracking mode",
				EnumEditor.getEditor(TrackingMode.class)) {
			@Override
			protected void setValue(TrackingMode value) {
				manager.setTrackingMode(value);
			}

			@Override
			protected TrackingMode getValue() {
				return manager.getTrackingMode();
			}
		});
	}

}
