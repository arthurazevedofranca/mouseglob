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

public class Statistics {

	private double m0, m1, m2;
	private double min, max;

	public Statistics() {
		clear();
	}

	public Statistics(Statistics stats) {
		m0 = stats.m0;
		m1 = stats.m1;
		m2 = stats.m2;
		min = stats.min;
		max = stats.max;
	}

	public void add(double x) {
		m0 += 1;
		m1 += x;
		m2 += x * x;
		min = x < min ? x : min;
		max = x > max ? x : max;
	}

	public void add(double... xn) {
		for (double x : xn)
			add(x);
	}

	public void add(Iterable<? extends Number> xn) {
		for (Number x : xn)
			add(x.doubleValue());
	}

	public int getCount() {
		return (int) m0;
	}

	public double getSum() {
		return m1;
	}

	public double getMean() {
		return m1 / m0;
	}

	public double getSumOfSquares() {
		return m2;
	}

	public double getVariance() {
		return (m2 - m1 * m1 / m0) / (m0 - 1);
	}

	public double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	public double getMinimum() {
		return min;
	}

	public double getMaximum() {
		return max;
	}

	public void clear() {
		m0 = m1 = m2 = 0;
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		sb.append("Mean\t").append(getMean()).append('\n');
		sb.append("Std. dev.\t").append(getStandardDeviation()).append('\n');
		sb.append("Min\t").append(getMinimum()).append('\n');
		sb.append("Max\t").append(getMaximum());
		return sb.toString();
	}

}
