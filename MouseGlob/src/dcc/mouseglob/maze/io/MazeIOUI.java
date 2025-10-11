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
package dcc.mouseglob.maze.io;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.module.AbstractView;

public class MazeIOUI extends AbstractView<MazeIO> {

	@Inject
	private MazeIOController controller;

	@Override
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(controller.openMazeAction);
		toolBar.add(controller.saveMazeAction);
		toolBar.add(controller.saveMazeAsAction);
		toolBar.add(controller.exportZonesAction);
		return toolBar;
	}

	@Override
	public JMenu makeMenu() {
		JMenu menu = new JMenu("Maze");
		menu.getPopupMenu().setLightWeightPopupEnabled(false);
		menu.setMnemonic(KeyEvent.VK_Z);

		menu.add(controller.openMazeAction);
		menu.add(controller.saveMazeAction);
		menu.add(controller.saveMazeAsAction);
		menu.add(controller.exportZonesAction);

		return menu;
	}

	void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
}
