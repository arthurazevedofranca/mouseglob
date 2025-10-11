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
package dcc.mouseglob.calibration;

import java.util.ArrayList;
import java.util.Collection;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.applet.CursorListener;
import dcc.mouseglob.applet.CursorListener.Cursor;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseListener;
import dcc.mouseglob.experiment.ExperimentEvent;
import dcc.mouseglob.experiment.ExperimentEvent.ExperimentEventType;
import dcc.mouseglob.experiment.ExperimentListener;
import dcc.mouseglob.movie.MovieEvent;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;
import dcc.mouseglob.movie.MovieListener;
import dcc.ui.ComponentFactory;
import dcc.ui.TextAction;
import dcc.ui.ToggleAction;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

// TODO Use formatted text fields?
public class CalibrationController extends
		AbstractController<CalibrationModule> implements MouseListener,
		MovieListener, ExperimentListener, CalibrationListener, XMLEncodable {

	private final Calibration model;
	private final CalibrationView view;

	private final Collection<CursorListener> cursorListeners;

	ToggleAction calibrateAction = new CalibrateAction();
	TextAction centimetersAction = new CentimetersAction();
	TextAction pixelsAction = new PixelsAction();

	@Inject
	CalibrationController(Calibration model, CalibrationView view) {
		this.model = model;
		this.view = view;
		model.addCalibratrionListener(this);

		calibrateAction = new CalibrateAction();
		centimetersAction = new CentimetersAction();
		pixelsAction = new PixelsAction();

		cursorListeners = new ArrayList<CursorListener>();
	}

	void setScaleValue(double value) {
		model.setScale(value);
		view.setScaleValue(value);
	}

	void reset() {
		model.reset();
	}

	private void setCalibrationEnabled(boolean b) {
		calibrateAction.setEnabled(b);
		pixelsAction.setEnabled(b);
		centimetersAction.setEnabled(b);
	}

	@Override
	public void onCalibrationSet(double scale) {
		view.setScaleValue(scale);
	}

	@SuppressWarnings("serial")
	private class CalibrateAction extends ToggleAction {
		public CalibrateAction() {
			super("Calibrate", ComponentFactory
					.getIcon("/resource/calibrate16.png"));
		}

		@Override
		public void itemStateChanged(boolean state) {
			model.clearPoints();
			setCursor(state ? Cursor.CROSS : Cursor.ARROW);
		}
	}

	@SuppressWarnings("serial")
	private class PixelsAction extends TextAction {
		public PixelsAction() {
			super("Pixels");
		}

		@Override
		public void textChanged(String text) {
			if (!text.isEmpty())
				model.setPx(Double.parseDouble(text));
		}
	}

	@SuppressWarnings("serial")
	private class CentimetersAction extends TextAction {
		public CentimetersAction() {
			super("Centimeters");
		}

		@Override
		public void textChanged(String text) {
			if (!text.isEmpty())
				model.setCm(Double.parseDouble(text));
		}
	}

	private boolean setP1(int x, int y) {
		model.setP1(x, y);
		model.setTemporary(x, y);
		return true;
	}

	private boolean setP2(int x, int y) {
		model.setP2(x, y);
		calibrateAction.setSelected(false);
		pixelsAction.setText(String.format("%.2f", model.getPx()));
		return true;
	}

	private void update(int x, int y) {
		model.setTemporary(x, y);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if (!calibrateAction.isSelected())
			return false;
		int x = event.getMouseX(), y = event.getMouseY();
		switch (event.getType()) {
		case PRESSED:
			return setP1(x, y);
		case RELEASED:
			return setP2(x, y);
		case DRAGGED:
			update(x, y);
			return false;
		default:
			return false;
		}
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		if (event.getType() == MovieEventType.PLAY)
			setCalibrationEnabled(false);
		else if (event.getType() == MovieEventType.PAUSE)
			setCalibrationEnabled(true);
	}

	@Override
	public void onExperimentEvent(ExperimentEvent event) {
		if (event.getType() == ExperimentEventType.NEW)
			reset();
	}

	public void addCursorListener(CursorListener cursorListener) {
		cursorListeners.add(cursorListener);
	}

	private void setCursor(Cursor cursor) {
		for (CursorListener cursorListener : cursorListeners)
			cursorListener.setCursor(cursor);
	}

	@Override
	public String getTagName() {
		return "calibration";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new CalibrationXMLEncoder(processor, model);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new CalibrationXMLDecoder(this);
	}

}
