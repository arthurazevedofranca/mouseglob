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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import dcc.inject.Inject;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.camera.Camera;
import dcc.mouseglob.experiment.setup.wizard.DefaultWizardPanel;
import dcc.mouseglob.inspector.InspectorController;
import dcc.mouseglob.inspector.InspectorUI;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.movie.AppletResizer;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.movie.MovieUI;
import dcc.mouseglob.tracking.TrackingController;
import dcc.mouseglob.tracking.TrackingManager;
import dcc.mouseglob.tracking.TrackingUI;
import dcc.mouseglob.tracking.TrackingManager.ImageType;
import dcc.tree.Tree;
import dcc.tree.TreeManager;
import dcc.ui.EnumButtonGroup;
import dcc.ui.PopupMenu;

public class TrackingSetupWizardPanel extends DefaultWizardPanel {

	static final Object ID = "tracking_setup";

	private MouseGlobApplet applet;
	private Tree tree;
	private TrackingManager trackingManager;
	private TrackingUI trackingController;
	private InspectorUI inspectorUI;
	private MovieUI movieUI;
	private JPanel panel;

	@Inject
	public TrackingSetupWizardPanel(MovieManager movieManager, MovieUI movieUI,
			ZonesManager zonesManager, BoundariesManager boundariesManager,
			TrackingManager trackingManager,
			TrackingController trackingController, TrackingUI trackingUI,
			InspectorController inspectorController, InspectorUI inspectorUI,
			Tree tree) {
		super(ID, MazeSetupWizardPanel.ID, FINISH);

		this.tree = tree;

		TreeManager treeManager = tree.getModel();
		treeManager.setRoot(trackingManager);
		treeManager.addTreeSelectionListener(trackingManager);
		treeManager.addTreeSelectionListener(inspectorController);

		applet = new MouseGlobApplet();
		applet.addPaintable(zonesManager.getRenderer());
		applet.addPaintable(boundariesManager.getRenderer());
		applet.addPaintable(trackingManager);

		applet.addMouseListener(trackingController);

		this.trackingManager = trackingManager;
		movieManager.addMovieListener(new AppletResizer(applet));
		movieManager.addNewFrameListener(trackingManager);
		trackingManager.addNewFrameListener(applet);

		trackingController.addCursorListener(applet);

		EnumButtonGroup<ImageType> imageTypeGroup = new EnumButtonGroup<ImageType>() {
			@Override
			public void valueSelected(ImageType value) {
				TrackingSetupWizardPanel.this.trackingManager
						.setImageType(value);
			}
		};
		imageTypeGroup.add("Clean Image", ImageType.CLEAN);
		imageTypeGroup.add("Tracking Image", ImageType.GLOB);
		imageTypeGroup.select(0);

		PopupMenu popup = imageTypeGroup.makePopupMenu();

		applet.addMouseListener(popup);

		this.movieUI = movieUI;
		this.trackingController = trackingUI;
		this.inspectorUI = inspectorUI;
	}

	@Override
	public JPanel getComponent() {
		panel = new JPanel(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.add(trackingController.getAddTrackerButton());
		panel.add(toolBar, BorderLayout.NORTH);

		JPanel treePanel = tree.getView().makePanel();
		JScrollPane inspectorPanel = inspectorUI.makePanel();
		JSplitPane elementsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				treePanel, inspectorPanel);

		JPanel appletPanel = getAppletPanel(Camera.WIDTH, Camera.HEIGHT);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				elementsPanel, appletPanel);
		panel.add(splitPane, BorderLayout.CENTER);

		panel.add(movieUI.makePlaybackPanel(), BorderLayout.SOUTH);

		return panel;
	}

	protected JPanel getAppletPanel(int appletWidth, int appletHeight) {
		applet.init();
		applet.setAppletSize(appletWidth, appletHeight);
		applet.init();
		return DefaultMovieAppletWizardPanel.getAppletPanel(applet);
	}

	@Override
	public void aboutToDisplayPanel() {
		tree.getModel().setRoot(trackingManager);
		JPanel treePanel = tree.getView().makePanel();
		JScrollPane inspectorPanel = inspectorUI.makePanel();
		JSplitPane elementsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				treePanel, inspectorPanel);

		JPanel appletPanel = getAppletPanel(Camera.WIDTH, Camera.HEIGHT);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				elementsPanel, appletPanel);
		panel.add(splitPane, BorderLayout.CENTER);
		panel.add(tree.getView().makePanel(), BorderLayout.WEST);
		panel.invalidate();
		applet.init();
	}

	@Override
	public void aboutToHidePanel() {
		applet.destroy();
	}

}
