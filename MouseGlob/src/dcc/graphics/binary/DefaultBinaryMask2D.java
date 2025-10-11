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

public abstract class DefaultBinaryMask2D implements BinaryMask2D {

	public BinaryMask2D and(BinaryMask2D mask) {
		return BinaryMask2DUtils.and(this, mask);
	}

	public BinaryMask2D or(BinaryMask2D mask) {
		return BinaryMask2DUtils.or(this, mask);
	}

	public BinaryMask2D xor(BinaryMask2D mask) {
		return BinaryMask2DUtils.xor(this, mask);
	}

	public BinaryMask2D not() {
		return BinaryMask2DUtils.not(this);
	}

}
