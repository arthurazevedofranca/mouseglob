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
package dcc.graphics;

public final class Color {

	public static final int BLACK = 0xff000000;
	public static final int GRAY = 0xff808080;
	public static final int WHITE = 0xffffffff;
	public static final int RED = 0xffff0000;
	public static final int GREEN = 0xff00ff00;
	public static final int BLUE = 0xff0000ff;
	public static final int YELLOW = RED | GREEN;
	public static final int CYAN = GREEN | BLUE;
	public static final int MAGENTA = RED | BLUE;

	private Color() {
	}

	public static final int alpha(int c, int a) {
		return (a << 24) | (c & 0xffffff);
	}

	public static final int alpha(int c, double a) {
		return alpha(c, a * 255);
	}

	public static final int scale(int x, double s) {
		return rgb((int) (r(x) * s), (int) (g(x) * s), (int) (b(x) * s));
	}

	/**
	 * Map x from [c,d] to [a,b].
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static final int map(int x, int a, int b, int c, int d) {
		return (int) ((x - c) * ((double) (b - a) / (d - c)) + a);
	}

	public static final int r(int c) {
		return (c >> 16) & 0xff;
	}

	public static final int g(int c) {
		return (c >> 8) & 0xff;
	}

	public static final int b(int c) {
		return c & 0xff;
	}

	public static final int rgb(int r, int g, int b) {
		return BLACK | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}

	public static final int rgb(double r, double g, double b) {
		int ir = (int) (255 * r) & 0xff;
		int ig = (int) (255 * g) & 0xff;
		int ib = (int) (255 * b) & 0xff;
		return rgb(ir, ig, ib);
	}

	public static final int argb(int a, int r, int g, int b) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static final int argb(double a, double r, double g, double b) {
		int ia = (int) (255 * a) & 0xff;
		int ir = (int) (255 * r) & 0xff;
		int ig = (int) (255 * g) & 0xff;
		int ib = (int) (255 * b) & 0xff;
		return (ia << 24) | (ir << 16) | (ig << 8) | ib;
	}

	public static final int rgbCorrect(int r, int g, int b) {
		r = constrain(r, 0, 255);
		g = constrain(g, 0, 255);
		b = constrain(b, 0, 255);
		return rgb(r, g, b);
	}

	public static final int rgbCorrect(double r, double g, double b) {
		r = constrain(r, 0, 1);
		g = constrain(g, 0, 1);
		b = constrain(b, 0, 1);
		return rgb(r, g, b);
	}

	public static final int grayscale(int c) {
		return gray(r(c), g(c), b(c));
	}

	public static final int gray(int l) {
		l = constrain(l, 0, 255);
		return rgb(l, l, l);
	}

	public static final int gray(double v) {
		return gray((int) (255.0 * v));
	}

	public static final int gray(int r, int g, int b) {
		return gray((r + g + b) / 3);
	}

	public static final int luma(int c) {
		return luma(r(c), g(c), b(c));
	}

	public static final int luma(int r, int g, int b) {
		return gray((7 * r + 23 * g + 2 * b) >> 5);
	}

	/**
	 * @param H
	 *            in [0, 360)
	 * @param S
	 *            in [0, 1]
	 * @param V
	 *            in [0, 1]
	 * @return
	 */
	public static final int hsv(double H, double S, double V) {
		if (Double.isNaN(H))
			return BLACK;

		double C = V * S;
		double Hp = H / 60;
		double X = C * (1 - Math.abs(Hp % 2 - 1));

		double r, g, b;
		r = g = b = 0;

		switch ((int) Hp) {
		case 0:
			r = C;
			g = X;
			break;
		case 1:
			r = X;
			g = C;
			break;
		case 2:
			g = C;
			b = X;
			break;
		case 3:
			g = X;
			b = C;
			break;
		case 4:
			r = X;
			b = C;
			break;
		case 5:
		case 6:
			r = C;
			b = X;
			break;
		}

		double m = V - C;

		return rgb(r + m, g + m, b + m);
	}

	private static final int constrain(int x, int min, int max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

	private static final double constrain(double x, double min, double max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

}
