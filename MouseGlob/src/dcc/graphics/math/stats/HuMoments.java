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
package dcc.graphics.math.stats;

public class HuMoments extends NormalizedMoments {

	public double h1() {
		return eta20 + eta02;
	}

	public double h2() {
		return sqr(eta20 - eta02) + 4 * sqr(eta11);
	}

	public double h3() {
		return sqr(eta30 - 3 * eta12) + sqr(3 * eta21 - eta03);
	}

	public double h4() {
		return sqr(eta30 + eta12) + sqr(eta21 + eta03);
	}

	public double h5() {
		double a = sqr(eta30 + eta12);
		double b = sqr(eta21 + eta03);
		return (eta30 - 3 * eta12) * (eta30 + eta12) * (a - 3 * b)
				+ (3 * eta21 - eta03) * (eta21 + eta03) * (3 * a - b);
	}

	public double h6() {
		double a = sqr(eta30 + eta12);
		double b = sqr(eta21 + eta03);
		return (eta20 - eta02) * (a - b) + 4 * eta11 * (eta30 + eta12)
				* (eta21 + eta03);
	}

	public double h7() {
		double a = sqr(eta30 + eta12);
		double b = sqr(eta21 + eta03);
		return (3 * eta21 - eta03) * (eta30 + eta12) * (a - 3 * b)
				+ (eta30 - 3 * eta12) * (eta21 + eta03) * (3 * a - b);
	}

	public double[] all() {
		return new double[] { h1(), h2(), h3(), h4(), h5(), h6(), h7() };
	}

	public double[] normalized() {
		double h1 = h1();
		double r4 = h1 * h1;
		double r6 = r4 * h1;
		double r8 = r4 * r4;
		double r12 = r6 * r6;
		return new double[] { 1, h2() / r4, h3() / r6, h4() / r6, h5() / r12,
				h6() / r8, h7() / r12 };
	}

	public double distance(HuMoments other) {
		return distance(all(), other.all());
	}

	private static double sqr(double x) {
		return x * x;
	}

	private static double distance(double[] x, double[] y) {
		double s = 0;
		x[6] = Math.abs(x[6]);
		y[6] = Math.abs(y[6]);
		for (int i = 0; i < x.length; i++)
			s += (x[i] - y[i]) * (x[i] - y[i]);
		return Math.sqrt(s);
	}

	@Override
	public HuMoments clone() {
		HuMoments copy = new HuMoments();
		copy.eta20 = eta20;
		copy.eta11 = eta11;
		copy.eta02 = eta02;
		copy.eta30 = eta30;
		copy.eta21 = eta21;
		copy.eta12 = eta12;
		copy.eta03 = eta03;
		return copy;
	}

}
