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
package dcc.graphics.math;

import dcc.graphics.Box;
import dcc.graphics.math.async.Operation2D;
import dcc.graphics.pool.DoubleMatrixPool;

public class FunctionMap extends DefaultMap<Double> {

	public static interface Function2D {
		double apply(double x, double y);
	}

	private Function2D function;
	private Box bounds;
	private int width, height;

	public FunctionMap(Function2D function, Box bounds, int width, int height) {
		this.function = function;
		this.bounds = bounds;
		this.width = width;
		this.height = height;
	}

	public Function2D getFunction() {
		return function;
	}

	public void setFunction(Function2D function) {
		this.function = function;
	}

	public Box getBounds() {
		return bounds;
	}

	public void setBounds(Box bounds) {
		this.bounds = bounds;
	}

	@Override
	public Double get(int i, int j) {
		return function.apply(getX(i), getY(j));
	}

	@Override
	public FunctionMap get(int x, int y, int w, int h) {
		double left = getX(x);
		double top = getY(y);
		double right = getX(x + w);
		double bottom = getY(y + h);
		Box newBounds = Box.fromCorners(left, top, right, bottom);
		return new FunctionMap(function, newBounds, w, h);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public double[][] getValues() {
		final double[][] values = DoubleMatrixPool.get(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				values[i][j] = get(i, j);
			}
		}.execute();
		return values;
	}

	private double getX(int i) {
		return bounds.left + i * bounds.width / width;
	}

	private double getY(int j) {
		return bounds.bottom - j * bounds.height / height;
	}

}
