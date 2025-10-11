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
package dcc.graphics.plot.oned;

import processing.core.PConstants;
import processing.core.PGraphics;
import dcc.graphics.plot.oned.Axis.Tick;

public class AxisRenderer {

	private static final int HEAD_LENGTH = 8;
	private static final int TEXT_SPACING = 8;
	static final float TICK_LENGTH = 2;

	private final Axis axis;

	public AxisRenderer(Axis axis) {
		this.axis = axis;
	}

	public void paintHorizontal(PGraphics g, float x, float y, float length) {
		g.pushStyle();
		g.textAlign(PConstants.CENTER, PConstants.TOP);
		g.stroke(0);
		g.fill(0);
		g.pushMatrix();
		g.translate(x, y);
		g.line(0, 0, length + HEAD_LENGTH, 0);
		g.line(length - 4 + HEAD_LENGTH, -2, length + HEAD_LENGTH, 0);
		g.line(length - 4 + HEAD_LENGTH, 2, length + HEAD_LENGTH, 0);

		double ts = correctHorizontalTickSpacing(g, length);
		for (Tick tick : axis.getTicks(ts))
			TickRenderer.paintHorizontal(tick, g, length);

		String label = axis.getLabel();
		if (label != null) {
			g.textAlign(PConstants.CENTER, PConstants.TOP);
			g.text(label, length / 2, getHeight(g) + 2);
		}
		g.popMatrix();
		g.popStyle();
	}

	public void paintVertical(PGraphics g, float x, float y, float length) {
		g.pushStyle();
		g.textAlign(PConstants.RIGHT, PConstants.CENTER);
		g.stroke(0);
		g.fill(0);
		g.pushMatrix();
		g.translate(x, y);
		g.line(0, length, 0, -HEAD_LENGTH);
		g.line(-2, 4 - HEAD_LENGTH, 0, -HEAD_LENGTH);
		g.line(2, 4 - HEAD_LENGTH, 0, -HEAD_LENGTH);

		double ts = correctVerticalTickSpacing(g, length);
		for (Tick tick : axis.getTicks(ts))
			TickRenderer.paintVertical(tick, g, length);

		String label = axis.getLabel();
		if (label != null) {
			g.translate(-getWidth(g, length), length / 2);
			g.rotate(-PConstants.HALF_PI);
			g.textAlign(PConstants.CENTER, PConstants.BOTTOM);
			g.text(label, 0, -2);
		}
		g.popMatrix();
		g.popStyle();
	}

	public void paintHorizontalGrid(PGraphics g, float x, float y, float width,
			float height) {
		g.pushStyle();
		g.stroke(220);
		g.pushMatrix();
		g.translate(x, y);
		double ts = correctVerticalTickSpacing(g, height);
		for (Tick tick : axis.getTicks(ts))
			TickRenderer.paintHorizontalGrid(tick, g, width, height);
		g.popMatrix();
		g.popStyle();
	}

	public void paintVerticalGrid(PGraphics g, float x, float y, float width,
			float height) {
		g.pushStyle();
		g.stroke(220);
		g.pushMatrix();
		g.translate(x, y);
		double ts = correctHorizontalTickSpacing(g, width);
		for (Tick tick : axis.getTicks(ts))
			TickRenderer.paintVerticalGrid(tick, g, width, height);
		g.popMatrix();
		g.popStyle();
	}

	public float getHeight(PGraphics g) {
		float ascent = g.textAscent();
		float descent = g.textDescent();
		float height = ascent + descent;
		return TICK_LENGTH + 1 + height;
	}

	public float getWidth(PGraphics g, float length) {
		return getTextWidth(g, length) + 1 + TICK_LENGTH;
	}

	private float getTextWidth(PGraphics g, float length) {
		float width = 0;
		double ts = correctVerticalTickSpacing(g, length);
		for (Tick tick : axis.getTicks(ts)) {
			float tickWidth = g.textWidth(tick.getText());
			if (tickWidth > width)
				width = tickWidth;
		}
		return width;
	}

	private double correctHorizontalTickSpacing(PGraphics g, float length) {
		float textWidth = getTextWidth(g, length);
		return axis.correctTickSpacing(length, textWidth + TEXT_SPACING);
	}

	private double correctVerticalTickSpacing(PGraphics g, float length) {
		float textHeight = g.textAscent() + g.textDescent();
		return axis.correctTickSpacing(length, textHeight + TEXT_SPACING);
	}

}
