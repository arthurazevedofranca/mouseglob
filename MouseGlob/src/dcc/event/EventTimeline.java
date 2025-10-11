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
package dcc.event;

import java.util.List;

import processing.core.PGraphics;
import dcc.event.EventManager.TimedEventManager;
import dcc.graphics.Box;
import dcc.graphics.Color;
import dcc.graphics.plot.Plot;
import dcc.graphics.plot.oned.Axis;
import dcc.graphics.plot.oned.AxisRenderer;

public class EventTimeline extends Plot {

	private static final float MARGIN = 10;

	private float timelineX;
	private float timelineLength;
	private float timelineHeight;
	private float barHeight;
	private int[] colors;

	private boolean positionUpToDate = false;

	private List<? extends TimedEventClass> eventClasses;
	private TimedEventManager<?> listener;
	private Axis timeAxis;
	private AxisRenderer timeRenderer;

	public EventTimeline(TimedEventManager<?> listener, int x, int y,
			int width, int height) {
		super(x, y, width, height);

		this.listener = listener;
		timeAxis = new Axis(0, 0, 1).setFormat("%.2f").setLabel("Time (s)");
		timeRenderer = new AxisRenderer(timeAxis);
	}

	public void setMinTime(long minTime) {
		timeAxis.setMin((double) minTime / 1000);
	}

	public void setMaxTime(long maxTime) {
		timeAxis.setMax((double) maxTime / 1000);
	}

	@Override
	public void paint(PGraphics g) {
		eventClasses = listener.getEventClasses();
		updateColors(eventClasses.size());
		g.pushStyle();
		g.rectMode(PGraphics.CORNERS);
		g.textAlign(PGraphics.RIGHT, PGraphics.CENTER);
		if (!positionUpToDate) {
			timelineX = getMaxDescriptionWidth(g) + MARGIN;
			timelineLength = getPlotWidth() - timelineX - 2 * MARGIN;
			timelineHeight = getPlotHeight() - 3 * MARGIN
					- timeRenderer.getHeight(g);
			barHeight = timelineHeight / eventClasses.size();
			positionUpToDate = true;
		}
		g.pushMatrix();
		g.translate(getPlotX() + timelineX, getPlotY() + MARGIN);
		timeRenderer.paintVerticalGrid(g, 0, 0, timelineLength,
				getPlotAreaHeight(g));

		int i = 0;
		for (TimedEventClass eventClass : eventClasses) {
			g.fill(0);
			g.text(eventClass.getDescription(), -2, barHeight * (i + 0.5f));
			g.noStroke();
			g.fill(colors[i]);
			for (TimedEvent event : eventClass.getEvents())
				paintBox(getEventBox(event, i), g);
			i++;
		}
		g.popStyle();
		timeRenderer.paintHorizontal(g, 0, timelineHeight, timelineLength);
		g.popMatrix();

		paintLabel(g);
	}

	@Override
	protected Probe updateProbe(Probe probe, float x, float y) {
		if (eventClasses.isEmpty())
			return null;
		double tx = x - getPlotX() - timelineX;
		double ty = y - getPlotY() - MARGIN;
		double xPosition = tx / timelineLength;
		if (xPosition < 0 || xPosition > 1 || ty < 0 || ty >= timelineHeight)
			return null;
		double time = timeAxis.getValue(xPosition);
		int eventClassIndex = (int) (ty / barHeight);
		TimedEventClass eventClass = eventClasses.get(eventClassIndex);
		TimedEvent event = eventClass.getEventAt((long) (time * 1000));
		if (event == null)
			return null;
		String description = event.getDescription();
		String text = "";
		if (description != null)
			text = description + ": ";
		text += String.format("%.2fs - %.2fs", event.getStartTime() / 1000.0,
				event.getEndTime() / 1000.0);
		probe.setText(text).setPosition(x, y);
		return probe;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		positionUpToDate = false;
	}

	private void paintBox(Box box, PGraphics g) {
		if (box != null)
			g.rect((float) box.left, (float) box.top, (float) box.right,
					(float) box.bottom);
	}

	private Box getEventBox(TimedEvent event, int i) {
		double startTime = event.getStartTime();
		double endTime = event.getEndTime();
		endTime = endTime > 0 ? endTime : System.currentTimeMillis();
		startTime /= 1000;
		endTime /= 1000;
		if (startTime <= timeAxis.getMax() && endTime >= timeAxis.getMin()) {
			startTime = Math.max(startTime, timeAxis.getMin());
			endTime = Math.min(endTime, timeAxis.getMax());
			float startX = getX(startTime);
			float endX = getX(endTime);
			return Box.fromCorners(startX, barHeight * i, endX, barHeight
					* (i + 1));
		}
		return null;
	}

	private float getX(double time) {
		return timelineLength * timeAxis.getRelativePosition(time);
	}

	private float getPlotAreaHeight(PGraphics g) {
		return getPlotHeight() - 2 * MARGIN - timeRenderer.getHeight(g);
	}

	private float getMaxDescriptionWidth(PGraphics g) {
		float max = 0;
		for (TimedEventClass eventClass : eventClasses)
			max = Math.max(max, g.textWidth(eventClass.getDescription()));
		return max + 2;
	}

	private void updateColors(int n) {
		colors = new int[n];
		for (int i = 0; i < n; i++)
			colors[i] = Color.hsv((i % n) * (360 / n), 1, 1);
	}

}
