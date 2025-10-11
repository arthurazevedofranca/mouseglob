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

import dcc.graphics.Box;
import dcc.graphics.Color;
import dcc.graphics.binary.BinaryMask2D;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.math.async.Operation2D;
import dcc.mouseglob.inspector.PropertyCollectionInspector;
import dcc.mouseglob.inspector.PropertyInspector;
import dcc.mouseglob.maze.Boundary.BoundaryType;

public class BoundaryMask implements BoundaryListener, BinaryMask2D {

	private final double referenceX, referenceY;

	private final List<Boundary> boundaries;
	private boolean[][] maskArray;

	private int positiveCount, negativeCount;
	private int width, height;

	BoundaryMask(double referenceX, double referenceY) {
		this.referenceX = referenceX;
		this.referenceY = referenceY;

		boundaries = new ArrayList<Boundary>();
		maskArray = null;

		positiveCount = 0;
		negativeCount = 0;
	}

	void add(Boundary boundary) {
		synchronized (boundaries) {
			boundaries.add(boundary);
		}

		if (boundary.getType() == BoundaryType.POSITIVE)
			positiveCount++;
		else
			negativeCount++;
	}

	void remove(Boundary boundary) {
		synchronized (boundaries) {
			boundaries.remove(boundary);
		}

		if (boundary.getType() == BoundaryType.POSITIVE)
			positiveCount--;
		else
			negativeCount--;
	}

	void clear() {
		synchronized (boundaries) {
			boundaries.clear();
		}

		positiveCount = 0;
		negativeCount = 0;
	}

	void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		calculate();
	}

	/**
	 * @return {@code true} if the point is inside the boundaries, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean get(int x, int y) {
		if (boundaries.isEmpty())
			return true;

		synchronized (boundaries) {
			boolean positive = (positiveCount == 0);
			if (!positive)
				for (Boundary boundary : boundaries)
					if (boundary.getType() == BoundaryType.POSITIVE
							&& boundary.get(x, y)) {
						positive = true;
						break;
					}
			if (positive) {
				if (negativeCount != 0) {
					for (Boundary boundary : boundaries)
						if (boundary.getType() == BoundaryType.NEGATIVE
								&& boundary.get(x, y))
							return false;
				}
				return true;
			}
		}

		return false;
	}

	void calculate() {
		if (maskArray == null || maskArray.length != width
				|| maskArray[0].length != height)
			maskArray = new boolean[width][height];

		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				maskArray[i][j] = get(i, j);
			}
		}.execute();
	}

	public void apply(final BinaryImage image, Box.Int bounds) {
		final int x = bounds.left;
		final int y = bounds.top;
		int w = bounds.width;
		int h = bounds.height;

		new Operation2D(w, h) {
			@Override
			protected void compute(int i, int j) {
				if (!maskArray[x + i][y + j])
					image.set(i, j, Color.BLACK);
			}
		}.execute();
	}

	public PropertyInspector<?> getInspector() {
		return new PropertyCollectionInspector<List<Boundary>, Boundary>(
				"Boundaries") {
			@Override
			protected List<Boundary> getValue() {
				return boundaries;
			}
		};
	}

	@Override
	public void onBoundaryEvent(BoundaryEvent event) {
		Boundary b = event.getBoundary();
		switch (event.getType()) {
		case BOUNDARY_ADDED:
			if (b.contains(referenceX, referenceY))
				add(b);
			break;
		case BOUNDARY_REMOVED:
			if (b.contains(referenceX, referenceY))
				remove(b);
			break;
		case BOUNDARIES_CLEARED:
			clear();
			break;
		}
	}

}
