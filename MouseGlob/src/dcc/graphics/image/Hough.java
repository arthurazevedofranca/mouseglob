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

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Color;
import dcc.graphics.math.Optimizer;
import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.Vector;

public class Hough extends ScalarMap {
	private int imageWidth;
	private int imageHeight;
	public int max;
	private int angularSteps;
	private int spatialSteps;
	public int count;

	/**
	 * Performs the Hough Transform of this image for straight line detection.
	 * <p>
	 * <code>r = x0 * cos(theta) + y0 * sin(theta)</code>
	 * 
	 * @param p
	 *            - number of angular steps
	 * @param q
	 *            - number of spatial steps
	 */
	public Hough(int p, int q) {
		super(p, q);
		angularSteps = p;
		spatialSteps = q;
	}

	public void calculate(BinaryImage image) {
		imageWidth = image.width;
		imageHeight = image.height;
		max = (int) Math.hypot(imageWidth, imageHeight);
		count = 0;
		synchronized (v) {
			reset();
			for (int x = 0; x < imageWidth; x++) {
				for (int y = 0; y < imageHeight; y++) {
					if (image.get(x, y) == Color.WHITE) {
						for (int i = 0; i < angularSteps; i++) {
							double theta = i * Math.PI / angularSteps;
							double r = x * Math.cos(theta) + y
									* Math.sin(theta);
							int j = (int) (spatialSteps * (r + max) / (2 * max));
							v[i][j]++;
						}
						count++;
					}
				}
			}
		}
	}

	public List<Vector> getPoints(float threshold) {
		ArrayList<Vector> points = new ArrayList<Vector>();
		for (int i = 0; i < angularSteps; i++) {
			for (int j = 0; j < spatialSteps; j++) {
				if (v[i][j] >= threshold * count) {
					double theta = i * Math.PI / angularSteps;
					double r = j * 2 * (double) max / spatialSteps - max;
					points.add(new Vector(theta, r));
				}
			}
		}
		return points;
	}

	public List<Vector[]> getLines(float threshold) {
		List<Vector> points = getPoints(threshold);
		List<Vector[]> lines = new ArrayList<Vector[]>();

		for (Vector p : points) {
			double theta = p.x;
			double r = p.y;
			double x0, y0, x1, y1;

			if (theta <= Math.PI / 4) {
				x0 = r / Math.cos(theta);
				y0 = 0;
				x1 = x0 - imageHeight * Math.tan(theta);
				y1 = imageHeight;
			} else if (theta <= Math.PI / 2) {
				x0 = 0;
				y0 = r / Math.sin(theta);
				x1 = imageWidth;
				y1 = y0 - imageWidth / Math.tan(theta);
			} else if (theta <= 3 / 2 * Math.PI) {
				x0 = r / Math.cos(theta);
				y0 = 0;
				x1 = x0 - imageHeight * Math.tan(theta);
				y1 = imageHeight;
			} else {
				x0 = 0;
				y0 = r / Math.sin(theta);
				x1 = imageWidth;
				y1 = y0 - imageWidth / Math.tan(theta);
			}

			lines.add(new Vector[] { new Vector(x0, y0), new Vector(x1, y1) });
		}

		return lines;
	}

	public List<Vector> getPoints(int n) {
		ArrayList<Vector> points = new ArrayList<Vector>();
		Optimizer op = new Optimizer(this);
		for (Vector p : op.getLocalMaxima(n)) {
			double theta = (float) (p.x * Math.PI / angularSteps);
			double r = p.y * 2f * max / spatialSteps - max;
			points.add(new Vector(theta, r));
		}
		return points;
	}

	public List<Line> getLines(int n) {
		List<Line> lines = new ArrayList<Line>();
		for (Vector p : getPoints(n))
			lines.add(new Line(p));
		return lines;
	}

	public class Line {

		public final Vector p1, p2;

		private Line(Vector radial) {
			double theta = radial.x;
			double r = radial.y;
			double x0, y0, x1, y1;

			if (theta <= Math.PI / 4) {
				x0 = r / Math.cos(theta);
				y0 = 0;
				x1 = x0 - imageHeight * Math.tan(theta);
				y1 = imageHeight;
			} else if (theta <= Math.PI / 2) {
				x0 = 0;
				y0 = r / Math.sin(theta);
				x1 = imageWidth;
				y1 = y0 - imageWidth / Math.tan(theta);
			} else if (theta <= 3 / 2 * Math.PI) {
				x0 = r / Math.cos(theta);
				y0 = 0;
				x1 = x0 - imageHeight * Math.tan(theta);
				y1 = imageHeight;
			} else {
				x0 = 0;
				y0 = r / Math.sin(theta);
				x1 = imageWidth;
				y1 = y0 - imageWidth / Math.tan(theta);
			}

			p1 = new Vector(x0, y0);
			p2 = new Vector(x1, y1);
		}

		public void paint(PGraphics g) {
			Vector.Float f1 = p1.toFloat(), f2 = p2.toFloat();
			g.line(f1.x, f1.y, f2.x, f2.y);
		}

	}
}
