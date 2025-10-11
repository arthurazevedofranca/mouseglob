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
package dcc.mouseglob.movie;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import dcc.inject.Inject;
import dcc.module.AbstractView;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;

public class MovieUI extends AbstractView<MovieModule> {

	@Inject
	private MovieManager manager;
	@Inject
	private MovieController controller;

	@Override
	public JPanel makePanel() {
		JPanel moviePanel = new JPanel();
		BoxLayout layout = new BoxLayout(moviePanel, BoxLayout.LINE_AXIS);
		moviePanel.setLayout(layout);

		moviePanel.add(controller.openMovieAction.getIconButton());
		moviePanel.add(controller.rewindAction.getIconButton());
		moviePanel.add(controller.playAction.getIconButton());
		moviePanel.add(controller.fastForwardAction.getIconButton());
		moviePanel.add(controller.pauseAction.getIconButton());
		moviePanel.add(Box.createHorizontalStrut(10));

		JLabel posisionLabel = controller.positionAction.getLabel();
		posisionLabel.setMaximumSize(new Dimension(100, 20));
		moviePanel.add(posisionLabel);
		moviePanel.add(Box.createHorizontalStrut(10));

		JSlider sliderPlayPos = controller.positionAction.getSimpleSlider();

		moviePanel.add(sliderPlayPos);

		return moviePanel;
	}

	public JPanel makeOpenMoviePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel.add(new JLabel("Movie file:"));
		panel.add(Box.createHorizontalStrut(10));

		final JTextField nameField = new JTextField(40);
		nameField.setEditable(false);
		panel.add(nameField);

		manager.addMovieListener(new MovieListener() {
			@Override
			public void onMovieEvent(MovieEvent event) {
				if (event.getType() == MovieEventType.OPEN)
					nameField.setText(event.getFileName());
			}
		});

		JButton button = new JButton(controller.openMovieAction);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(button);

		return panel;
	}

	public JPanel makePlaybackPanel() {
		JPanel moviePanel = new JPanel();
		BoxLayout layout = new BoxLayout(moviePanel, BoxLayout.LINE_AXIS);
		moviePanel.setLayout(layout);

		moviePanel.add(controller.rewindAction.getIconButton());
		moviePanel.add(controller.playAction.getIconButton());
		moviePanel.add(controller.fastForwardAction.getIconButton());
		moviePanel.add(controller.pauseAction.getIconButton());
		moviePanel.add(Box.createHorizontalStrut(10));

		JLabel posisionLabel = controller.positionAction.getLabel();
		posisionLabel.setMaximumSize(new Dimension(100, 20));
		moviePanel.add(posisionLabel);
		moviePanel.add(Box.createHorizontalStrut(10));

		JSlider sliderPlayPos = controller.positionAction.getSimpleSlider();

		moviePanel.add(sliderPlayPos);

		return moviePanel;
	}

	@Override
	public JMenu makeMenu() {
		JMenu menu = new JMenu("Movie");
		menu.getPopupMenu().setLightWeightPopupEnabled(false);
		menu.setMnemonic(KeyEvent.VK_M);

		menu.add(controller.openMovieAction);
		menu.addSeparator();

		menu.add(controller.rewindAction);
		menu.add(controller.playAction);
		menu.add(controller.fastForwardAction);
		menu.add(controller.pauseAction);

		return menu;
	}
}
