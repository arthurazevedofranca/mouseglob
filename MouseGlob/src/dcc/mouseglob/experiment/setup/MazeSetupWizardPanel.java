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
package dcc.mouseglob.experiment.setup;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.maze.BoundariesController;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.MazeUI;
import dcc.mouseglob.maze.ZonesController;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.maze.io.MazeIOUI;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.movie.MovieUI;
import dcc.mouseglob.shape.CircleDrawer;
import dcc.mouseglob.shape.PolygonDrawer;
import dcc.tree.DefaultTreeable;
import dcc.tree.Tree;
import dcc.tree.TreeManager;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;
import dcc.ui.ComponentFactory;

public class MazeSetupWizardPanel extends DefaultMovieAppletWizardPanel {

	static final Object ID = "maze_setup";

	private Tree tree;
	private MazeIOUI mazeIOUI;
	private MazeUI mazeUI;
	private Treeable maze;
	private JPanel panel;

	@Inject
	public MazeSetupWizardPanel(MovieManager movieManager, MovieUI movieUI,
			ZonesManager zonesManager, ZonesController zonesController,
			BoundariesManager boundariesManager,
			BoundariesController boundariesController, MazeIOUI mazeIOUI,
			MazeUI mazeUI, Tree tree) {
		super(ID, CalibrationWizardPanel.ID, TrackingSetupWizardPanel.ID,
				movieManager, movieUI);

		MouseGlobApplet applet = getApplet();

		CircleDrawer circleDrawer = CircleDrawer.getInstance();
		PolygonDrawer polygonDrawer = PolygonDrawer.getInstance();

		circleDrawer.addCursorListener(applet);
		polygonDrawer.addCursorListener(applet);

		maze = new DefaultTreeable("Maze", null);
		TreeNode mazeNode = maze.getNode();
		mazeNode.add(zonesManager);
		mazeNode.add(boundariesManager);
		this.tree = tree;

		TreeManager treeManager = tree.getModel();
		treeManager.addTreeSelectionListener(circleDrawer);
		treeManager.addTreeSelectionListener(polygonDrawer);
		treeManager.addTreeSelectionListener(zonesController);

		applet.addPaintable(zonesManager.getRenderer());
		applet.addPaintable(boundariesManager.getRenderer());
		applet.addPaintable(circleDrawer);
		applet.addPaintable(polygonDrawer);

		applet.addMouseListener(circleDrawer);
		applet.addMouseListener(polygonDrawer);

		this.mazeIOUI = mazeIOUI;
		this.mazeUI = mazeUI;
	}

	@Override
	public JPanel getComponent() {
		panel = super.getComponent();
		JPanel toolBarPanel = ComponentFactory.makeToolBarPanel(
				mazeIOUI.makeToolBar(), mazeUI.makeToolBar());
		panel.add(toolBarPanel, BorderLayout.NORTH);
		panel.add(tree.getView().makePanel(), BorderLayout.WEST);
		return panel;
	}

	@Override
	public void aboutToDisplayPanel() {
		tree.getModel().setRoot(maze);
		panel.add(tree.getView().makePanel(), BorderLayout.WEST);
		panel.invalidate();
	}

}
