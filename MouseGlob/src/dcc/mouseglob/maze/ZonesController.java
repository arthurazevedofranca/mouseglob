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
package dcc.mouseglob.maze;

import java.util.List;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.experiment.ExperimentEvent;
import dcc.mouseglob.experiment.ExperimentEvent.ExperimentEventType;
import dcc.mouseglob.experiment.ExperimentListener;
import dcc.mouseglob.movie.MovieEvent;
import dcc.mouseglob.movie.MovieListener;
import dcc.mouseglob.shape.CircleDrawer;
import dcc.mouseglob.shape.PolygonDrawer;
import dcc.mouseglob.shape.Shape;
import dcc.mouseglob.shape.ShapeListener;
import dcc.tree.TreeSelectionListener;
import dcc.tree.Treeable;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;
import dcc.ui.ToggleAction;

/**
 * @author Daniel Coelho de Castro
 */
public final class ZonesController extends AbstractController<Zones> implements
		MovieListener, ExperimentListener, TreeSelectionListener {
	Action drawPolygonAction;
	Action drawCircleAction;
	Action clearZonesAction;

	@Inject
	private ZonesManager zonesManager;
	@Inject
	private BoundariesController boundariesController;

	ZonesController() {
		drawPolygonAction = new DrawPolygonAction(PolygonDrawer.getInstance());
		drawCircleAction = new DrawCircleAction(CircleDrawer.getInstance());
		clearZonesAction = new ClearZonesAction();
		clearZonesAction.setEnabled(false);
	}

	public void add(Zone zone) {
		zonesManager.add(zone);

		clearZonesAction.setEnabled(true);
	}

	void setDrawingControlsEnabled(boolean b) {
		drawPolygonAction.setEnabled(b);
		drawCircleAction.setEnabled(b);
		clearZonesAction.setEnabled(b);
	}

	@SuppressWarnings("serial")
	private class DrawPolygonAction extends ToggleAction implements
			ShapeListener {
		private PolygonDrawer polygonDrawer;

		public DrawPolygonAction(PolygonDrawer polygonDrawer) {
			super("Draw Polygon Zone", ComponentFactory
					.getIcon("/resource/addPolygon16.png"));
			this.polygonDrawer = polygonDrawer;
		}

		@Override
		public void itemStateChanged(boolean state) {
			if (state)
				polygonDrawer.start(this);
			else
				polygonDrawer.finish();
			setDrawingControlsEnabled(!state);
			setEnabled(true);
			boundariesController.setDrawingControlsEnabled(!state);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(new Zone(shape));
			setDrawingControlsEnabled(true);
			boundariesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class DrawCircleAction extends Action implements ShapeListener {
		private CircleDrawer circleDrawer;

		public DrawCircleAction(CircleDrawer circleDrawer) {
			super("Draw Circle Zone", ComponentFactory
					.getIcon("/resource/addCircle16.png"));
			this.circleDrawer = circleDrawer;
		}

		@Override
		public void actionPerformed() {
			circleDrawer.start(this);
			setDrawingControlsEnabled(false);
			boundariesController.setDrawingControlsEnabled(false);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(new Zone(shape));
			setDrawingControlsEnabled(true);
			boundariesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class ClearZonesAction extends Action {
		public ClearZonesAction() {
			super("Clear Zones", ComponentFactory.getIcon("general/Delete16"));
		}

		@Override
		public void actionPerformed() {
			zonesManager.clear();
		}
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		switch (event.getType()) {
		case PLAY:
			setDrawingControlsEnabled(false);
			break;

		case PAUSE:
			setDrawingControlsEnabled(true);
			break;

		default:
		}
	}

	@Override
	public void onExperimentEvent(ExperimentEvent event) {
		if (event.getType() == ExperimentEventType.OPEN)
			zonesManager.clear();
	}

	@Override
	public void nodeSelected(Treeable object) {
		List<Zone> zones = zonesManager.getZones();
		synchronized (zones) {
			for (Zone z : zones)
				z.setSelected(false);
		}
		if (object instanceof Zone)
			((Zone) object).setSelected(true);
	}

	@Override
	public void nodeDoubleClicked(Treeable object) {
	}

}
