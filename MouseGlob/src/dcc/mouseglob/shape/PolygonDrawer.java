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
package dcc.mouseglob.shape;

import processing.core.PGraphics;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseEvent.Type;
import dcc.mouseglob.labelable.Point;

public class PolygonDrawer extends ShapeDrawer<Polygon> {

	private static PolygonDrawer instance;

	public static PolygonDrawer getInstance() {
		if (instance == null)
			instance = new PolygonDrawer();
		return instance;
	}

	private PolygonDrawer() {
		super(Polygon.class);
	}

	@Override
	protected Polygon getNewShape() {
		return new Polygon();
	}

	@Override
	protected void paintHandles(PGraphics g) {
		for (Point vertex : shapeBeingEdited.getVertices()) {
			vertex.setName(vertex.x + ", " + vertex.y);
			vertex.paint(g);
		}
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if (isDrawing()) {
			if (event.getType() == Type.CLICKED)
				return addVertex(event.getMouseX(), event.getMouseY());
			else if (event.getType() == Type.DOUBLE_CLICKED) {
				finish();
				return true;
			}
		}
		return super.onMouseEvent(event);
	}

	private boolean addVertex(int x, int y) {
		if (isDrawing()) {
			shapeBeingDrawn.addVertex(x, y);
			shapeBeingDrawn.updateInspector();
			return true;
		}

		return false;
	}

	@Override
	protected boolean update(int x, int y) {
		if (isMovingPoint()) {
			pointBeingMoved.x = x;
			pointBeingMoved.y = y;
			pointBeingMoved.setName(x + ", " + y);
			shapeBeingEdited.updateInspector();

			return true;
		}

		return false;
	}

}
