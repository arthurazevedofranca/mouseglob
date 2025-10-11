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

import processing.core.PGraphics;
import processing.core.PImage;
import dcc.graphics.PImageAdapter;
import dcc.graphics.Paintable;
import dcc.graphics.image.Image;
import dcc.graphics.plot.Plot;

public abstract class Plot2D extends Plot {

	private PImage pImage;
	private boolean valuesChanged;
	protected int dataWidth, dataHeight;
	private boolean invertY;

	protected Plot2D(int x, int y, int width, int height) {
		super(x, y, width, height);
		pImage = new PImage(width, height);
	}

	protected abstract void render(int[] pixels);

	protected void notifyValuesChanged() {
		valuesChanged = true;
		notifyRepaintNeeded();
	}

	public void setInvertY(boolean invertY) {
		this.invertY = invertY;
	}

	@Override
	public final void paint(PGraphics g) {
		if (dataWidth == 0 || dataHeight == 0)
			return;

		if (pImage == null || pImage.width != dataWidth
				|| pImage.height != dataHeight)
			pImage = new PImage(dataWidth, dataHeight);

		if (valuesChanged) {
			render(pImage.pixels);
			pImage.updatePixels();
			valuesChanged = false;
		}

		g.pushMatrix();
		g.translate(getPlotX(), getPlotY());
		if (invertY) {
			g.translate(0, getPlotHeight());
			g.scale(1, -1);
		}
		g.image(pImage, 0, 0, getPlotWidth(), getPlotHeight());
		if (!paintables.isEmpty()) {
			float scaleX = (float) getPlotWidth() / dataWidth;
			float scaleY = (float) getPlotHeight() / dataHeight;
			g.scale(scaleX, scaleY);
			for (Paintable p : paintables)
				p.paint(g);
		}
		g.popMatrix();
		g.removeCache(pImage);
		paintLabel(g);
	}

	@Override
	public final synchronized Probe updateProbe(Probe probe, float x, float y) {
		double tx = x - getPlotX();
		double ty = y - getPlotY();
		double xPosition = tx / getPlotWidth();
		double yPosition = ty / getPlotHeight();
		if (xPosition < 0 || xPosition > 1 || yPosition < 0 || yPosition > 1)
			return null;
		double xValue = xPosition * dataWidth;
		double yValue = yPosition * dataHeight;
		if (invertY)
			yValue = dataHeight - yValue;
		String text = String.format("%f, %f", xValue, yValue);
		probe.setText(text).setPosition(x, y).setValues(xValue, yValue);
		return probe;
	}

	public Image getImage() {
		return PImageAdapter.pImageToImage(pImage);
	}

}
