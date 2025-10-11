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
package dcc.ui;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class SliderAction implements ChangeListener {
	private String name;
	private int min;
	private int max;
	private int value;
	private boolean enabled;
	private boolean borderSet = false;
	private boolean valueIsAdjusting;
	private ArrayList<JSlider> sliders;

	public SliderAction(String name, int min, int max, int value) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
		enabled = true;
		sliders = new ArrayList<JSlider>();
	}

	public SliderAction(int min, int max, int value) {
		this(null, min, max, value);
	}

	public final int getMinimum() {
		return min;
	}

	public final void setMinimum(int min) {
		this.min = min;
		for (JSlider slider : sliders) {
			slider.setMinimum(min);
			updateTickSpacing(slider);
		}
		if (value < min)
			setValue(min);
	}

	public final int getMaximum() {
		return max;
	}

	public final void setMaximum(int max) {
		this.max = max;
		for (JSlider slider : sliders) {
			slider.setMaximum(max);
			updateTickSpacing(slider);
		}
		if (value > max)
			setValue(max);
	}

	public final int getValue() {
		return value;
	}

	@Override
	public final void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		setValue(source.getValue());
		setEnabled(source.isEnabled());
	}

	public abstract void valueChanged(int value);

	public final void setValue(int value) {
		this.value = value;
		for (JSlider slider : sliders) {
			slider.setValue(value);
			if (borderSet)
				setBorder(slider, value);
		}
		valueChanged(value);
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (JSlider slider : sliders)
			slider.setEnabled(enabled);
	}

	public final boolean isEnabled() {
		return enabled;
	}

	private void setBorder(JSlider slider, int value) {
		String title = (name != null ? name + ": " : "") + value;
		slider.setBorder(BorderFactory.createTitledBorder(title));
	}

	private void updateTickSpacing(JSlider slider) {
		slider.setMinorTickSpacing((max - min) / 32);
		slider.setMajorTickSpacing((max - min) / 4);
	}

	public final boolean isValueAdjusting() {
		return valueIsAdjusting;
	}

	public void valueStartedAdjusting(int value) {
	}

	public void valueFinishedAdjusting(int value) {
	}

	public final JSlider getSimpleSlider(Dimension size) {
		@SuppressWarnings("serial")
		JSlider slider = new JSlider(min, max, value) {
			@Override
			public void setValueIsAdjusting(boolean b) {
				super.setValueIsAdjusting(b);

				if (b && !valueIsAdjusting)
					valueStartedAdjusting(value);

				else if (!b && valueIsAdjusting)
					valueFinishedAdjusting(value);

				valueIsAdjusting = b;
			}
		};

		slider.addChangeListener(this);
		sliders.add(slider);

		slider.setVisible(true);
		slider.setPreferredSize(size);
		slider.setMaximumSize(size);
		slider.setEnabled(isEnabled());

		return slider;
	}

	public final JSlider getSimpleSlider() {
		return getSimpleSlider(null);
	}

	public final JSlider getSlider(Dimension size, int minorTickSpacing,
			int majorTickSpacing) {
		JSlider slider = getSimpleSlider(size);
		boolean showTicks = true;
		if (showTicks) {
			slider.setMinorTickSpacing(minorTickSpacing);
			slider.setMajorTickSpacing(majorTickSpacing);
		}
		slider.setPaintTicks(showTicks);
		slider.setPaintLabels(showTicks);
		setBorder(slider, getValue());
		borderSet = true;

		return slider;
	}

	public final JSlider getSlider(Dimension size) {
		int range = max - min;
		return getSlider(size, range / 32, range / 4);
	}

	public final JSlider getSlider() {
		return getSlider(null);
	}

}
