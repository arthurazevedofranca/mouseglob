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

import javax.swing.JMenu;

import dcc.graphics.series.SmoothedSeries;
import dcc.ui.SliderAction;

public class SmoothAction extends SliderAction {

	private static final int NUM_TICKS = 20;

	private final SmoothedSeries smoothedSeries;
	private final double maxSigma;

	private final PlotApplet applet;

	public SmoothAction(SmoothedSeries smoothedSeries, double maxSigma,
			PlotApplet applet) {
		super(0, NUM_TICKS, 0);
		this.smoothedSeries = smoothedSeries;
		this.maxSigma = maxSigma;
		this.applet = applet;
	}

	@Override
	public void valueChanged(int value) {
		if (value > 0) {
			double sigma = maxSigma * (double) value / NUM_TICKS;
			smoothedSeries.smooth(sigma);
		} else {
			smoothedSeries.unsmooth();
		}
		applet.redraw();
	}

	public JMenu getMenu() {
		JMenu smoothMenu = new JMenu("Smooth...");
		smoothMenu.add(getSimpleSlider());
		return smoothMenu;
	}

}
