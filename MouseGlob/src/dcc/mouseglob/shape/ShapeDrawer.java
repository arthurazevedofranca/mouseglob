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

import java.util.ArrayList;
import java.util.Collection;

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.mouseglob.applet.CursorListener;
import dcc.mouseglob.applet.CursorListener.Cursor;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseListener;
import dcc.mouseglob.labelable.Point;
import dcc.mouseglob.labelable.Style;
import dcc.mouseglob.maze.Region;
import dcc.tree.TreeSelectionListener;
import dcc.tree.TreeStructureListener;
import dcc.tree.Treeable;

public abstract class ShapeDrawer<S extends Shape> implements Paintable,
		MouseListener, TreeStructureListener, TreeSelectionListener {

	private static final Collection<ShapeDrawer<?>> drawers = new ArrayList<>();

	private static void clearAll() {
		for (ShapeDrawer<?> drawer : drawers) {
			drawer.shapeBeingDrawn = null;
			drawer.shapeBeingEdited = null;
		}
	}

	private final Collection<CursorListener> cursorListeners;
	private final Class<S> shapeClass;

	protected S shapeBeingDrawn;
	protected S shapeBeingEdited;
	protected Point pointBeingMoved;

	private ShapeListener listener;

	protected ShapeDrawer(Class<S> shapeClass) {
		cursorListeners = new ArrayList<CursorListener>();
		this.shapeClass = shapeClass;
		drawers.add(this);
	}

	protected abstract S getNewShape();

	public final void start(ShapeListener listener) {
		clearAll();
		this.listener = listener;
		shapeBeingDrawn = getNewShape();
		setCursor(Cursor.CROSS);
	}

	public final void finish() {
		listener.shapeCreated(shapeBeingDrawn);
		listener = null;
		shapeBeingEdited = shapeBeingDrawn;
		shapeBeingDrawn = null;
		setCursor(Cursor.ARROW);
	}

	protected final void deselectPoint() {
		if (isMovingPoint()) {
			pointBeingMoved.setSelected(false);
			pointBeingMoved = null;
			setCursor(Cursor.ARROW);
		}
	}

	protected abstract boolean update(int x, int y);

	public final boolean isDrawing() {
		return shapeBeingDrawn != null;
	}

	public final boolean isEditing() {
		return shapeBeingEdited != null;
	}

	public final boolean isMovingPoint() {
		return pointBeingMoved != null;
	}

	@Override
	public final void paint(PGraphics g) {
		if (isDrawing()) {
			g.pushStyle();
			Style.ZONE.apply(g);
			shapeBeingDrawn.paint(g);
			g.popStyle();
		}

		if (isEditing())
			paintHandles(g);

		if (isMovingPoint())
			pointBeingMoved.paint(g);
	}

	protected abstract void paintHandles(PGraphics g);

	protected final boolean selectPoint(int x, int y) {
		if (isEditing()) {
			deselectPoint();
			pointBeingMoved = shapeBeingEdited.getSelectedPoint(x, y);
			setCursor(pointBeingMoved != null ? Cursor.MOVE : Cursor.ARROW);
		}

		return false;
	}

	public final void addCursorListener(CursorListener cursorListener) {
		cursorListeners.add(cursorListener);
	}

	protected final void setCursor(Cursor cursor) {
		for (CursorListener cursorListener : cursorListeners)
			cursorListener.setCursor(cursor);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		int x = event.getMouseX(), y = event.getMouseY();
		switch (event.getType()) {
		case MOVED:
			return selectPoint(x, y);
		case RELEASED:
			deselectPoint();
			return false;
		case DRAGGED:
			return update(x, y);
		default:
			return false;
		}
	}

	@Override
	public final void nodeAdded(Treeable object) {
	}

	@Override
	public final void nodeRemoved(Treeable object) {
		shapeBeingEdited = null;
	}

	@Override
	public final void nodeSelected(Treeable object) {
		shapeBeingEdited = null;

		Shape shape = null;
		if (object instanceof Region)
			shape = ((Region) object).getShape();

		if (shapeClass.isInstance(shape))
			shapeBeingEdited = shapeClass.cast(shape);
	}

	@Override
	public final void nodeDoubleClicked(Treeable object) {
	}

}
