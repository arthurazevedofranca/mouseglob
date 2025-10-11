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
package dcc.graphics.image;

import dcc.graphics.Box;
import dcc.graphics.Color;
import dcc.graphics.math.async.Operation2D;

public class BinaryImage extends GrayscaleImage {
	BinaryImage(int width, int height, int[] pixels) {
		super(width, height, pixels);
	}

	BinaryImage(Image image) {
		super(image.width, image.height, image.pixels);
	}

	public BinaryImage(int width, int height) {
		super(width, height);
	}

	@Override
	public BinaryImage clone() {
		return new BinaryImage(super.clone());
	}

	public BinaryImage copy(BinaryImage destination) {
		return new BinaryImage(super.copy(destination));
	}

	public BinaryImage copy(BinaryImage bufferImage, int x0, int y0, int x1,
			int y1) {
		return new BinaryImage(super.copy(bufferImage, x0, y0, x1, y1));
	}

	public BinaryImage copy(BinaryImage destination, Box box) {
		return new BinaryImage(super.copy(destination, box));
	}

	@Override
	public BinaryImage get(int x, int y, int w, int h) {
		return new BinaryImage(super.get(x, y, w, h));
	}

	@Override
	public BinaryImage get(Box.Int box) {
		return new BinaryImage(super.get(box));
	}

	@Override
	public double value(int x, int y) {
		return Color.r(get(x, y)) > 128 ? 1d : 0d;
	}

	void set(int i, boolean value) {
		pixels[i] = value ? Color.WHITE : Color.BLACK;
	}

	void set(int x, int y, boolean value) {
		pixels[x + y * width] = value ? Color.WHITE : Color.BLACK;
	}

	public BinaryImage dilate(final int[][] element, BinaryImage destination) {
		destination = check(destination);

		final int ew = element[0].length;
		final int eh = element.length;

		final int rw = (ew - 1) / 2;
		final int rh = (eh - 1) / 2;

		final int[] src = pixels, dst = destination.pixels;
		new Operation2D(rw, width - rw, rh, height - rh) {
			@Override
			protected void compute(int x, int y) {
				dst[x + y * width] = src[x + y * width];
				elementLoop: for (int i = -rw; i < ew - rw; i++) {
					for (int j = -rh; j < eh - rh; j++) {
						if (element[j + rh][i + rw] != 0) {
							if (checkPixel(x + i, y + j)) {
								dst[x + y * width] = Color.WHITE;
								break elementLoop;
							}
						}
					}
				}
			}
		}.execute();

		return destination;
	}

	public BinaryImage erode(final int[][] element, BinaryImage destination) {
		destination = check(destination);

		final int ew = element[0].length;
		final int eh = element.length;

		final int rw = (ew - 1) / 2;
		final int rh = (eh - 1) / 2;

		final int[] src = pixels, dst = destination.pixels;
		new Operation2D(rw, width - rw, rh, height - rh) {
			@Override
			protected void compute(int x, int y) {
				dst[x + y * width] = src[x + y * width];
				elementLoop: for (int i = -rw; i < ew - rw; i++) {
					for (int j = -rh; j < eh - rh; j++) {
						if (element[j + rh][i + rw] != 0) {
							if (!checkPixel(x + i, y + j)) {
								dst[x + y * width] = Color.BLACK;
								break elementLoop;
							}
						}
					}
				}
			}
		}.execute();

		return destination;
	}

	public BinaryImage open(int[][] element, BinaryImage destination) {
		return erode(element, null).dilate(element, destination);
	}

	public BinaryImage close(int[][] element, BinaryImage destination) {
		return dilate(element, null).erode(element, destination);
	}

	public BinaryImage hitAndMiss(int[][] element, BinaryImage destination) {
		return erode(element, destination);
	}

	public BinaryImage hitAndMiss(int[][][] elements, BinaryImage destination) {
		BinaryImage result = hitAndMiss(elements[0], destination);

		for (int i = 1; i < elements.length; i++)
			result = result.or(hitAndMiss(elements[i], destination),
					destination);

		return result;
	}

	public BinaryImage perimeter(BinaryImage destination) {
		destination = check(destination);

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				destination.set(x, y, checkBoundary(x, y));

		return destination;
	}

	private boolean checkBoundary(int x, int y) {
		x = constrain(x, 1, width - 2);
		y = constrain(y, 1, height - 2);

		if (!checkPixel(x, y))
			return false;

		if (!checkPixel(x - 1, y - 1))
			return true;

		if (!checkPixel(x, y - 1))
			return true;

		if (!checkPixel(x + 1, y - 1))
			return true;

		if (!checkPixel(x - 1, y))
			return true;

		if (!checkPixel(x + 1, y))
			return true;

		if (!checkPixel(x - 1, y + 1))
			return true;

		if (!checkPixel(x, y + 1))
			return true;

		if (!checkPixel(x + 1, y + 1))
			return true;

		return false;
	}

	public boolean checkPixel(int x, int y) {
		return get(x, y) == Color.WHITE;
	}

	public BinaryImage invert(BinaryImage destination) {
		return new BinaryImage(super.invert(destination));
	}

	public BinaryImage operate(BinaryImage image, Operator operator,
			BinaryImage destination) {
		return new BinaryImage(super.operate(image, operator, destination));
	}

	public BinaryImage and(BinaryImage image, BinaryImage destination) {
		return operate(image, Operator.AND, destination);
	}

	public BinaryImage or(BinaryImage image, BinaryImage destination) {
		return operate(image, Operator.OR, destination);
	}

	public BinaryImage xor(BinaryImage image, BinaryImage destination) {
		return operate(image, Operator.XOR, destination);
	}

}
