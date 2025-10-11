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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.FileType;
import dcc.mouseglob.applet.CursorListener;
import dcc.mouseglob.applet.CursorListener.Cursor;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseEvent.Button;
import dcc.mouseglob.applet.MouseEvent.Type;
import dcc.mouseglob.applet.MouseListener;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.experiment.ExperimentIOManager;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.BoundaryMask;
import dcc.mouseglob.tracking.TrackingEvent.TrackingEventType;
import dcc.mouseglob.tracking.TrackingManager.ThresholdMode;
import dcc.mouseglob.tracking.TrackingManager.TrackingMode;
import dcc.ui.Action;
import dcc.ui.ButtonGroup;
import dcc.ui.ComponentFactory;
import dcc.ui.RadioAction;
import dcc.ui.SliderAction;
import dcc.ui.ToggleAction;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

/**
 * @author Daniel Coelho de Castro
 */
public final class TrackingController extends AbstractController<Tracking>
		implements TrackingListener, MouseListener, XMLEncodable {

	ToggleAction addTrackerAction;

	SliderAction thresholdAction;
	SliderAction trackerSizeAction;

	RadioAction lightAction;
	RadioAction darkAction;

	RadioAction normalAction;
	RadioAction differenceAction;

	Action resetAction;

	@Inject
	private TrackingManager manager;
	@Inject
	private BoundariesManager boundariesManager;
	@Inject
	private Calibration calibration;
	@Inject
	private ExperimentIOManager experimentIOManager;

	private List<CursorListener> cursorListeners;

	TrackingController() {
		addTrackerAction = new AddTrackerAction();

		thresholdAction = new ThresholdAction();
		trackerSizeAction = new TrackerSizeAction();

		lightAction = new LightAction();
		darkAction = new DarkAction();

		normalAction = new NormalAction();
		differenceAction = new DifferenceAction();

		resetAction = new ResetAction();

		new ButtonGroup(lightAction, darkAction);
		lightAction.setSelected(true);

		new ButtonGroup(normalAction, differenceAction);
		normalAction.setSelected(true);

		cursorListeners = new ArrayList<CursorListener>();
	}

	/**
	 * Sets the value of the <code>TrackingManager</code> threshold and the
	 * corresponding slider.
	 * 
	 * @param value
	 *            - the new value (<code>0 <= value <= 256</code>)
	 */
	public void setThreshold(int value) {
		manager.setThreshold(value);
		thresholdAction.setValue(value);
	}

	/**
	 * Sets the size of the <code>TrackingManager</code> trackers and the
	 * corresponding slider.
	 * 
	 * @param value
	 *            - the new size (<code>0 <= value <= 100</code>)
	 */
	public void setTrackerSize(int value) {
		manager.setTrackerSize(value);
		trackerSizeAction.setValue(value);
	}

	public void setThresholdMode(String value) {
		ThresholdMode mode = ThresholdMode.get(value);
		manager.setThresholdMode(mode);
		if (mode == ThresholdMode.LIGHT)
			lightAction.setSelected(true);
		if (mode == ThresholdMode.DARK)
			darkAction.setSelected(true);
	}

	public void setTrackingMode(String value) {
		TrackingMode mode = TrackingMode.get(value);
		manager.setTrackingMode(mode);
		if (mode == TrackingMode.NORMAL)
			normalAction.setSelected(true);
		if (mode == TrackingMode.DIFFERENCE)
			differenceAction.setSelected(true);
	}

	private void addTracker(int x, int y) {
		BoundaryMask mask = boundariesManager.getMask(x, y);
		Tracker tracker = new Tracker(x, y, manager.getTrackerSize(), mask);
		manager.add(tracker);
	}

	String getBackgroundFileName() {
		String experimentFileName = experimentIOManager.getCurrentExperiment()
				.getExperimentFileName();
		return FileType.removeExtension(experimentFileName) + "_bg.png";
	}

	@SuppressWarnings("serial")
	private class AddTrackerAction extends ToggleAction {
		public AddTrackerAction() {
			super("Add Tracker", ComponentFactory
					.getIcon("/resource/addTracker16.png"));
		}

		@Override
		public void itemStateChanged(boolean state) {
			if (calibration.hasScale()) {
				manager.isAddingTracker = state;
				setCursor(state ? Cursor.CROSS : Cursor.ARROW);
			} else if (state) {
				JOptionPane.showMessageDialog(null,
						"Must calibrate before adding a tracker.", "Warning",
						JOptionPane.ERROR_MESSAGE);
				setSelected(false);
			}
		}
	}

	private class ThresholdAction extends SliderAction {
		public ThresholdAction() {
			super("Threshold", 0, 256, TrackingManager.DEFAULT_THRESHOLD);
		}

		@Override
		public void valueChanged(int value) {
			manager.setThreshold(value);
		}
	}

	private class TrackerSizeAction extends SliderAction {
		public TrackerSizeAction() {
			super("Size", 0, 100, TrackingManager.DEFAULT_TRACKER_SIZE);
		}

		@Override
		public void valueChanged(int value) {
			manager.setTrackerSize(value);
		}
	}

	@SuppressWarnings("serial")
	private class LightAction extends RadioAction {
		public LightAction() {
			super("Light");
			setDescription("Light Threshold Mode");
		}

		@Override
		public void actionPerformed() {
			if (manager != null)
				manager.setThresholdMode(ThresholdMode.LIGHT);
		}
	}

	@SuppressWarnings("serial")
	private class DarkAction extends RadioAction {
		public DarkAction() {
			super("Dark");
			setDescription("Dark Threshold Mode");
		}

		@Override
		public void actionPerformed() {
			if (manager != null)
				manager.setThresholdMode(ThresholdMode.DARK);
		}
	}

	@SuppressWarnings("serial")
	private class NormalAction extends RadioAction {
		public NormalAction() {
			super("Normal");
			setDescription("Normal Tracking Mode");
		}

		@Override
		public void actionPerformed() {
			if (manager != null)
				manager.setTrackingMode(TrackingMode.NORMAL);
			resetAction.setEnabled(false);
			lightAction.setEnabled(true);
			darkAction.setEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class DifferenceAction extends RadioAction {
		public DifferenceAction() {
			super("Difference");
			setDescription("Difference Tracking Mode");
		}

		@Override
		public void actionPerformed() {
			if (manager != null)
				manager.setTrackingMode(TrackingMode.DIFFERENCE);
			lightAction.setSelected(true);
			resetAction.setEnabled(true);
			lightAction.setEnabled(false);
			darkAction.setEnabled(false);
		}
	}

	@SuppressWarnings("serial")
	private class ResetAction extends Action {
		public ResetAction() {
			super("Reset");
			setDescription("Reset Difference Background");
		}

		@Override
		public void actionPerformed() {
			manager.resetBackground();
		}
	}

	@Override
	public void onTrackingEvent(TrackingEvent event) {
		if (event.getType() == TrackingEventType.TRACKER_ADDED)
			addTrackerAction.setSelected(false);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if (event.getType() == Type.CLICKED) {
			if (event.getButton() == Button.LEFT && manager.isAddingTracker) {
				addTracker(event.getMouseX(), event.getMouseY());
				return true;
			}
		}
		return false;
	}

	public void addCursorListener(CursorListener listener) {
		cursorListeners.add(listener);
	}

	private void setCursor(Cursor cursor) {
		for (CursorListener listener : cursorListeners)
			listener.setCursor(cursor);
	}

	@Override
	public String getTagName() {
		return "tracking";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new TrackingXMLEncoder(processor, manager, this);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new TrackingXMLDecoder(manager, this, boundariesManager);
	}

}
