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
package dcc.mouseglob.applet;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import dcc.graphics.Paintable;
import dcc.graphics.PaintingEnvironment;

@SuppressWarnings("serial")
public class DefaultApplet extends PApplet implements PaintingEnvironment,
		CursorListener {

	private final List<Paintable> paintables;
	private final List<MouseListener> mouseListeners;

	public DefaultApplet() {
		paintables = new ArrayList<Paintable>();
		mouseListeners = new ArrayList<MouseListener>();
	}

	@Override
	public void setup() {
		stroke(0);

		rectMode(CENTER);
		ellipseMode(CENTER);
		smooth();

		background(255);

		// frameRate(30);
		noLoop();
	}

	@Override
	public void draw() {
		for (Paintable p : paintables)
			p.paint(g);
	}

	public void setAppletSize(int width, int height) {
		size(width, height);
		Dimension dim = new Dimension(width, height);
		setMinimumSize(dim);
		setPreferredSize(dim);
		setMaximumSize(dim);
		redraw();
	}

	private MouseEvent.Button getMouseButton() {
		if (mouseButton == LEFT)
			return MouseEvent.Button.LEFT;
		else if (mouseButton == RIGHT)
			return MouseEvent.Button.RIGHT;
		return null;
	}

	private void notifyMouseListeners(MouseEvent.Type type) {
		redraw();
		MouseEvent event = new MouseEvent(mouseX, mouseY, mouseX, mouseY,
				getMouseButton(), type);
		for (MouseListener listener : mouseListeners)
			if (listener.onMouseEvent(event))
				break;
	}

	@Override
	public void mouseMoved() {
		notifyMouseListeners(MouseEvent.Type.MOVED);
	}

	@Override
	public void mouseClicked() {
		if (mouseEvent.getClickCount() == 1)
			notifyMouseListeners(MouseEvent.Type.CLICKED);
		else
			notifyMouseListeners(MouseEvent.Type.DOUBLE_CLICKED);
	}

	@Override
	public void mousePressed() {
		notifyMouseListeners(MouseEvent.Type.PRESSED);
	}

	@Override
	public void mouseReleased() {
		notifyMouseListeners(MouseEvent.Type.RELEASED);
	}

	@Override
	public void mouseDragged() {
		notifyMouseListeners(MouseEvent.Type.DRAGGED);
	}

	@Override
	public void setCursor(Cursor cursor) {
		if (cursor != null)
			cursor(cursor.pCursor);
	}

	@Override
	public void addPaintable(Paintable paintable) {
		paintables.add(paintable);
	}

	@Override
	public void removePaintable(Paintable paintable) {
		paintables.remove(paintable);
	}

	public void addMouseListener(MouseListener listener) {
		mouseListeners.add(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);
	}

}
