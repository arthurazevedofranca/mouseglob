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
import dcc.graphics.math.async.Operation1D;

public class GrayscaleImage extends Image {
	public GrayscaleImage(int width, int height, int[] pixels) {
		super(width, height, pixels);
	}

	GrayscaleImage(Image image) {
		super(image.width, image.height, image.pixels);
	}

	public GrayscaleImage(int width, int height) {
		super(width, height);
	}

	@Override
	public GrayscaleImage clone() {
		return new GrayscaleImage(super.clone());
	}

	public GrayscaleImage copy(GrayscaleImage destination) {
		return new GrayscaleImage(super.copy(destination));
	}

	public GrayscaleImage copy(GrayscaleImage bufferImage, int x0, int y0,
			int x1, int y1) {
		return new GrayscaleImage(super.copy(bufferImage, x0, y0, x1, y1));
	}

	public GrayscaleImage copy(GrayscaleImage bufferImage, Box box) {
		return new GrayscaleImage(super.copy(bufferImage, box));
	}

	public double value(int x, int y) {
		return Color.r(get(x, y)) / 255.0;
	}

	@Override
	public GrayscaleImage get(int x, int y, int w, int h) {
		return new GrayscaleImage(super.get(x, y, w, h));
	}

	@Override
	public GrayscaleImage get(Box.Int box) {
		return new GrayscaleImage(super.get(box));
	}

	public GrayscaleImage filter(double[][] filter, GrayscaleImage destination) {
		destination = check(destination);

		int filterWidth = filter[0].length;
		int filterHeight = filter.length;

		for (int x = filterWidth; x < width - filterWidth; x++) {
			for (int y = filterHeight; y < height - filterHeight; y++) {
				int g = 0;

				for (int i = 0; i < filterWidth; i++)
					for (int j = 0; j < filterHeight; j++)
						g += Color.r(get(x + i - filterWidth / 2, y + j
								- filterHeight / 2))
								* filter[j][i];

				destination.set(x, y, Color.gray(Math.abs(g)));
			}
		}

		return destination;
	}

	public GrayscaleImage brighten(int x, GrayscaleImage destination) {
		destination = check(destination);

		for (int i = 0; i < pixels.length; i++) {
			int r = Operator.OFFSET.operate(Color.r(pixels[i]), x);
			destination.pixels[i] = Color.rgb(r, r, r);
		}

		return destination;
	}

	public GrayscaleImage darken(int x, GrayscaleImage destination) {
		return brighten(-x, destination);
	}

	public GrayscaleImage blur(double sigma, GrayscaleImage destination) {
		return new GrayscaleImage(super.blur(sigma, destination));
	}

	public GrayscaleImage invert(GrayscaleImage destination) {
		return new GrayscaleImage(super.invert(destination));
	}

