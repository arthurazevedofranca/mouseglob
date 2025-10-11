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
package dcc.mouseglob.report;

import java.awt.Dimension;

import dcc.graphics.plot.Plot;
import dcc.graphics.plot.Plot.RepaintListener;
import dcc.mouseglob.applet.DefaultApplet;

@SuppressWarnings("serial")
public class PlotApplet extends DefaultApplet implements RepaintListener {

	private int width, height;
	private final Plot plot;
	private boolean needsRepaint = true;
	private boolean needsIncrement = false;

	public PlotApplet(Plot plot) {
		this.plot = plot;
		setSize(plot.getPlotWidth(), plot.getPlotHeight());
		plot.addRepaintListener(this);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		plot.setSize(width, height);
	}

	@Override
	public void setup() {
		super.setup();
		size(width, height);
		noLoop();
		background(255);
		plot.paint(g);
		redraw();
	}

	@Override
	public void draw() {
		if (needsRepaint) {
			background(255);
			plot.paint(g);
			needsRepaint = false;
		}
		if (needsIncrement) {
			plot.paintIncrement(g);
			needsIncrement = false;
		}
	}

	@Override
	public void onRepaintNeeded(Plot plot) {
		needsRepaint = true;
		redraw();
	}

	@Override
	public void onIncrementNeeded(Plot plot) {
		needsIncrement = true;
		redraw();
	}

}
