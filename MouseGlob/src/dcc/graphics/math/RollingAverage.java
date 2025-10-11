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

public class RollingAverage {

	private final double[] values;
	private final int numValues;
	private int count = 0;

	public RollingAverage(int numValues) {
		values = new double[numValues];
		this.numValues = numValues;
		count = 0;
	}

	public void add(double value) {
		values[count++ % numValues] = value;
	}

	public double getAverage() {
		int num = Math.min(count, numValues);
		double sum = 0;
		for (int i = 0; i < num; i++)
			sum += values[i];
		return sum / num;
	}

	public void reset() {
		for (int i = 0; i < values.length; i++)
			values[i] = 0;
		count = 0;
	}

}
