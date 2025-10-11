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

import dcc.graphics.binary.BinaryMask2D;

public class MaskedScalarMap extends DefaultMap<Double> {

	private final ScalarMap map;
	private final BinaryMask2D mask;

	public MaskedScalarMap(ScalarMap map, BinaryMask2D mask) {
		this.map = map;
		this.mask = mask;
	}

	@Override
	public Double get(int i, int j) {
		return mask.get(i, j) ? map.get(i, j) : 0;
	}

	@Override
	public Map<Double> get(int x, int y, int w, int h) {
		throw new UnsupportedOperationException(
				"Capture is not implemented for MaskedScalarMap");
	}

	@Override
	public int getWidth() {
		return map.getWidth();
	}

	@Override
	public int getHeight() {
		return map.getHeight();
	}

	public double[][] getValues() {
		double[][] values = new double[map.width][map.height];
		for (int i = 0; i < map.width; i++)
			for (int j = 0; j < map.height; j++)
				values[i][j] = get(i, j);
		return values;
	}

}
