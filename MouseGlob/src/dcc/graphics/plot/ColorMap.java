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
package dcc.graphics.plot;

import dcc.graphics.Color;

public abstract class ColorMap {

	public static final ColorMap GRAY = new ColorMap() {
		@Override
		protected int getColor(double value) {
			return Color.gray(value);
		}
	};

	public static final ColorMap JET = new ColorMap() {
		@Override
		public int getColor(double value) {
			double fourValue = 4 * value;

			double r = Math.min(fourValue - 1.5f, -fourValue + 4.5f);
			double g = Math.min(fourValue - 0.5f, -fourValue + 3.5f);
			double b = Math.min(fourValue + 0.5f, -fourValue + 2.5f);

			return Color.rgbCorrect(r, g, b);
		}
	};

	public static final ColorMap BLACK_BODY = new PiecewiseLinearColorMap(
			new ColorPoint(0, 0, 0, 0), new ColorPoint(0.4, 1, 0, 0),
			new ColorPoint(0.75, 1, 1, 0), new ColorPoint(1, 1, 1, 1));

	public static final ColorMap HUE = new ColorMap() {
		@Override
		protected int getColor(double value) {
			double H = value * 360;
			double S = 1;
			double V = 1;

			return Color.hsv(H, S, V);
		}
	};

	public static final ColorMap COOL_WARM = new PiecewiseLinearColorMap(
			new ColorPoint(0, 0.230, 0.299, 0.754), new ColorPoint(0.5, 0.865,
					0.865, 0.865), new ColorPoint(1, 0.706, 0.016, 0.150));

	public final int color(double value) {
		return getColor(constrain(value, 0, 1));
	}

	/**
	 * Gets the RGB color associated with the given value.
	 * 
	 * @param value
	 *            - a real value, in the range [0, 1]
	 * @return the color
	 */
	protected abstract int getColor(double value);

	public ColorMap gamma(final double gamma) {
		return new ColorMap() {
			@Override
			protected int getColor(double value) {
				return ColorMap.this.getColor(Math.pow(value, gamma));
			}
		};
	}

	public ColorMap discretize(int bins) {
		int[] colors = new int[bins];
		for (int i = 0; i < bins; i++)
			colors[i] = getColor((double) i / (bins - 1));
		return new DiscreteColorMap(colors);
	}

	private static double constrain(double x, double min, double max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

	private static class RGBColor {

		private final double r, g, b;

		public RGBColor(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public RGBColor lerp(RGBColor c, double k) {
			double lr = r + (c.r - r) * k;
			double lg = g + (c.g - g) * k;
			double lb = b + (c.b - b) * k;
			return new RGBColor(lr, lg, lb);
		}

		public int toInt() {
			return Color.rgb(r, g, b);
		}

	}

	private static class PiecewiseLinearColorMap extends ColorMap {

		private final ColorPoint[] points;

		private PiecewiseLinearColorMap(ColorPoint... points) {
			this.points = points;
		}

		@Override
		protected int getColor(double value) {
			if (value <= points[0].x)
				return points[0].color.toInt();
			if (value >= points[points.length - 1].x)
				return points[points.length - 1].color.toInt();
			for (int i = 1; i < points.length; i++) {
				if (value < points[i].x) {
					return points[i].lerp(points[i - 1], value).toInt();
				}
			}
			return Color.BLACK;
		}

	}

	private static class ColorPoint {

		private final double x;
		private final RGBColor color;

		public ColorPoint(double x, double r, double g, double b) {
			this.x = x;
			color = new RGBColor(r, g, b);
		}

		public RGBColor lerp(ColorPoint p, double value) {
			double k = (value - p.x) / (x - p.x);
			return p.color.lerp(color, k);
		}

	}

	private static class DiscreteColorMap extends ColorMap {

		private final int[] colors;

		private DiscreteColorMap(int[] colors) {
			this.colors = colors;
		}

		@Override
		protected int getColor(double value) {
			int i = (int) (value * colors.length);
			return colors[Math.min(Math.max(i, 0), colors.length - 1)];
		}

	}

}
