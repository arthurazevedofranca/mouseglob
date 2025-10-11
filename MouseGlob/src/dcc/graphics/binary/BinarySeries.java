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
package dcc.graphics.binary;

import java.util.BitSet;

public class BinarySeries extends DefaultBinaryMask1D {

	private static final int DEFAULT_INITIAL_SIZE = 100;

	private final BitSet bits;

	private int size = 0;

	public BinarySeries() {
		this(DEFAULT_INITIAL_SIZE);
	}

	public BinarySeries(int initialSize) {
		bits = new BitSet(initialSize);
	}

	public void add(boolean value) {
		bits.set(size++, value);
	}

	@Override
	public boolean get(int index) {
		if (index < 0 || index >= size)
			return false;
		return bits.get(index);
	}

	public void set(int index, boolean value) {
		bits.set(index, value);
	}

	public void clear() {
		size = 0;
	}

	public int count() {
		return bits.cardinality();
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public static BinarySeries from(BinaryMask1D mask, int size) {
		BinarySeries series = new BinarySeries(size);
		for (int i = 0; i < size; i++)
			series.add(mask.get(i));
		return series;
	}

}
