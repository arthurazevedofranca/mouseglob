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
package dcc.mouseglob;

import javax.swing.SwingUtilities;

import dcc.inject.Context;
import dcc.inject.Indexer;
import dcc.inject.Inject;
import dcc.module.AbstractModule;
import dcc.module.ModuleNotInitializedException;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.calibration.CalibrationController;
import dcc.mouseglob.camera.CameraManager;
import dcc.mouseglob.experiment.Experiment;
import dcc.mouseglob.experiment.ExperimentIOManager;
import dcc.mouseglob.inspector.InspectorController;
import dcc.mouseglob.keyevent.KeyEventManager;
import dcc.mouseglob.keyevent.KeyEventModule;
import dcc.mouseglob.maze.BoundariesController;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesController;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.maze.io.MazeIOController;
import dcc.mouseglob.movie.AppletResizer;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.report.ReportsController;
import dcc.mouseglob.shape.CircleDrawer;
import dcc.mouseglob.shape.PolygonDrawer;
import dcc.mouseglob.tracking.TrackingController;
import dcc.mouseglob.tracking.TrackingManager;
import dcc.mouseglob.trajectory.TrajectoriesIOController;
import dcc.mouseglob.trajectory.TrajectoriesIOManager;
import dcc.mouseglob.visit.VisitEventManager;
import dcc.tree.Tree;
import dcc.tree.TreeManager;

