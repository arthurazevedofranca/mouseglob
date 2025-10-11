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
package dcc.graphics.plot.twod;

import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Image;
import dcc.graphics.math.Optimizer;
import dcc.graphics.math.async.Operation2D;
import dcc.graphics.plot.ColorBar;
import dcc.graphics.plot.ColorMap;
import dcc.graphics.plot.oned.Axis;

public class ScalarPlot2D extends Plot2D {

	private double[][] values;

	private Axis axis;
	private ColorMap colorMap;

	public ScalarPlot2D(int x, int y, int width, int height, Axis axis,
			ColorMap colorMap) {
		super(x, y, width, height);
		this.axis = axis;
		this.colorMap = colorMap;
	}

	public ScalarPlot2D(int x, int y, int width, int height, Axis axis) {
		this(x, y, width, height, axis, ColorMap.GRAY);
	}

	public ScalarPlot2D(int x, int y, int width, int height, ColorMap colorMap) {
		this(x, y, width, height, Axis.autoscaling(1), colorMap);
	}

	public ScalarPlot2D(int x, int y, int width, int height) {
		this(x, y, width, height, Axis.autoscaling(1));
	}

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
	}

	public void setValues(double[][] values) {
		dataWidth = values.length;
		dataHeight = values[0].length;
		this.values = values;

		boolean autoMin = axis.isAutoMin();
		boolean autoMax = axis.isAutoMax();
		if (autoMin || autoMax) {
			Optimizer opt = new Optimizer(values);
			if (autoMin)
				axis.setMin(opt.getMinimumValue());
			if (autoMax)
				axis.setMax(opt.getMaximumValue());
		}

		notifyValuesChanged();
	}

	public ColorMap getColorMap() {
		return colorMap;
	}

	public void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}

	public ColorBar getColorBar() {
		return new ColorBar(axis, colorMap);
	}

	@Override
	protected void render(final int[] pixels) {
		synchronized (values) {
			new Operation2D(dataWidth, dataHeight) {
				@Override
				protected void compute(int i, int j) {
					double rel = axis.getRelativePosition(values[i][j]);
					pixels[i + j * dataWidth] = colorMap.color(rel);
				}
			}.execute();
		}
	}

	@Override
	public Image getImage() {
		int[] pixels = new int[dataWidth * dataHeight];
		render(pixels);
		if (colorMap == ColorMap.GRAY)
			return new GrayscaleImage(dataWidth, dataHeight, pixels);
		return new Image(dataWidth, dataHeight, pixels);
	}
}
