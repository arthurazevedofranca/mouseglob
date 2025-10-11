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

public class CircleDrawer extends ShapeDrawer<Circle> {

	private static CircleDrawer instance;

	public static CircleDrawer getInstance() {
		if (instance == null)
			instance = new CircleDrawer();
		return instance;
	}

	private CircleDrawer() {
		super(Circle.class);
	}

	@Override
	protected Circle getNewShape() {
		return new Circle();
	}

	@Override
	protected void paintHandles(PGraphics g) {
		shapeBeingEdited.getCenter().paint(g);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if (isDrawing()) {
			if (event.getType() == Type.PRESSED)
				return setAnchor(event.getMouseX(), event.getMouseY());
			else if (event.getType() == Type.RELEASED) {
				finish();
				return true;
			}
		}
		return super.onMouseEvent(event);
	}

	private boolean setAnchor(int x, int y) {
		shapeBeingDrawn.setAnchor(x, y);
		return true;
	}

	@Override
	protected boolean update(int x, int y) {
		if (isDrawing()) {
			shapeBeingDrawn.findCenter(x, y);
			shapeBeingDrawn.updateInspector();
			return true;
		}

		if (isMovingPoint()) {
			pointBeingMoved.x = x;
			pointBeingMoved.y = y;

			String name = x + ", " + y;
			if (!pointBeingMoved.equals(shapeBeingEdited.getCenter())) {
				shapeBeingEdited.setRadius(pointBeingMoved);
				shapeBeingEdited.updateInspector();
				name = shapeBeingEdited.getRadiusPointName();
			}
			pointBeingMoved.setName(name);
			return true;
		}

		return false;
	}
}
