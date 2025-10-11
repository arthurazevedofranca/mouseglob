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

import dcc.graphics.Box;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.math.async.Operation2D;

public final class BinaryMask2DUtils {

	private BinaryMask2DUtils() {
	}

	public static DefaultBinaryMask2D and(BinaryMask2D mask1, BinaryMask2D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i, int j) {
				return mask1.get(i, j) && mask2.get(i, j);
			}
		};
	}

	public static DefaultBinaryMask2D or(BinaryMask2D mask1, BinaryMask2D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i, int j) {
				return mask1.get(i, j) || mask2.get(i, j);
			}
		};
	}

	public static DefaultBinaryMask2D xor(BinaryMask2D mask1, BinaryMask2D mask2) {
		return new BinaryOpMask(mask1, mask2) {
			@Override
			public boolean get(int i, int j) {
				return mask1.get(i, j) ^ mask2.get(i, j);
			}
		};
	}

	public static DefaultBinaryMask2D not(BinaryMask2D mask) {
		return new UnaryOpMask(mask) {
			@Override
			public boolean get(int i, int j) {
				return !mask.get(i, j);
			}
		};
	}

	public static DefaultBinaryMask2D translate(final BinaryMask2D mask,
			final int ti, final int tj) {
		return new DefaultBinaryMask2D() {
			@Override
			public boolean get(int i, int j) {
				if (i < ti || j < tj)
					return false;
				return !mask.get(i - ti, j - tj);
			}
		};
	}

	public static DefaultBinaryMask2D fromImage(final BinaryImage image) {
		return new DefaultBinaryMask2D() {
			@Override
			public boolean get(int i, int j) {
				return image.checkPixel(i, j);
			}
		};
	}

	public static int count(final BinaryMask2D mask, final Box.Int box) {
		class Count extends Operation2D {
			int count = 0;

			protected Count() {
				super(box.left, box.right, box.top, box.bottom);
			}

			@Override
			protected void compute(int i, int j) {
				count += mask.get(i, j) ? 1 : 0;
			}
		}
		Count count = new Count();
		count.execute();
		return count.count;
	}

	private static abstract class BinaryOpMask extends DefaultBinaryMask2D {
		protected final BinaryMask2D mask1, mask2;

		private BinaryOpMask(BinaryMask2D mask1, BinaryMask2D mask2) {
			this.mask1 = mask1;
			this.mask2 = mask2;
		}
	}

	private static abstract class UnaryOpMask extends DefaultBinaryMask2D {
		protected final BinaryMask2D mask;

		private UnaryOpMask(BinaryMask2D mask) {
			this.mask = mask;
		}
	}

}
