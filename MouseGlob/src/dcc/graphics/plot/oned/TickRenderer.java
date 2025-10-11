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

import processing.core.PGraphics;
import dcc.graphics.plot.oned.Axis.Tick;

final class TickRenderer {

	static void paintHorizontal(Tick tick, PGraphics g, float axisLength) {
		float pos = axisLength * tick.getRelativePosition();
		g.line(pos, -AxisRenderer.TICK_LENGTH, pos, AxisRenderer.TICK_LENGTH);
		g.text(tick.getText(), pos, AxisRenderer.TICK_LENGTH + 1);
	}

	static void paintVertical(Tick tick, PGraphics g, float axisLength) {
		float pos = axisLength * (1 - tick.getRelativePosition());
		g.line(-AxisRenderer.TICK_LENGTH, pos, AxisRenderer.TICK_LENGTH, pos);
		g.text(tick.getText(), -AxisRenderer.TICK_LENGTH - 1, pos);
	}

	static void paintHorizontalGrid(Tick tick, PGraphics g, float width,
			float axisLength) {
		float pos = axisLength * (1 - tick.getRelativePosition());
		g.line(0, pos, width, pos);
	}

	static void paintVerticalGrid(Tick tick, PGraphics g, float axisLength,
			float height) {
		float pos = axisLength * tick.getRelativePosition();
		g.line(pos, 0, pos, height);
	}

}
