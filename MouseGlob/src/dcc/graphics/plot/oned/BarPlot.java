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
package dcc.graphics.plot.oned;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import dcc.graphics.Box;
import dcc.graphics.Color;

public class BarPlot extends Plot1D {

	private final List<Box> boxes;

	public BarPlot(int x, int y, int width, int height, Axis xAxis, Axis yAxis) {
		super(x, y, width, height, xAxis, yAxis);
		boxes = new ArrayList<Box>();
	}

	public synchronized void addBar(double x1, double x2, double y) {
		boolean isEmpty = boxes.isEmpty();

		if (xAxis.isAutoMin()) {
			double minX = xAxis.getMin();
			if (x1 < minX || isEmpty) {
				xAxis.setMin(x1);
				boundsChanged = true;
			}
		}

		if (xAxis.isAutoMax()) {
			double maxX = xAxis.getMax();
			if (x1 > maxX || isEmpty) {
				xAxis.setMax(x1);
				boundsChanged = true;
			}
		}

		if (yAxis.isAutoMin()) {
			double minY = yAxis.getMin();
			if (y < minY || isEmpty) {
				yAxis.setMin(y);
				boundsChanged = true;
			}
		}

		if (yAxis.isAutoMax()) {
			double maxY = yAxis.getMax();
			if (y > maxY || isEmpty) {
				yAxis.setMax(y);
				boundsChanged = true;
			}
		}

		boxes.add(Box.fromCorners(x1, y, x2, 0));

		if (boundsChanged)
			notifyRepaintNeeded();
	}

	public synchronized void removeBox(int index) {
		boxes.remove(index);
	}

	public synchronized void clear() {
		boxes.clear();
	}

	@Override
	protected synchronized void paintPlotArea(PGraphics g) {
		g.pushStyle();
		g.fill(Color.rgb(192, 192, 255));
		g.rectMode(PConstants.CORNERS);
		synchronized (boxes) {
			for (Box box : boxes) {
				float x1 = getX(box.left), x2 = getX(box.right);
				float y1 = getY(box.top), y2 = getY(box.bottom);
				g.rect(x1, y1, x2, y2);
			}
		}
		g.popStyle();
	}

	@Override
	public synchronized void paintIncrement(PGraphics g) {
		paintPlotArea(g);
	}

}
