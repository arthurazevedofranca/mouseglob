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

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.math.Vector;
import dcc.graphics.plot.Plot;

public abstract class Plot1D extends Plot {

	private static final int MARGIN = 20;

	protected final Axis xAxis, yAxis;
	protected final AxisRenderer xRenderer, yRenderer;

	private int xLength, yLength;
	private int xOffset, yOffset;
	protected boolean boundsChanged = true;
	private boolean scalingConstrained = false;
	private boolean verticalGrid = false, horizontalGrid = false;

	protected Plot1D(int x, int y, int width, int height, Axis xAxis, Axis yAxis) {
		super(x, y, width, height);

		xLength = width - 2 * MARGIN;
		yLength = height - 2 * MARGIN;

		this.xAxis = xAxis;
		this.yAxis = yAxis;

		xRenderer = new AxisRenderer(xAxis);
		yRenderer = new AxisRenderer(yAxis);
	}

	@Override
	public synchronized final void setSize(int width, int height) {
		boundsChanged = true;
		super.setSize(width, height);
	}

	final float getX(double value) {
		return getPlotX() + xOffset + xLength
				* xAxis.getRelativePosition(value);
	}

	final float getY(double value) {
		return getPlotY() + yOffset + yLength
				* (1 - yAxis.getRelativePosition(value));
	}

	public final Axis getXAxis() {
		return xAxis;
	}

	public final Axis getYAxis() {
		return yAxis;
	}

	@Override
	public final synchronized Probe updateProbe(Probe probe, float x, float y) {
		double tx = x - getPlotX() - xOffset;
		double ty = y - getPlotY() - yOffset;
		double xPosition = tx / xLength;
		double yPosition = 1 - ty / yLength;
		if (xPosition < 0 || xPosition > 1 || yPosition < 0 || yPosition > 1)
			return null;
		double xValue = xAxis.getValue(xPosition);
		double yValue = yAxis.getValue(yPosition);
		String text = String.format("%f, %f", xValue, yValue);
		probe.setText(text).setPosition(x, y).setValues(xValue, yValue);
		return probe;
	}

	final boolean isWithinBounds(double x, double y) {
		return xAxis.isWithinBounds(x) && yAxis.isWithinBounds(y);
	}

	final boolean isWithinBounds(Vector p) {
		return isWithinBounds(p.x, p.y);
	}

	public final void setScalingConstrained(boolean scalingConstrained) {
		this.scalingConstrained = scalingConstrained;
	}

	public final void setGridLines(boolean horizontal, boolean vertical) {
		horizontalGrid = horizontal;
		verticalGrid = vertical;
	}

	@Override
	public final boolean needsRepaint() {
		return boundsChanged;
	}

	@Override
	public final synchronized void paint(PGraphics g) {
		int xHeight = (int) xRenderer.getHeight(g);
		int yLowerOffset = MARGIN + xHeight;
		yLength = getPlotHeight() - MARGIN - yLowerOffset;
		int yWidth = (int) yRenderer.getWidth(g, yLength);
		xOffset = MARGIN + yWidth;
		yOffset = MARGIN;
		xLength = getPlotWidth() - xOffset - MARGIN;

		if (scalingConstrained) {
			double xRange = xAxis.getRange();
			double yRange = yAxis.getRange();
			double xScale = xLength / xRange;
			double yScale = yLength / yRange;
			if (xScale > yScale) {
				xLength = (int) (yScale * xRange);
				xOffset = (getPlotWidth() + yWidth - xLength) / 2;
			} else {
				yLength = (int) (xScale * yRange);
				yOffset = (getPlotHeight() - xHeight - yLength) / 2;
			}
		}

		int x0 = getPlotX() + xOffset;
		int y0 = getPlotY() + yOffset;

		if (verticalGrid)
			xRenderer.paintVerticalGrid(g, x0, y0, xLength, yLength);
		if (horizontalGrid)
			yRenderer.paintHorizontalGrid(g, x0, y0, xLength, yLength);

		paintPlotArea(g);

		g.pushStyle();
		g.pushMatrix();

		g.fill(0);
		g.translate(x0, y0);
		xRenderer.paintHorizontal(g, 0, yLength, xLength);
		yRenderer.paintVertical(g, 0, 0, yLength);

		if (!paintables.isEmpty()) {
			g.pushMatrix();
			float scaleX = (float) (xLength / xAxis.getRange());
			float scaleY = (float) (yLength / yAxis.getRange());
			g.scale(scaleX, scaleY);
			g.translate((float) xAxis.getMin(), (float) yAxis.getMin());
			for (Paintable p : paintables)
				p.paint(g);
			g.popMatrix();
		}

		g.popMatrix();
		g.popStyle();

		paintLabel(g);
		boundsChanged = false;
	}

	protected abstract void paintPlotArea(PGraphics g);

	@Override
	public String toString() {
		return xAxis + " x " + yAxis;
	}

}
