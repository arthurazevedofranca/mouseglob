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

import java.util.Iterator;

import dcc.graphics.Box;
import dcc.graphics.Color;
import dcc.graphics.math.Map;
import dcc.graphics.math.async.Operation1D;
import dcc.graphics.pool.IntegerArrayPool;

public class Image implements Map<Integer>, Cloneable {

	protected int width;
	protected int height;
	protected int[] pixels;

	public Image(int width, int height, int[] pixels) {
		if (pixels.length != width * height)
			throw new IllegalArgumentException("Wrong number of pixels.");
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	public Image(int width, int height) {
		this(width, height, IntegerArrayPool.get(width * height));
	}

	Image(Image image) {
		this(image.width, image.height, image.pixels);
	}

	public final void setSize(int newWidth, int newHeight) {
		if (width != newWidth || height != newHeight) {
			release();
			width = newWidth;
			height = newHeight;
			pixels = IntegerArrayPool.get(width * height);
		}
	}

	public final void setSize(Image image) {
		setSize(image.width, image.height);
	}

	@Override
	public Image clone() {
		return copy(null);
	}

	public Image copy(Image destination) {
		destination = check(destination);
		System.arraycopy(pixels, 0, destination.pixels, 0, pixels.length);
		return destination;
	}

	public Image copy(Image destination, int x0, int y0, int x1, int y1) {
		x1 = Math.min(x1, width);
		y1 = Math.min(y1, width);
		int w = x1 - x0 + 1;
		int h = y1 - y0 + 1;

		if (destination == null)
			destination = new Image(w, h);

		else if (destination.pixels.length != w * h) {
			destination.release();
			destination.pixels = IntegerArrayPool.get(w * h);
			destination.width = w;
			destination.height = h;
		}

		for (int j = 0; j < h; j++)
			System.arraycopy(pixels, x0 + (j + y0) * width, destination.pixels,
					j * w, w);

		return destination;
	}

	public Image copy(Image destination, Box box) {
		int x0 = (int) box.left;
		int y0 = (int) box.top;
		int x1 = (int) box.right;
		int y1 = (int) box.bottom;
		return copy(destination, x0, y0, x1, y1);
	}

	@Override
	public final Integer get(int x, int y) {
		return pixels[x + y * width];
	}

	public final void set(int x, int y, int c) {
		pixels[x + y * width] = c;
	}

	@Override
	public Image get(int x, int y, int w, int h) {
		int[] capture = new int[w * h];

		for (int j = 0; j < h; j++)
			System.arraycopy(pixels, x + (j + y) * width, capture, j * w, w);

		return new Image(w, h, capture);
	}

	public Image get(Box.Int box) {
		int x = box.left;
		int y = box.top;
		int w = box.width + 1;
		int h = box.height + 1;
		return get(x, y, w, h);
	}

	public Box getBox() {
		return Box.fromSize(width - 1, height - 1);
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public boolean dimensionsMatch(Map<?> otherMap) {
		return this.getWidth() == otherMap.getWidth()
				&& this.getHeight() == otherMap.getHeight();
	}

	public Image filter(double[][] filter, Image destination) {
		destination = check(destination);

		int filterWidth = filter[0].length;
		int filterHeight = filter.length;

		int r, g, b;
		int minI, maxI, minJ, maxJ;
		for (int x = 0; x < width; x++) {
			minI = Math.max(0, filterWidth / 2 - x);
			maxI = Math.min(filterWidth, filterWidth / 2 + (width - 1 - x));
			for (int y = 0; y < height; y++) {
				minJ = Math.max(0, filterHeight / 2 - y);
				maxJ = Math.min(filterHeight, filterHeight / 2
						+ (height - 1 - y));

				r = 0;
				g = 0;
				b = 0;

				for (int i = minI; i < maxI; i++) {
					for (int j = minJ; j < maxJ; j++) {
						int p = get(x + i - filterWidth / 2, y + j
								- filterHeight / 2);

						r += Color.r(p) * filter[j][i];
						g += Color.g(p) * filter[j][i];
						b += Color.b(p) * filter[j][i];
					}
				}

				r = Math.abs(r);
				g = Math.abs(g);
				b = Math.abs(b);

				destination.set(x, y, Color.rgb(r, g, b));
			}
		}

		return destination;
	}

	public Image operate(Image image, final Operator operator, Image destination) {
		destination = check(destination);
		final int[] img = image.pixels, dst = destination.pixels;
		new Operation1D(pixels.length) {
			@Override
			protected void compute(int i) {
				dst[i] = operatePixels(pixels[i], img[i], operator);
			}
		}.execute();
		return this;
	}

	public Image operate(int x, Operator operator, Image destination) {
		destination = check(destination);
		for (int i = 0; i < pixels.length; i++)
			destination.pixels[i] = operatePixels(pixels[i], x, operator);
		return this;
	}

	public Image blur(double sigma, Image destination) {
		return filter(FilterFactory.gaussianX(sigma), destination).filter(
				FilterFactory.gaussianY(sigma), destination);
	}

	public Image brighten(int x, Image destination) {
		destination = check(destination);
		for (int i = 0; i < pixels.length; i++) {
			int r = Operator.OFFSET.operate(Color.r(pixels[i]), x);
			int g = Operator.OFFSET.operate(Color.g(pixels[i]), x);
			int b = Operator.OFFSET.operate(Color.b(pixels[i]), x);
			pixels[i] = Color.rgb(r, g, b);
		}

		return this;
	}

	public Image darken(int x, Image destination) {
		return brighten(-x, destination);
	}

	public Image invert(Image destination) {
		for (int i = 0; i < pixels.length; i++)
			destination.pixels[i] = (~pixels[i]) | Color.BLACK;
		return this;
	}

	public GrayscaleImage grayscale(GrayscaleImage destination) {
		destination = check(destination);
		final int[] dst = destination.pixels;
		new Operation1D(pixels.length) {
			@Override
			protected void compute(int i) {
				dst[i] = Color.grayscale(pixels[i]);
			}
		}.execute();
		return destination;
	}

	public GrayscaleImage luminance(GrayscaleImage destination) {
		destination = check(destination);
		final int[] dst = destination.pixels;
		new Operation1D(pixels.length) {
			@Override
			protected void compute(int i) {
				dst[i] = Color.luma(pixels[i]);
			}
		}.execute();
		return destination;
	}

	public Image add(Image image, Image destination) {
		return operate(image, Operator.ADD, destination);
	}

	public Image subtract(Image image, Image destination) {
		return operate(image, Operator.DIFFERENCE, destination);
	}

	public Histogram getHistogram() {
		return new Histogram(pixels);
	}

	protected int operatePixels(int x, int y, Operator operator) {
		return Color.rgb(operator.operate(Color.r(x), Color.r(y)),
				operator.operate(Color.g(x), Color.g(y)),
				operator.operate(Color.b(x), Color.b(y)));
	}

	protected static final int constrain(int x, int min, int max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

	protected Image check(Image buffer) {
		if (buffer == null || !dimensionsMatch(buffer))
			buffer = new Image(width, height);
		return buffer;
	}

	protected GrayscaleImage check(GrayscaleImage buffer) {
		if (buffer == null || !dimensionsMatch(buffer))
			buffer = new GrayscaleImage(width, height);
		return buffer;
	}

	protected BinaryImage check(BinaryImage buffer) {
		if (buffer == null || !dimensionsMatch(buffer))
			buffer = new BinaryImage(width, height);
		return buffer;
	}

	public synchronized void release() {
		width = 0;
		height = 0;
		IntegerArrayPool.release(pixels);
		pixels = null;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new PixelIterator();
	}

	private class PixelIterator implements Iterator<Integer> {

		private int i;

		@Override
		public boolean hasNext() {
			return i < pixels.length;
		}

		@Override
		public Integer next() {
			return pixels[i++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove a value from an array.");
		}

	}

}
