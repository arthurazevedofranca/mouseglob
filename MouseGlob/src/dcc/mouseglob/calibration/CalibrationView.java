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
package dcc.mouseglob.calibration;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.module.AbstractView;

/**
 * @author Daniel Coelho de Castro
 */
public class CalibrationView extends AbstractView<CalibrationModule> {
	private static final Dimension FIELD_SIZE = new Dimension(60, 20);
	private static final NumberFormat FORMAT = DecimalFormat.getInstance();
	{
		FORMAT.setMaximumFractionDigits(3);
	}

	@Inject
	private CalibrationController controller;

	private JTextField scaleField;

	@Override
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar("Calibration");

		scaleField = new JTextField();
		scaleField.setMaximumSize(FIELD_SIZE);
		scaleField.setText("-");
		scaleField.setEditable(false);

		toolBar.add(controller.calibrateAction.getToggleButton());
		toolBar.add(controller.pixelsAction.getTextField(FIELD_SIZE));
		toolBar.add(new JLabel(" px"));
		toolBar.addSeparator();
		toolBar.add(controller.centimetersAction.getTextField(FIELD_SIZE));
		toolBar.add(new JLabel(" cm"));
		toolBar.addSeparator();
		toolBar.add(new JLabel("Scale: "));
		toolBar.add(scaleField);
		toolBar.add(new JLabel(" cm/px"));

		return toolBar;
	}

	void setScaleText(String text) {
		scaleField.setText(text);
	}

	void setScaleValue(double value) {
		setScaleText(FORMAT.format(value));
	}
}
