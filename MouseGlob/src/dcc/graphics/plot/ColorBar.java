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

import processing.core.PConstants;
import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.plot.oned.Axis;

public class ColorBar {

	private Axis axis;
	private ColorMap colorMap;

	public ColorBar(Axis axis, ColorMap colorMap) {
		this.axis = axis;
		this.colorMap = colorMap;
	}

	public Paintable getRenderer(int x, int y, int width, int height) {
		return new ColorBarRenderer(x, y, width, height);
	}

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
	}

	public ColorMap getColorMap() {
		return colorMap;
	}

	public void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}

	private class ColorBarRenderer implements Paintable {

		private static final int MARGIN = 20;

		private int x, y;
		private int width, height;

		private ColorBarRenderer(int x, int y, int width, int height) {
			this.x = x + MARGIN;
			this.y = y + MARGIN;
			this.width = width;
			this.height = height - 2 * MARGIN;
		}

		@Override
		public void paint(PGraphics g) {
			g.pushStyle();
			g.pushMatrix();
			g.translate(x, y);
			g.rectMode(PConstants.CORNER);
			g.noFill();
			for (int i = 1; i < height; i++) {
				double k = 1 - (double) (i - 1) / (height - 2);
				int color = colorMap.color(k);
				g.stroke(color);
				g.line(1, i, width - 1, i);
			}
			g.stroke(0);
			g.rect(0, 0, width, height);
			g.fill(0);
			g.textAlign(PConstants.LEFT, PConstants.CENTER);
			double min = axis.getMax();
			double max = axis.getMin();
			for (int i = 0; i <= 10; i++) {
				double value = min + (max - min) * i / 10.0;
				double pos = (1 - axis.getRelativePosition(value))
						* (height - 2);
				g.text(axis.format(value), width + 3, 1 + (float) pos);
			}
			String label = axis.getLabel();
			if (label != null) {
				g.translate(0, height / 2);
				g.rotate(-PConstants.HALF_PI);
				g.textAlign(PConstants.CENTER, PConstants.BOTTOM);
				g.text(label, 0, -2);
			}
			g.popMatrix();
			g.popStyle();
		}

	}

}
