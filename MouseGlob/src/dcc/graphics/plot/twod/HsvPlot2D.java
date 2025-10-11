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

import dcc.graphics.Color;
import dcc.graphics.math.Optimizer;

public class HsvPlot2D extends Plot2D {

	private VectorField vector;

	protected double max;
	private boolean auto;

	public HsvPlot2D(int x, int y, int width, int height, boolean auto) {
		super(x, y, width, height);
		vector = new VectorField();
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
		dataWidth = vector.width;
		dataHeight = vector.height;

		if (auto) {
			Optimizer opt = new Optimizer(vector.magnitude);
			max = opt.getMaximumValue();
		}

		notifyValuesChanged();
	}

	public HsvPlot2D setMax(double max) {
		this.max = max;
		return this;
	}

	@Override
	protected void render(int[] pixels) {
		for (int i = 0; i < dataWidth; i++)
			for (int j = 0; j < dataHeight; j++) {
				double h = vector.angle[i][j];
				double v = Math.min(vector.magnitude[i][j] / max, 1);
				pixels[i + j * dataWidth] = Color.hsv(h, 1, v);
			}
	}

}
