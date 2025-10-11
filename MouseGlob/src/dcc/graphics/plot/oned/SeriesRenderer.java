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

import dcc.graphics.math.Vector;
import dcc.graphics.series.Series;
import processing.core.PGraphics;

public class SeriesRenderer {

	private final Series series;
	private final int color;
	private int lastPainted;

	public SeriesRenderer(Series series, int color) {
		this.series = series;
		this.color = color;
		lastPainted = 0;
	}

	Series getSeries() {
		return series;
	}

	void paint(PGraphics g, SeriesPlot plot) {
		if (series.isEmpty())
			return;
		g.noFill();
		g.stroke(color);
		for (Vector p : series) {
			if (plot.isWithinBounds(p.x, p.y))
				g.ellipse(plot.getX(p.x), plot.getY(p.y), 5, 5);
		}
	}

	private int paintLine(PGraphics g, SeriesPlot plot, int from) {
		synchronized (series) {
			if (series.isEmpty())
				return 0;
			g.stroke(color);
			int n = series.size();
			if (from >= n)
				return 0;
			Vector p1, p2 = series.getPoint(from);
			for (int i = from + 1; i < n; i++) {
				p1 = p2;
				p2 = series.getPoint(i);
				if (p1 != null && p2 != null && plot.isWithinBounds(p1)
						&& plot.isWithinBounds(p2))
					g.line(plot.getX(p1.x), plot.getY(p1.y), plot.getX(p2.x),
							plot.getY(p2.y));
			}
			return n - 1;
		}
	}

	final void paintLine(PGraphics g, SeriesPlot plot) {
		lastPainted = paintLine(g, plot, 0);
	}

	final void paintIncrement(PGraphics g, SeriesPlot plot) {
		if (plot.needsRepaint())
			lastPainted = 0;
		lastPainted = paintLine(g, plot, lastPainted);
	}

}
