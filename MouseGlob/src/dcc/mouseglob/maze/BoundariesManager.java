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

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.image.Image;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.maze.BoundaryEvent.BoundaryEventType;
import dcc.tree.DefaultTreeable;
import dcc.tree.TreeBranch;
import dcc.tree.TreeNode;

public class BoundariesManager extends DefaultTreeable implements
		TreeBranch<Boundary>, NewFrameListener {

	private List<Boundary> boundaries;
	private List<BoundaryMask> masks;
	private boolean maskUpToDate;
	private int width, height;

	private TreeNode node;

	private List<BoundaryListener> boundaryListeners;

	public BoundariesManager() {
		super("Boundaries", "/resource/boundary16.png");

		boundaries = new ArrayList<Boundary>();
		masks = new ArrayList<BoundaryMask>();
		maskUpToDate = false;

		node = getNode();

		boundaryListeners = new ArrayList<BoundaryListener>();
	}

	@Override
	public void add(Boundary b) {
		synchronized (boundaries) {
			boundaries.add(b);
		}

		maskUpToDate = false;

		b.setName("Boundary " + boundaries.size());

		BoundaryEvent event = new BoundaryEvent(
				BoundaryEventType.BOUNDARY_ADDED, b);
		for (BoundaryListener listener : boundaryListeners)
			listener.onBoundaryEvent(event);

		node.add(b);
	}

	@Override
	public void remove(Boundary b) {
		node.remove(b);

		synchronized (boundaries) {
			boundaries.remove(b);
		}

		maskUpToDate = false;

		BoundaryEvent event = new BoundaryEvent(
				BoundaryEventType.BOUNDARY_REMOVED, b);
		for (BoundaryListener listener : boundaryListeners)
			listener.onBoundaryEvent(event);
	}

	public void clear() {
		synchronized (boundaries) {
			boundaries.clear();
		}

		maskUpToDate = false;

		BoundaryEvent event = new BoundaryEvent(
				BoundaryEventType.BOUNDARIES_CLEARED, null);
		for (BoundaryListener listener : boundaryListeners)
			listener.onBoundaryEvent(event);

		updateStructure();
	}

	public boolean isEmpty() {
		return boundaries.isEmpty();
	}

	public List<Boundary> getBoundaries() {
		return boundaries;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	void refreshMasks() {
		for (BoundaryMask mask : masks)
			mask.calculate();
		maskUpToDate = true;
	}

	@Override
	public void newFrame(Image frame, long time) {
		if (!maskUpToDate) {
			width = frame.getWidth();
			height = frame.getHeight();
			for (BoundaryMask mask : masks)
				mask.setSize(width, height);
			maskUpToDate = true;
		}
	}

	public BoundaryMask getMask(double x, double y) {
		BoundaryMask mask = new BoundaryMask(x, y);
		for (Boundary boundary : boundaries)
			if (boundary.contains(x, y))
				mask.add(boundary);
		mask.setSize(width, height);
		masks.add(mask);
		addBoundaryListener(mask);
		return mask;
	}

	public void addBoundaryListener(BoundaryListener listener) {
		boundaryListeners.add(listener);
	}

	private void updateStructure() {
		node.removeAllChildren();
		synchronized (boundaries) {
			for (Boundary boundary : boundaries)
				node.add(boundary);
		}
	}

	public Paintable getRenderer() {
		return new BoundariesRenderer();
	}

	private class BoundariesRenderer implements Paintable {
		@Override
		public void paint(PGraphics g) {
			synchronized (boundaries) {
				for (Boundary boundary : boundaries)
					boundary.paint(g);
			}
		}
	}

}
