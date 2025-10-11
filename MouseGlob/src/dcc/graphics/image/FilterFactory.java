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
package dcc.graphics.image;

import dcc.graphics.math.Gaussian;

public class FilterFactory {
	public static final double[][] ROBERTS_CROSS_X = { { 1, 0 }, { 0, -1 } };
	public static final double[][] ROBERTS_CROSS_Y = { { 0, 1 }, { -1, 0 } };
	public static final double[][][] ROBERTS_CROSS = { ROBERTS_CROSS_X,
			ROBERTS_CROSS_Y };

	public static final double[][] SOBEL_X = { { -1, 0, 1 }, { -2, 0, 2 },
			{ -1, 0, 1 } };
	public static final double[][] SOBEL_Y = { { 1, 2, 1 }, { 0, 0, 0 },
			{ -1, -2, -1 } };
	public static final double[][][] SOBEL = { SOBEL_X, SOBEL_Y };

	public static final double[][] PREWITT_0 = { { -1, 1, 1 }, { -1, -2, 1 },
			{ -1, 1, 1 } };
	public static final double[][] PREWITT_45 = rotate45(PREWITT_0);
	public static final double[][] PREWITT_90 = rotate90(PREWITT_0);
	public static final double[][] PREWITT_135 = rotate135(PREWITT_0);
	public static final double[][] PREWITT_180 = rotate180(PREWITT_0);
	public static final double[][] PREWITT_225 = rotate225(PREWITT_0);
	public static final double[][] PREWITT_270 = rotate270(PREWITT_0);
	public static final double[][] PREWITT_315 = rotate315(PREWITT_0);
	public static final double[][][] PREWITT = { PREWITT_0, PREWITT_90,
			PREWITT_180, PREWITT_270 };

	public static final double[][] LINE_0 = { { -1, 2, -1 }, { -1, 2, -1 },
			{ -1, 2, -1 } };
	public static final double[][] LINE_45 = { { -1, -1, 2 }, { -1, 2, -1 },
			{ 2, -1, -1 } };
	public static final double[][] LINE_90 = { { -1, 2, -1 }, { -1, 2, -1 },
			{ -1, 2, -1 } };
	public static final double[][] LINE_135 = { { 2, -1, -1 }, { -1, 2, -1 },
			{ -1, -1, 2 } };
	public static final double[][][] LINE = { LINE_0, LINE_45, LINE_90,
			LINE_135 };

	public static double[] gaussian(double sigma) {
		int r = (int) Math.ceil(3 * sigma);
		int n = 2 * r + 1;
		double[] g = new double[n];
		for (int i = 0; i < n; i++)
			g[i] = Gaussian.g(i - r, sigma);
		return normalize(g);
	}

	public static double[] dGaussian(double sigma) {
		int r = (int) Math.ceil(3 * sigma);
		int n = 2 * r + 1;
		double[] dg = new double[n];
		for (int i = 0; i < n; i++)
			dg[i] = Gaussian.dg(i - r, sigma);
		return dg;
	}

	public static double[] normalize(double[] filter) {
		double sum = 0;
		for (int i = 0; i < filter.length; i++)
			sum += filter[i];
		for (int i = 0; i < filter.length; i++)
			filter[i] /= sum;
		return filter;
	}

	public static double[][] gaussianX(double sigma) {
		int r = (int) Math.ceil(3 * sigma);
		int n = 2 * r + 1;
		double[][] g = new double[1][n];
		for (int i = 0; i < n; i++)
			g[0][i] = Gaussian.g(i - r, sigma);
		return g;
	}

	public static double[][] gaussianY(double sigma) {
		int r = (int) Math.ceil(3 * sigma);
		int n = 2 * r + 1;
		double[][] g = new double[n][1];
		for (int i = 0; i < n; i++)
			g[i][0] = Gaussian.g(i - r, sigma);
		return g;
	}

	public static double[][] gaussian2D(double sigma) {
		int r = (int) Math.ceil(3 * sigma);
		int n = 2 * r + 1;
		double[][] g = new double[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				g[i][j] = Gaussian.g2D(i - r, j - r, sigma);
		return g;
	}

	public static void print(double[][] filter) {
		for (int j = 0; j < filter.length; j++) {
			for (int i = 0; i < filter[0].length; i++)
				System.out.print(filter[j][i] + "\t");
			System.out.println();
		}
	}

	public static double[][] normalize(double[][] filter) {
		double sum = 0;

		for (int i = 0; i < filter[0].length; i++)
			for (int j = 0; j < filter.length; j++)
				sum += filter[j][i];

		for (int i = 0; i < filter[0].length; i++)
			for (int j = 0; j < filter.length; j++)
				filter[j][i] /= sum;

		return filter;
	}

	private static double[][] rotate45(double[][] filter) {
		double[][] newFilter = new double[3][3];

		newFilter[1][1] = filter[1][1];
		newFilter[0][0] = filter[0][1];
		newFilter[0][1] = filter[0][2];
		newFilter[0][2] = filter[1][2];
		newFilter[1][2] = filter[2][2];
		newFilter[2][2] = filter[2][1];
		newFilter[2][1] = filter[2][0];
		newFilter[2][0] = filter[1][0];
		newFilter[1][0] = filter[0][0];

		return newFilter;
	}

	private static double[][] rotate90(double[][] filter) {
		return rotate45(rotate45(filter));
	}

	private static double[][] rotate135(double[][] filter) {
		return rotate45(rotate90(filter));
	}

	private static double[][] rotate180(double[][] filter) {
		return rotate45(rotate135(filter));
	}

	private static double[][] rotate225(double[][] filter) {
		return rotate45(rotate180(filter));
	}

	private static double[][] rotate270(double[][] filter) {
		return rotate45(rotate225(filter));
	}

	private static double[][] rotate315(double[][] filter) {
		return rotate45(rotate270(filter));
	}
}
