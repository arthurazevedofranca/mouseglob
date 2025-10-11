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

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Color;
import dcc.graphics.Paintable;
import dcc.graphics.PaintingEnvironment;

public abstract class Plot implements Paintable, PaintingEnvironment {
	private int plotX;
	private int plotY;
	private int plotWidth;
	private int plotHeight;

	private Label label;
	private Probe probe;

	private List<RepaintListener> repaintListeners;

	protected List<Paintable> paintables;

	protected Plot(int x, int y, int width, int height) {
		repaintListeners = new ArrayList<Plot.RepaintListener>();
		setPosition(x, y);
		setSize(width, height);
		paintables = new ArrayList<Paintable>();
	}

	public int getPlotX() {
		return plotX;
	}

	public void setPlotX(int x) {
		this.plotX = x;
	}

	public int getPlotY() {
		return plotY;
	}

	public void setPlotY(int y) {
		this.plotY = y;
	}

	public int getPlotWidth() {
		return plotWidth;
	}

	public void setPlotWidth(int width) {
		this.plotWidth = width;
	}

	public int getPlotHeight() {
		return plotHeight;
	}

	public void setPlotHeight(int height) {
		this.plotHeight = height;
	}

	public void setPosition(int x, int y) {
		plotX = x;
		plotY = y;
	}

	public void setSize(int width, int height) {
		plotWidth = width;
		plotHeight = height;
		notifyRepaintNeeded();
	}

	public final Probe probe(float x, float y) {
		if (probe == null)
			probe = new Probe();
		return updateProbe(probe, x, y);
	}

	protected Probe updateProbe(Probe probe, float x, float y) {
		return null;
	}

	public final void setLabel(Label label) {
		this.label = label;
	}

	protected final void paintLabel(PGraphics g) {
		if (label != null) {
			g.pushMatrix();
			g.translate(plotX, plotY);
			label.paint(g);
			g.popMatrix();
		}
	}

	@Override
	public void addPaintable(Paintable paintable) {
		paintables.add(paintable);
		notifyRepaintNeeded();
	}

	@Override
	public void removePaintable(Paintable paintable) {
		paintables.remove(paintable);
		notifyRepaintNeeded();
	}

	public void addRepaintListener(RepaintListener listener) {
		synchronized (repaintListeners) {
			repaintListeners.add(listener);
			listener.onRepaintNeeded(this);
		}
	}

	public void removeRepaintListener(RepaintListener listener) {
		synchronized (repaintListeners) {
			repaintListeners.remove(listener);
		}
	}

	public final void notifyRepaintNeeded() {
		synchronized (repaintListeners) {
			for (RepaintListener listener : repaintListeners)
				listener.onRepaintNeeded(this);
		}
	}

	public final void notifyIncrementNeeded() {
		synchronized (repaintListeners) {
			for (RepaintListener listener : repaintListeners)
				listener.onIncrementNeeded(this);
		}
	}

	public void paintIncrement(PGraphics g) {
		paint(g);
	}

	public boolean needsRepaint() {
		return true;
	}

	public static class Label implements Paintable {
		private String text;
		private int x;
		private int y;
		private int bgColor;
		private int fgColor;

		public Label(String text, int x, int y, int bgColor, int fgColor) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.bgColor = bgColor;
			this.fgColor = fgColor;
		}

		public Label(String text, int x, int y) {
			this(text, x, y, Color.WHITE, Color.BLACK);
		}

		public Label(String text) {
			this(text, 0, 0);
		}

		@Override
		public void paint(PGraphics g) {
			float width = g.textWidth(text) + 2;
			float height = g.textAscent() + 2;
			y = Math.max(y, (int) height - 2);
			g.pushStyle();
			g.rectMode(PGraphics.CORNER);
			g.textAlign(PGraphics.LEFT, PGraphics.BASELINE);
			g.noStroke();
			g.fill(bgColor);
			g.rect(x, y + 2 - height, width, height);
			g.fill(fgColor);
			g.text(text, x + 1, y - 1);
			g.popStyle();
		}

	}

	public static class Probe implements Paintable {

		private String text;
		private float x, y;
		private double xValue, yValue;

		public String getText() {
			return text;
		}

		public Probe setText(String text) {
			this.text = text;
			return this;
		}

		public Probe setPosition(float x, float y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Probe setValues(double xValue, double yValue) {
			this.xValue = xValue;
			this.yValue = yValue;
			return this;
		}

		public double getXValue() {
			return xValue;
		}

		public double getYValue() {
			return yValue;
		}

		@Override
		public void paint(PGraphics g) {
			float width = g.textWidth(text) + 2;
			float height = g.textAscent() + 2;
			g.pushStyle();
			g.rectMode(PGraphics.CORNER);
			g.textAlign(PGraphics.RIGHT, PGraphics.BASELINE);
			g.noStroke();

			float probeX = Math.max(x - 4, width);
			float probeY = Math.max(y - 6, height);
			g.fill(Color.WHITE);
			g.rect(probeX - width, probeY + 2 - height, width, height);
			g.fill(Color.BLACK);
			g.text(text, probeX, probeY);
			g.popStyle();
		}

	}

	public static interface RepaintListener {

		void onRepaintNeeded(Plot plot);

		void onIncrementNeeded(Plot plot);

	}

}
