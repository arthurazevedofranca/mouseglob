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

import dcc.graphics.math.async.Operation1D;

public final class BinaryMask1DUtils {

	private BinaryMask1DUtils() {
	}

	public static DefaultBinaryMask1D and(BinaryMask1D mask1, BinaryMask1D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i) {
				return mask1.get(i) && mask2.get(i);
			}
		};
	}

	public static DefaultBinaryMask1D or(BinaryMask1D mask1, BinaryMask1D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i) {
				return mask1.get(i) || mask2.get(i);
			}
		};
	}

	public static DefaultBinaryMask1D xor(BinaryMask1D mask1, BinaryMask1D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i) {
				return mask1.get(i) ^ mask2.get(i);
			}
		};
	}

	public static DefaultBinaryMask1D not(BinaryMask1D mask) {
		return new UnaryOpMask(mask) {
			@Override
			public boolean get(int i) {
				return !mask.get(i);
			}
		};
	}

	public static int count(final BinaryMask1D mask, final int from,
			final int to) {
		class Count extends Operation1D {
			int count = 0;

			protected Count() {
				super(from, to);
			}

			@Override
			protected void compute(int i) {
				count += mask.get(i) ? 1 : 0;
			}
		}
		Count count = new Count();
		count.execute();
		return count.count;
	}

	private static abstract class BinaryOpMask extends DefaultBinaryMask1D {
		protected final BinaryMask1D mask1, mask2;

		private BinaryOpMask(BinaryMask1D mask1, BinaryMask1D mask2) {
			this.mask1 = mask1;
			this.mask2 = mask2;
		}

	}

	private static abstract class UnaryOpMask extends DefaultBinaryMask1D {
		protected final BinaryMask1D mask;

		private UnaryOpMask(BinaryMask1D mask) {
			this.mask = mask;
		}
	}

}
