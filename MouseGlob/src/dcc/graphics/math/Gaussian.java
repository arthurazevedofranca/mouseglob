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

import dcc.graphics.math.Matrix.SingularMatrixException;

public final class Gaussian {

	private Gaussian() {
	}

	public static double g(double x, double sigma) {
		double exponent = -(x * x) / (2 * sigma * sigma);
		return Math.exp(exponent) / (Math.sqrt(2 * Math.PI) * sigma);
	}

	public static double dg(double x, double sigma) {
		return x / (sigma * sigma) * g(x, sigma);
	}

	public static double g2D(double x, double y, double sigma) {
		double exponent = -(x * x + y * y) / (2 * sigma * sigma);
		return Math.exp(exponent) / (2 * Math.PI * sigma * sigma);
	}

	public static double pdf2D(Vector x, Vector mean, Matrix sigma) {
		try {
			Matrix invSigma = sigma.inverse();
			Vector u = x.subtract(mean);
			double exponent = -u.dot(invSigma.multiply(u)) / 2;
			double det = sigma.determinant();
			return Math.exp(exponent) / (2 * Math.PI * Math.sqrt(det));
		} catch (SingularMatrixException e) {
			return 0;
		}
	}

}
