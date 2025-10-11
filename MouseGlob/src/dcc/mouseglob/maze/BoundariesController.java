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

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.experiment.ExperimentEvent;
import dcc.mouseglob.experiment.ExperimentEvent.ExperimentEventType;
import dcc.mouseglob.experiment.ExperimentListener;
import dcc.mouseglob.maze.Boundary.BoundaryType;
import dcc.mouseglob.movie.MovieEvent;
import dcc.mouseglob.movie.MovieListener;
import dcc.mouseglob.shape.CircleDrawer;
import dcc.mouseglob.shape.PolygonDrawer;
import dcc.mouseglob.shape.Shape;
import dcc.mouseglob.shape.ShapeListener;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;
import dcc.ui.ToggleAction;

public class BoundariesController extends AbstractController<Boundaries>
		implements MovieListener, ExperimentListener {
	Action drawPositivePolygonAction;
	Action drawPositiveCircleAction;
	Action drawNegativePolygonAction;
	Action drawNegativeCircleAction;
	Action clearBoundariesAction;
	Action refreshMaskAction;

	@Inject
	private BoundariesManager boundariesManager;
	@Inject
	private ZonesController zonesController;

	BoundariesController() {
		PolygonDrawer polygonDrawer = PolygonDrawer.getInstance();
		CircleDrawer circleDrawer = CircleDrawer.getInstance();

		drawPositivePolygonAction = new DrawPositivePolygonAction(polygonDrawer);
		drawPositiveCircleAction = new DrawPositiveCircleAction(circleDrawer);

		drawNegativePolygonAction = new DrawNegativePolygonAction(polygonDrawer);
		drawNegativeCircleAction = new DrawNegativeCircleAction(circleDrawer);

		clearBoundariesAction = new ClearBoundariesAction();
		clearBoundariesAction.setEnabled(false);

		refreshMaskAction = new RefreshMaskAction();
	}

	public void add(Boundary boundary) {
		boundariesManager.add(boundary);
		clearBoundariesAction.setEnabled(true);
	}

	public void add(Shape shape, BoundaryType boundaryType) {
		add(new Boundary(shape, boundaryType));
	}

	public void clear() {
		boundariesManager.clear();
	}

	void setDrawingControlsEnabled(boolean b) {
		drawPositivePolygonAction.setEnabled(b);
		drawPositiveCircleAction.setEnabled(b);
		drawNegativePolygonAction.setEnabled(b);
		drawNegativeCircleAction.setEnabled(b);
		clearBoundariesAction.setEnabled(b);
	}

	@SuppressWarnings("serial")
	private class DrawPositivePolygonAction extends ToggleAction implements
			ShapeListener {
		private PolygonDrawer polygonDrawer;

		public DrawPositivePolygonAction(PolygonDrawer polygonDrawer) {
			super("Draw Positive Polygon Boundary", ComponentFactory
					.getIcon("/resource/addPositivePolygon16.png"));
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
			zonesController.setDrawingControlsEnabled(!state);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(shape, BoundaryType.POSITIVE);
			setDrawingControlsEnabled(true);
			zonesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class DrawPositiveCircleAction extends Action implements
			ShapeListener {
		private CircleDrawer circleDrawer;

		public DrawPositiveCircleAction(CircleDrawer circleDrawer) {
			super("Draw Positive Circle Boundary", ComponentFactory
					.getIcon("/resource/addPositiveCircle16.png"));
			this.circleDrawer = circleDrawer;
		}

		@Override
		public void actionPerformed() {
			circleDrawer.start(this);
			setDrawingControlsEnabled(false);
			zonesController.setDrawingControlsEnabled(false);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(shape, BoundaryType.POSITIVE);
			setDrawingControlsEnabled(true);
			zonesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class DrawNegativePolygonAction extends ToggleAction implements
			ShapeListener {
		private PolygonDrawer polygonDrawer;

		public DrawNegativePolygonAction(PolygonDrawer polygonDrawer) {
			super("Draw Negative Polygon Boundary", ComponentFactory
					.getIcon("/resource/addNegativePolygon16.png"));
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
			zonesController.setDrawingControlsEnabled(!state);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(shape, BoundaryType.NEGATIVE);
			setDrawingControlsEnabled(true);
			zonesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class DrawNegativeCircleAction extends Action implements
			ShapeListener {
		private CircleDrawer circleDrawer;

		public DrawNegativeCircleAction(CircleDrawer circleDrawer) {
			super("Draw Negative Circle Boundary", ComponentFactory
					.getIcon("/resource/addNegativeCircle16.png"));
			this.circleDrawer = circleDrawer;
		}

		@Override
		public void actionPerformed() {
			circleDrawer.start(this);
			setDrawingControlsEnabled(false);
			zonesController.setDrawingControlsEnabled(false);
		}

		@Override
		public void shapeCreated(Shape shape) {
			add(shape, BoundaryType.NEGATIVE);
			setDrawingControlsEnabled(true);
			zonesController.setDrawingControlsEnabled(true);
		}
	}

	@SuppressWarnings("serial")
	private class ClearBoundariesAction extends Action {
		public ClearBoundariesAction() {
			super("Clear Boundaries", ComponentFactory
					.getIcon("general/Delete16"));
		}

		@Override
		public void actionPerformed() {
			clear();
		}
	}

	@SuppressWarnings("serial")
	private class RefreshMaskAction extends Action {
		public RefreshMaskAction() {
			super("Refresh Boundaries", ComponentFactory
					.getIcon("general/Refresh16"));
		}

		@Override
		public void actionPerformed() {
			boundariesManager.refreshMasks();
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
			clear();
	}

}
