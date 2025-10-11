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
package dcc.graphics.series;

public class RollingSeries1D extends Series1D {

	private final int maxValues;

	public RollingSeries1D(int maxValues) {
		this.maxValues = maxValues;
	}

	@Override
	public void add(double value) {
		super.add(value);
		if (size() > maxValues)
			remove(0);
	}

	@Override
	public void add(double[] values) {
		super.add(values);
		while (size() > maxValues)
			remove(0);
	}

}
