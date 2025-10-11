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
package dcc.mouseglob.maze;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.module.View;
import dcc.mouseglob.maze.io.MazeIOUI;

public class MazeUI implements View {
	private static MazeUI instance;

	static MazeUI getInstance() {
		if (instance == null)
			instance = new MazeUI();
		return instance;
	}

	@Inject
	private ZonesController zones;
	@Inject
	private BoundariesController boundaries;
	@Inject
	private MazeIOUI mazeIOUI;

	private MazeUI() {
	}

	@Override
	public JComponent makePanel() {
		return null;
	}

	@Override
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar("Maze");

		toolBar.add(zones.drawPolygonAction.getIconButton());
		toolBar.add(zones.drawCircleAction.getIconButton());
		toolBar.add(zones.clearZonesAction);

		toolBar.addSeparator();

		toolBar.add(boundaries.drawPositivePolygonAction.getIconButton());
		toolBar.add(boundaries.drawPositiveCircleAction.getIconButton());
		toolBar.add(boundaries.drawNegativePolygonAction.getIconButton());
		toolBar.add(boundaries.drawNegativeCircleAction.getIconButton());
		toolBar.add(boundaries.clearBoundariesAction);
		toolBar.add(boundaries.refreshMaskAction);

		return toolBar;
	}

	@Override
	public JMenu makeMenu() {
		JMenu menu = mazeIOUI.makeMenu();
		menu.addSeparator();

		menu.add(zones.drawPolygonAction);
		menu.add(zones.drawCircleAction);
		menu.add(zones.clearZonesAction);

		menu.addSeparator();

		menu.add(boundaries.drawPositivePolygonAction);
		menu.add(boundaries.drawPositiveCircleAction);
		menu.add(boundaries.drawNegativePolygonAction);
		menu.add(boundaries.drawNegativeCircleAction);
		menu.add(boundaries.clearBoundariesAction);
		menu.add(boundaries.refreshMaskAction);

		return menu;
	}
}
