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

public class Histogram {

	private int[] histogram;
	private int pixelCount;
	private int maxPixels;

	public Histogram(int[] pixels) {
		histogram = new int[256];
		pixelCount = pixels.length;

		for (int i = 0; i < pixelCount; i++)
			histogram[pixels[i] & 0xff]++;

		findMax();
	}

	public Histogram(Image image) {
		this(image.pixels);
	}

	private void findMax() {
		maxPixels = 0;
		for (int i = 0; i < 256; i++)
			if (histogram[i] > maxPixels)
				maxPixels = histogram[i];
	}

	public int[] getHistogram() {
		return histogram;
	}

	public int[] getCumulation() {
		int[] cumu = new int[256];
		int sum = 0;
		for (int i = 0; i < 256; i++) {
			sum += histogram[i];
			cumu[i] = sum;
		}
		return cumu;
	}

	public int getPixelCount() {
		return pixelCount;
	}

	public int getMaxPixels() {
		return maxPixels;
	}

	public int getMinValue() {
		for (int i = 0; i < 256; i++)
			if (histogram[i] > 0)
				return i;
		return 0;
	}

	public int getMinValue(double quantile) {
		int sum = 0;
		for (int i = 0; i < 256; i++) {
			sum += histogram[i];
			if (sum > pixelCount * quantile)
				return i;
		}
		return 0;
	}

	public int getMaxValue() {
		for (int i = 255; i >= 0; i--)
			if (histogram[i] > 0)
				return i;
		return 0;
	}

	public int getMaxValue(double quantile) {
		int sum = 0;
		for (int i = 255; i >= 0; i--) {
			sum += histogram[i];
			if (sum > pixelCount * quantile)
				return i;
		}
		return 0;
	}

	public int getOtsuThreshold() {
		double[] vars = getVariances();
		double max = 0;
		int threshold = 0;

		for (int i = 0; i < 256; i++) {
			if (vars[i] > max) {
				max = vars[i];
				threshold = i;
			}
		}

		return threshold;
	}

	public int getBiasedThreshold(double[] bias) {
		double[] vars = getVariances();
		double maxVar = 0;
		double max = 0;
		int threshold = 0;

		for (double var : vars)
			if (var > maxVar)
				maxVar = var;

		for (int i = 0; i < 256; i++) {
			if (vars[i] + bias[i] * maxVar > max) {
				max = vars[i] + bias[i] * maxVar;
				threshold = i;
			}
		}

		return threshold;
	}

	public double[] getVariances() {
		double sum = 0;
		for (int i = 0; i < 256; i++)
			sum += i * histogram[i];
		sum /= pixelCount;

		double sumB = 0;
		double wB = 0;
		double[] vars = new double[256];

		for (int i = 0; i < 256; i++) {
			double fraction = (double) histogram[i] / pixelCount;
			wB += fraction;
			if (wB == 0)
				continue;
			if (wB == 1)
				break;

			sumB += fraction;

			double meanB = sumB / wB;
			double meanF = (sum - sumB) / (1 - wB);

			vars[i] = wB * (1 - wB) * (meanB - meanF) * (meanB - meanF);
		}

		return vars;
	}

}
