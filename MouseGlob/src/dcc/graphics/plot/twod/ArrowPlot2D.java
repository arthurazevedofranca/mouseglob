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
package dcc.graphics.plot.twod;

import dcc.graphics.math.Optimizer;
import dcc.graphics.plot.Plot;
import processing.core.PGraphics;

public class ArrowPlot2D extends Plot {

	private int width;
	private int height;
	private int step;
	private VectorField vector;

	protected double max;
	private boolean auto;

	public ArrowPlot2D(int x, int y, int width, int height, boolean auto) {
		super(x, y, width, height);
		vector = new VectorField();
		step = 1;
		max = 1;
		this.auto = auto;
	}

	public void setValues(double[][] x, double[][] y) {
		vector.setValues(x, y);
		update();
	}

	public void setRadial(double[][] magnitude, double[][] angle) {
		vector.setRadial(magnitude, angle);
		update();
	}

	private void update() {
		width = vector.width;
		height = vector.height;

		if (auto) {
			Optimizer opt = new Optimizer(vector.magnitude);
			max = opt.getMaximumValue();
		}
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public void paint(PGraphics g) {
		if (width != 0 && height != 0) {
			g.stroke(255, 0, 255);
			g.pushMatrix();
			g.translate(getPlotX(), getPlotY());
			// g.scale((double) (plotWidth / width), (double) (plotHeight /
			// height));
			paintArrowPlot(g, step);
			g.popMatrix();
		}
	}

	private void paintArrowPlot(PGraphics g, int s) {
		float scale = (float) getPlotWidth() / width;
		for (int i = 0; i < width; i += s)
			for (int j = 0; j < height; j += s) {
				float length = (float) (scale * (vector.magnitude[i][j] / max) * s);
				paintArrow(g, i * scale, j * scale, length,
						(float) vector.angle[i][j]);
			}
	}

	private static void paintArrow(PGraphics g, float x0, float y0,
			float length, float angle) {
		g.pushMatrix();
		g.translate(x0, y0);
		g.rotate(angle);
		g.line(0, 0, length, 0);
		g.line(length, 0, 0.7f * length, 0.2f * length);
		g.line(length, 0, 0.7f * length, -0.2f * length);
		g.popMatrix();
	}

}