	public BinaryImage threshold(int threshold, BinaryImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++)
			lut[i] = i >= threshold ? Color.WHITE : Color.BLACK;
		return applyLUT(lut, destination);
	}

	public BinaryImage inverseThreshold(int threshold, BinaryImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++)
			lut[i] = i < threshold ? Color.WHITE : Color.BLACK;
		return applyLUT(lut, destination);
	}

	public BinaryImage bandThreshold(int center, int threshold,
			BinaryImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++) {
			int diff = Math.abs(i - Color.b(center));
			lut[i] = diff >= threshold ? Color.WHITE : Color.BLACK;
		}
		return applyLUT(lut, destination);
	}

	public GrayscaleImage trinarize(int low, int high,
			GrayscaleImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++) {
			if (i >= high)
				lut[i] = Color.WHITE;
			else if (i >= low)
				lut[i] = Color.GRAY;
			else
				lut[i] = Color.BLACK;
		}
		return applyLUT(lut, destination);
	}

	public static BinaryImage hysteresis(GrayscaleImage trin) {
		boolean change = true;

		while (change) {
			change = false;
			for (int x = 1; x < trin.width - 1; x++) {
				for (int y = 1; y < trin.height - 1; y++) {
					if (trin.get(x, y) == Color.WHITE) {
						if (ifSet(trin, x + 1, y, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x - 1, y, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x, y + 1, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x, y - 1, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x + 1, y + 1, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x - 1, y - 1, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x - 1, y + 1, Color.GRAY, Color.WHITE))
							change = true;

						if (ifSet(trin, x + 1, y - 1, Color.GRAY, Color.WHITE))
							change = true;
					}
				}
			}
			if (change) {
				for (int x = trin.width - 2; x > 0; x--) {
					for (int y = trin.height - 2; y > 0; y--) {
						if (trin.get(x, y) == Color.WHITE) {
							if (ifSet(trin, x + 1, y, Color.GRAY, Color.WHITE))
								change = true;

							if (ifSet(trin, x - 1, y, Color.GRAY, Color.WHITE))
								change = true;

							if (ifSet(trin, x, y + 1, Color.GRAY, Color.WHITE))
								change = true;

							if (ifSet(trin, x, y - 1, Color.GRAY, Color.WHITE))
								change = true;

							if (ifSet(trin, x + 1, y + 1, Color.GRAY,
									Color.WHITE))
								change = true;

							if (ifSet(trin, x - 1, y - 1, Color.GRAY,
									Color.WHITE))
								change = true;

							if (ifSet(trin, x - 1, y + 1, Color.GRAY,
									Color.WHITE))
								change = true;

							if (ifSet(trin, x + 1, y - 1, Color.GRAY,
									Color.WHITE))
								change = true;
						}
					}
				}
			}
		}

		for (int x = 0; x < trin.width; x++)
			for (int y = 0; y < trin.height; y++)
				ifSet(trin, x, y, Color.GRAY, Color.BLACK);

		return new BinaryImage(trin);
	}

	public BinaryImage hysteresis(int low, int high, GrayscaleImage destination) {
		return hysteresis(trinarize(low, high, destination));
	}

	private static boolean ifSet(GrayscaleImage image, int x, int y,
			int condition, int value) {
		if (image.get(x, y) == condition) {
			image.set(x, y, value);
			return true;
		}
		return false;
	}

	public GrayscaleImage normalize(int min, int max, GrayscaleImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++)
			lut[i] = Color.gray((double) (i - min) / (max - min));

		return applyLUT(lut, destination);
	}

	public GrayscaleImage normalize(double level, GrayscaleImage destination) {
		Histogram h = getHistogram();
		return normalize(h.getMinValue(level / 2), h.getMaxValue(level / 2),
				destination);
	}

	public GrayscaleImage normalize(GrayscaleImage destination) {
		Histogram h = getHistogram();
		return normalize(h.getMinValue(), h.getMaxValue(), destination);
	}

	public GrayscaleImage equalize(GrayscaleImage destination) {
		int[] cumu = new Histogram(this).getCumulation();
		int[] lut = new int[256];

		for (int i = 0; i < 256; i++)
			lut[i] = Color.gray((double) cumu[i] / (width * height));

		return applyLUT(lut, destination);
	}

	public GrayscaleImage operate(GrayscaleImage image, Operator operator,
			GrayscaleImage destination) {
		return new GrayscaleImage(super.operate(image, operator, destination));
	}

	public GrayscaleImage add(GrayscaleImage image, GrayscaleImage destination) {
		return operate(image, Operator.ADD, destination);
	}

	public GrayscaleImage subtract(GrayscaleImage image,
			GrayscaleImage destination) {
		return operate(image, Operator.DIFFERENCE, destination);
	}

	public GrayscaleImage gamma(float gamma, GrayscaleImage destination) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++)
			lut[i] = Color.gray(Math.pow(i / 255.0, gamma));

		return applyLUT(lut, destination);
	}

	@Override
	protected int operatePixels(int x, int y, Operator operator) {
		return Color.gray(operator.operate(Color.b(x), Color.b(y)));
	}

	private GrayscaleImage applyLUT(int[] lut, GrayscaleImage destination) {
		destination = check(destination);
		new LookupTable(lut, destination.pixels).execute();
		return destination;
	}

	private BinaryImage applyLUT(int[] lut, BinaryImage destination) {
		destination = check(destination);
		new LookupTable(lut, destination.pixels).execute();
		return destination;
	}

	private class LookupTable extends Operation1D {

		private final int[] lut, dst;

		protected LookupTable(int[] lut, int[] dst) {
			super(pixels.length);
			this.lut = lut;
			this.dst = dst;
		}

		@Override
		protected void compute(int i) {
			dst[i] = lut[Color.r(pixels[i])];
		}

	}

}
