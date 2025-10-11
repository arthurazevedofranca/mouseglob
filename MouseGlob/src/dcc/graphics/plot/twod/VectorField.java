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

class VectorField {

	protected double[][] magnitude;
	protected double[][] angle;

	protected int width;
	protected int height;

	void setValues(double[][] x, double[][] y) {
		setRadial(getMagnitude(x, y), getAngle(x, y));
	}

	void setRadial(double[][] magnitude, double[][] angle) {
		width = magnitude.length;
		height = magnitude[0].length;

		this.magnitude = magnitude;
		this.angle = angle;
	}

	private static double[][] getMagnitude(double[][] x, double[][] y) {
		int width = x.length;
		int height = x[0].length;

		if (width == 0 || height == 0)
			return null;

		double[][] mag = new double[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				mag[i][j] = Math.hypot(x[i][j], y[i][j]);

		return mag;
	}

	private static double[][] getAngle(double[][] x, double[][] y) {
		int width = x.length;
		int height = x[0].length;

		if (width == 0 || height == 0)
			return null;

		double[][] angle = new double[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				angle[i][j] = Math.atan2(y[i][j], x[i][j]) * 180.0 / Math.PI;
				if (angle[i][j] < 0)
					angle[i][j] += 360.0;
			}

		return angle;
	}

}
