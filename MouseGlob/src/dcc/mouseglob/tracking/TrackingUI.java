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
package dcc.mouseglob.tracking;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import dcc.inject.Inject;
import dcc.module.AbstractView;
import dcc.ui.ComponentFactory;

public class TrackingUI extends AbstractView<Tracking> {

	@Inject
	private TrackingController controller;

	@Override
	public JPanel makePanel() {
		JPanel trackingPanel = new JPanel();
		GroupLayout layout = new GroupLayout(trackingPanel);
		trackingPanel.setLayout(layout);

		JSlider sliderThreshold = controller.thresholdAction.getSlider();
		JSlider sliderSize = controller.trackerSizeAction.getSlider();

		JPanel modePanel = makeModePanel();

		JPanel thresholdModePanel = makeThresholdModePanel();

		JButton buttonReset = new JButton(controller.resetAction);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(sliderThreshold)
								.addComponent(thresholdModePanel))
				.addGroup(
						layout.createSequentialGroup().addComponent(sliderSize)
								.addComponent(modePanel)
								.addComponent(buttonReset)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(sliderThreshold)
								.addComponent(thresholdModePanel))
				.addGroup(
						layout.createParallelGroup().addComponent(sliderSize)
								.addComponent(modePanel)
								.addComponent(buttonReset)));

		return trackingPanel;
	}

	private JPanel makeModePanel() {
		JPanel modePanel = ComponentFactory.makeBorder("Mode");
		GroupLayout layout = new GroupLayout(modePanel);
		modePanel.setLayout(layout);

		JRadioButton radioNormal = controller.normalAction.getRadioButton();
		JRadioButton radioDifference = controller.differenceAction
				.getRadioButton();

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(radioNormal).addComponent(radioDifference));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(radioNormal).addComponent(radioDifference));

		return modePanel;
	}

	private JPanel makeThresholdModePanel() {
		JPanel modePanel = ComponentFactory.makeBorder("Threshold");
		GroupLayout layout = new GroupLayout(modePanel);
		modePanel.setLayout(layout);

		JRadioButton radioLight = controller.lightAction.getRadioButton();
		JRadioButton radioDark = controller.darkAction.getRadioButton();

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(radioLight).addComponent(radioDark));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(radioLight).addComponent(radioDark));

		return modePanel;
	}

	public JToggleButton getAddTrackerButton() {
		return controller.addTrackerAction.getToggleButton();
	}

}