public class MouseGlob extends
		AbstractModule<MouseGlobApplet, MouseGlobUI, MouseGlobController> {

	public static final String VERSION = "2.0.1";
	public static final String HEADER = String.format("MouseGlob %s - Daniel Coelho de Castro (2009-2016)", VERSION);

	@Inject
	public static KeyEventModule keyEvent;
	@Inject
	public static VisitEventManager visitEventManager;
	Tree tree;
	private CameraManager cameraManager;
	private MovieManager movieManager;

	@Inject
	private MouseGlob(MouseGlobApplet applet, MouseGlobController controller) {
		setModel(applet);
		setController(controller);
	}

	@Inject
	private void initPaintables(MouseGlobApplet applet,
			TrajectoriesIOManager trajectoriesIOManager,
			BoundariesManager boundariesManager, ZonesManager zonesManager,
			Calibration calibrationModel, TrackingManager trackingManager) {

		applet.addPaintable(boundariesManager.getRenderer());
		applet.addPaintable(zonesManager.getRenderer());
		applet.addPaintable(calibrationModel);
		applet.addPaintable(CircleDrawer.getInstance());
		applet.addPaintable(PolygonDrawer.getInstance());
		applet.addPaintable(trackingManager);
		applet.addPaintable(trajectoriesIOManager);
	}

	@Inject
	private void initNewFrameListeners(CameraManager cameraManager,
			MovieManager movieManager, BoundariesManager boundariesManager,
			TrackingManager trackingManager, MouseGlobApplet applet,
			VisitEventManager visitEventManager,
			TrajectoriesIOManager trajectoriesIOManager,
			KeyEventManager keyEventManager) {

		cameraManager.addNewFrameListener(boundariesManager);
		cameraManager.addNewFrameListener(trackingManager);
		movieManager.addNewFrameListener(boundariesManager);
		movieManager.addNewFrameListener(trackingManager);
		movieManager.addNewFrameListener(keyEventManager);
		trackingManager.addNewFrameListener(applet);
		trackingManager.addNewFrameListener(visitEventManager);
		trackingManager.addNewFrameListener(trajectoriesIOManager);
	}

	@Inject
	private void initMovieListeners(MovieManager movieManager,
			CalibrationController calibrationController,
			MouseGlobApplet applet, BoundariesController boundariesController,
			ZonesController zonesController) {

		movieManager.addMovieListener(calibrationController);
		movieManager.addMovieListener(new AppletResizer(applet));
		movieManager.addMovieListener(zonesController);
		movieManager.addMovieListener(boundariesController);
		// movieManager.addMovieListener(camera.getController());
	}

	@Inject
	private void initMouseListeners(MouseGlobApplet applet,
			BoundariesController boundariesController,
			ZonesController zonesController,
			CalibrationController calibrationController,
			TrackingController trackingController) {

		applet.addMouseListener(calibrationController);
		applet.addMouseListener(CircleDrawer.getInstance());
		applet.addMouseListener(PolygonDrawer.getInstance());
		applet.addMouseListener(trackingController);
	}

	@Inject
	private void initTrackingListeners(TrackingManager trackingManager,
			VisitEventManager visitEventManager,
			TrajectoriesIOController trajectoriesIOController) {

		trackingManager.addTrackingListener(visitEventManager);
		trackingManager.addTrackingListener(trajectoriesIOController);
	}

	@Inject
	private void initTreeListeners(ExperimentIOManager experimentIOManager,
			InspectorController inspectorController,
			TrackingManager trackingManager, ZonesController zonesController,
			ReportsController reportsController) {

		Experiment experiment = experimentIOManager.getCurrentExperiment();
		tree = new Tree(experiment.getNode());
		experiment.updateStructure();

		CircleDrawer circleDrawer = CircleDrawer.getInstance();
		PolygonDrawer polygonDrawer = PolygonDrawer.getInstance();

		TreeManager treeManager = tree.getModel();

		treeManager.addTreeStructureListener(inspectorController);
		treeManager.addTreeStructureListener(circleDrawer);
		treeManager.addTreeStructureListener(polygonDrawer);

		treeManager.addTreeSelectionListener(circleDrawer);
		treeManager.addTreeSelectionListener(polygonDrawer);
		treeManager.addTreeSelectionListener(inspectorController);
		treeManager.addTreeSelectionListener(trackingManager);
		treeManager.addTreeSelectionListener(zonesController);
		treeManager.addTreeSelectionListener(reportsController);
	}

	@Inject
	private void initZoneListeners(ZonesManager zonesManager,
			VisitEventManager visitEventManager) {

		zonesManager.addZoneListener(visitEventManager);
	}

	@Inject
	private void initExperimentListeners(
			ExperimentIOManager experimentIOManager,
			MazeIOController mazeIOController, ZonesController zonesController,
			BoundariesController boundariesController,
			CalibrationController calibrationController) {

		experimentIOManager.addExperimentListener(mazeIOController);
		experimentIOManager.addExperimentListener(zonesController);
		experimentIOManager.addExperimentListener(boundariesController);
		experimentIOManager.addExperimentListener(calibrationController);
	}

	@Inject
	private void initCursorListeners(MouseGlobApplet applet,
			CalibrationController calibrationController,
			TrackingController trackingController) {

		CircleDrawer.getInstance().addCursorListener(applet);
		PolygonDrawer.getInstance().addCursorListener(applet);
		calibrationController.addCursorListener(applet);
		trackingController.addCursorListener(applet);
	}

	@Inject
	private void initView(MouseGlobUI view) {
		setView(view);
	}

	@Inject
	private void setCloseables(CameraManager cameraManager,
			MovieManager movieManager) {
		this.cameraManager = cameraManager;
		this.movieManager = movieManager;
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MouseGlob.class);

	void onClose() {
		try {
			cameraManager.stop();
			movieManager.close();
		} catch (NullPointerException | ModuleNotInitializedException e) {
			log.warn("Error while closing resources: {}", e.toString());
		}

		PropertiesManager.getInstance().store();
		log.info("Properties stored. Bye!");
	}

	public static void main(String[] args) {
		PropertiesManager.getInstance().load();

		Context injection = Context.getGlobal();
		Indexer idx = Indexer.load("/dcc/mouseglob/classes.txt");
		// Validate DI graph before injecting
		try {
			dcc.inject.GraphValidator.ValidationResult vr = injection.validate(idx);
			if (!vr.ok) {
				log.error("Dependency graph validation failed. See report above.");
				dcc.mouseglob.ui.ErrorDialog.showError("Erro de Injeção de Dependências",
					"Falha na validação das dependências.\nCorrija ciclos ou bindings ausentes e reinicie.\n\nVeja logs em " +
				java.nio.file.Paths.get(System.getProperty("user.home"), ".mouseglob", "logs").toString() + ".");
				return;
			}
		} catch (Throwable t) {
			log.error("DI validation crashed: {}", t.toString());
		}
		injection.inject(idx);
		log.info("Initialized injection context: {}", injection);
		final MouseGlob mouseGlob = injection.getInstance(MouseGlob.class);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mouseGlob.getView().createGUI();
			}
		});
	}
}
