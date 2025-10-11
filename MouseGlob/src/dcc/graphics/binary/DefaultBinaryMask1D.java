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

public abstract class DefaultBinaryMask1D implements BinaryMask1D {

	public BinaryMask1D and(BinaryMask1D mask) {
		return BinaryMask1DUtils.and(this, mask);
	}

	public BinaryMask1D or(BinaryMask1D mask) {
		return BinaryMask1DUtils.or(this, mask);
	}

	public BinaryMask1D xor(BinaryMask1D mask) {
		return BinaryMask1DUtils.xor(this, mask);
	}

	public BinaryMask1D not() {
		return BinaryMask1DUtils.not(this);
	}

	public String toString(int size) {
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++)
			sb.append(get(i) ? '1' : '0');
		return sb.toString();
	}

}
