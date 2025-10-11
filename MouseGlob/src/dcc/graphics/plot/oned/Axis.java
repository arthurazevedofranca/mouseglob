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

import java.util.ArrayList;
import java.util.List;

public class Axis {

	public static Axis autoscalingMin(double max, double tickSpacing) {
		return new Axis(max, max, tickSpacing, true, false);
	}

	public static Axis autoscalingMax(double min, double tickSpacing) {
		return new Axis(min, min, tickSpacing, false, true);
	}

	public static Axis autoscaling(double tickSpacing) {
		return new Axis(0, 0, tickSpacing, true, true);
	}

	protected double min, max;
	protected boolean autoMin = false, autoMax = false;
	private final double tickSpacing;
	private double fixedPoint = Double.POSITIVE_INFINITY;

	private String format = null;
	private boolean isInteger = false;
	protected boolean invert = false;

	private String label;

	public Axis(double min, double max) {
		this(min, max, (max - min) / 10);
	}

	public Axis(int min, int max) {
		this(min, max, (max - min) / 10);
	}

	public Axis(double min, double max, double tickSpacing) {
		this(min, max, tickSpacing, false, false);
	}

	protected Axis(double min, double max, double tickSpacing, boolean autoMin,
			boolean autoMax) {
		this.min = min;
		this.max = max;
		this.tickSpacing = tickSpacing;
		this.autoMin = autoMin;
		this.autoMax = autoMax;
	}

	public double getMin() {
		return min;
	}

	public Axis setMin(double min) {
		this.min = min;
		return this;
	}

	public double getMax() {
		return max;
	}

	public Axis setMax(double max) {
		this.max = max;
		return this;
	}

	public boolean isAutoMin() {
		return autoMin;
	}

	public boolean isAutoMax() {
		return autoMax;
	}

	public Axis setFixedPoint(double fixedPoint) {
		this.fixedPoint = fixedPoint;
		return this;
	}

	public Axis setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public float getRelativePosition(double value) {
		if (min == max)
			return 0.5f;
		if (invert)
			return (float) ((max - value) / (max - min));
		return (float) ((value - min) / (max - min));
	}

	public double getValue(double position) {
		if (invert)
			return max - position * (max - min);
		return min + position * (max - min);
	}

	public boolean isWithinBounds(double value) {
		return value >= min && value <= max && !Double.isNaN(value);
	}

	public double getRange() {
		return max - min;
	}

	public Axis setInvert(boolean invert) {
		this.invert = invert;
		return this;
	}

	public boolean isInverted() {
		return invert;
	}

	public Axis setFormat(String format) {
		this.format = format;
		isInteger = format.indexOf('d') != -1;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public Tick getTick(double value) {
		return new Tick(this, value);
	}

	List<Tick> getTicks(double spacing) {
		List<Tick> ticks = new ArrayList<>();
		double first = getStartingTick(spacing);
		for (double value = first; value <= max; value += spacing)
			ticks.add(getTick(value));
		return ticks;
	}

	private double getStartingTick(double spacing) {
		if (fixedPoint != Double.POSITIVE_INFINITY)
			return fixedPoint + spacing
					* Math.ceil((min - fixedPoint) / spacing);
		return min;
	}

	double correctTickSpacing(float length, float graphicSpacing) {
		if (max <= min)
			return tickSpacing;
		double[] mult = { 1, 2, 5 };
		double minSpacing = (max - min) * (graphicSpacing / length);
		if (tickSpacing < minSpacing) {
			double spacing = tickSpacing;
			while (spacing < minSpacing) {
				for (int i = 0; i < mult.length; i++) {
					if (spacing * mult[i] > minSpacing)
						return spacing * mult[i];
				}
				spacing *= 10;
			}
			return spacing;
		} else {
			double spacing = tickSpacing;
			double prevSpacing = spacing;
			while (spacing > minSpacing) {
				for (int i = 0; i < mult.length; i++) {
					if (spacing / mult[i] < minSpacing)
						return prevSpacing;
					prevSpacing = spacing / mult[i];
				}
				spacing /= 10;
			}
			return prevSpacing;
		}
	}

	double correctTickSpacing2(float length, float minSpacing) {
		double numFixedTicks = Math.ceil((max - min) / tickSpacing);
		if (max <= min)
			numFixedTicks = 1;
		double numDesiredTicks = Math.ceil(length / minSpacing);
		if (numFixedTicks > numDesiredTicks)
			return tickSpacing * Math.ceil(numFixedTicks / numDesiredTicks);
		return tickSpacing / Math.floor(numDesiredTicks / numFixedTicks);
	}

	public String format(double value) {
		if (format == null) {
			if (value == (int) value)
				return String.valueOf((int) value);
			return String.valueOf(value);
		} else {
			if (isInteger)
				return String.format(format, (int) Math.round(value));
			return String.format(format, value);
		}
	}

	@Override
	public String toString() {
		return "[" + min + ", " + max + ": " + tickSpacing + "]";
	}

	public static class Tick {

		private final Axis axis;
		private final double value;
		private final String text;

		private Tick(Axis axis, double value) {
			this.axis = axis;
			this.value = value;
			text = axis.format(value);
		}

		String getText() {
			return text;
		}

		float getRelativePosition() {
			return axis.getRelativePosition(value);
		}

	}

}
