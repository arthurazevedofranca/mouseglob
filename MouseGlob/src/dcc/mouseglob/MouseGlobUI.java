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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;

import dcc.inject.Inject;
import dcc.module.View;
import dcc.mouseglob.analysis.AnalysesView;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.calibration.CalibrationView;
import dcc.mouseglob.experiment.ExperimentIOView;
import dcc.mouseglob.inspector.InspectorUI;
import dcc.mouseglob.keyevent.KeyEventController;
import dcc.mouseglob.maze.MazeUI;
import dcc.mouseglob.movie.MovieUI;
import dcc.mouseglob.report.Report;
import dcc.mouseglob.tracking.TrackingController;
import dcc.mouseglob.tracking.TrackingUI;
import dcc.mouseglob.trajectory.TrajectoriesIOView;
import dcc.mouseglob.ui.FileChooser;
import dcc.mouseglob.ui.StatusUI;
import dcc.tree.Tree;
import dcc.tree.TreeUI;
import dcc.ui.ComponentFactory;
import dcc.ui.RadioAction;
import dcc.ui.StatusBar;
import dcc.util.ImageLoader;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.UIScale;
import dcc.inject.Context;
import dcc.mouseglob.experiment.setup.ChooseVideoWizardPanel;
import dcc.mouseglob.experiment.setup.CalibrationWizardPanel;
import dcc.mouseglob.experiment.setup.ExperimentNameWizardPanel;
import dcc.mouseglob.experiment.setup.MazeSetupWizardPanel;
import dcc.mouseglob.experiment.setup.TrackingSetupWizardPanel;
import dcc.mouseglob.experiment.setup.wizard.Wizard;

/**
 * @author Daniel Coelho de Castro
 */
@SuppressWarnings("serial")
public class MouseGlobUI extends JFrame implements View {
	private JMenu viewMenu;
	private MouseGlob module;

	@Inject
	MouseGlobUI(MouseGlob module) {
		super("MouseGlob " + MouseGlob.VERSION);

		this.module = module;

		// Initialize modern Look & Feel (FlatLaf) with persisted theme and scaling
		try {
			String theme = PropertiesManager.getInstance().get("ui.theme", "light");
			String scaleS = PropertiesManager.getInstance().get("ui.scale", "1.0");
			float scale;
			try { scale = Float.parseFloat(scaleS); } catch (Exception ex) { scale = 1.0f; }
			System.setProperty("flatlaf.uiScale", String.valueOf(Math.max(1.0f, scale)));
			if ("dark".equalsIgnoreCase(theme)) FlatDarkLaf.setup();
			else FlatLightLaf.setup();
			UIManager.put("TitlePane.unifiedBackground", Boolean.TRUE);
		} catch (Exception ignore) {
			// fallback to default
		}

		FileChooser.setParent(this);
		Report.setParent(this);
	}

 @Inject
	private void initMenuBar(ExperimentIOView experimentIOView, MazeUI mazeUI,
			TrackingController trackingController, MovieUI movieUI,
			KeyEventController keyEventController,
			dcc.mouseglob.keyevent.KeyEventView keyEventView) {

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = experimentIOView.makeMenu();
		fileMenu.addSeparator();
		fileMenu.add(module.getController().exitAction);
		menuBar.add(fileMenu);
		JMenu mazeMenu = mazeUI.makeMenu();
		mazeMenu.addSeparator();
		menuBar.add(mazeMenu);
		menuBar.add(movieUI.makeMenu());
		menuBar.add(makeEventsMenu(keyEventController, keyEventView));
		menuBar.add(makeViewMenu());
		menuBar.add(makeSetupMenu());

		setJMenuBar(menuBar);
	}

	@Inject
	private void initPanels(MovieUI movieUI, InspectorUI inspectorUI,
			ExperimentIOView experimentIOView, MazeUI mazeUI,
			TrajectoriesIOView trajectoryIOView,
			CalibrationView calibrationView, TrackingUI trackingUI,
			AnalysesView analysesView, MouseGlobApplet applet) {

		JPanel moviePanel = movieUI.makePanel();
		Tree tree = module.tree;
		TreeUI treeUI = tree.getView();
		JPanel treePanel = treeUI.makePanel();
		JScrollPane inspectorPanel = inspectorUI.makePanel();

		final JSplitPane elementsPanel = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, treePanel, inspectorPanel);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				elementsPanel.setResizeWeight(0.5);
			}
		});

		StatusBar statusBar = StatusUI.makeStatusBar();

		add(makeToolBarPanel(experimentIOView, mazeUI, trajectoryIOView,
				calibrationView, trackingUI, analysesView), BorderLayout.NORTH);

		JPanel centralPanel = new JPanel(new BorderLayout());
		centralPanel.add(moviePanel, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				elementsPanel, centralPanel);
		add(split, BorderLayout.CENTER);

		add(statusBar, BorderLayout.SOUTH);

		JPanel appletPanel = new JPanel();
		appletPanel.setLayout(new BoxLayout(appletPanel, BoxLayout.Y_AXIS));
		appletPanel.add(Box.createVerticalGlue());
		appletPanel.add(applet);
		appletPanel.add(Box.createVerticalGlue());
		centralPanel.add(appletPanel, BorderLayout.CENTER);
	}

	private JPanel makeToolBarPanel(ExperimentIOView experimentIOView,
			MazeUI mazeUI, TrajectoriesIOView trajectoryIOView,
			CalibrationView calibrationView, TrackingUI trackingUI,
			AnalysesView analysesView) {

		JToolBar experimentToolBar = experimentIOView.makeToolBar();
		experimentToolBar.addSeparator();
		experimentToolBar.add(analysesView.getManageAnalysesButton());

		JToolBar mazeToolBar = mazeUI.makeToolBar();
		mazeToolBar.addSeparator();
		mazeToolBar.add(trackingUI.getAddTrackerButton());

		JToolBar coordinatesIOToolBar = trajectoryIOView.makeToolBar();

		JToolBar calibrationToolBar = calibrationView.makeToolBar();

		return ComponentFactory.makeToolBarPanel(experimentToolBar,
				mazeToolBar, coordinatesIOToolBar, calibrationToolBar);
	}

	void createGUI() {
		setMinimumSize(new Dimension(640, 480));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				module.onClose();
			}
		});
		setIconImage(ImageLoader.load("/resource/tracker16.png"));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		repaint();
	}

	@Override
	public JPanel makePanel() {
		return null;
	}

	@Override
	public JToolBar makeToolBar() {
		return null;
	}

	@Override
	public JMenu makeMenu() {
		return null;
	}

 private JMenu makeEventsMenu(KeyEventController keyEventController, dcc.mouseglob.keyevent.KeyEventView keyEventView) {
		JMenu menu = new JMenu("Events");
		menu.getPopupMenu().setLightWeightPopupEnabled(false);
		menu.setMnemonic(KeyEvent.VK_E);

		menu.add(keyEventController.newKeyEventClassAction);
		JMenuItem shortcuts = new JMenuItem("Shortcuts...");
		shortcuts.addActionListener(e -> keyEventView.makeDialog().setVisible(true));
		menu.add(shortcuts);

		return menu;
	}

 private JMenu makeViewMenu() {
		viewMenu = new JMenu("View");
		viewMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		viewMenu.setMnemonic(KeyEvent.VK_V);

		MouseGlobController controller = module.getController();
		for (RadioAction action : controller.imageTypeGroup.getRadioActions())
			viewMenu.add(action.getRadioButtonMenuItem());
		viewMenu.addSeparator();

		// Appearance submenu (theme + UI scale)
		JMenu appearance = new JMenu("Appearance");
		JMenu themeMenu = new JMenu("Theme");
		ButtonGroup themeGroup = new ButtonGroup();
		String theme = PropertiesManager.getInstance().get("ui.theme", "light");
		JRadioButtonMenuItem light = new JRadioButtonMenuItem("Light");
		JRadioButtonMenuItem dark = new JRadioButtonMenuItem("Dark");
		light.setSelected(!"dark".equalsIgnoreCase(theme));
		dark.setSelected("dark".equalsIgnoreCase(theme));
		light.addActionListener(e -> switchTheme(false));
		dark.addActionListener(e -> switchTheme(true));
		themeGroup.add(light);
		themeGroup.add(dark);
		themeMenu.add(light);
		themeMenu.add(dark);

		JMenu scaleMenu = new JMenu("UI Scale");
		JMenuItem s100 = new JMenuItem("100%"); s100.addActionListener(e -> changeScale(1.0f));
		JMenuItem s125 = new JMenuItem("125%"); s125.addActionListener(e -> changeScale(1.25f));
		JMenuItem s150 = new JMenuItem("150%"); s150.addActionListener(e -> changeScale(1.5f));
		scaleMenu.add(s100); scaleMenu.add(s125); scaleMenu.add(s150);

		appearance.add(themeMenu);
		appearance.add(scaleMenu);
		viewMenu.add(appearance);

		return viewMenu;
	}

	private void switchTheme(boolean dark) {
		PropertiesManager.getInstance().set("ui.theme", dark ? "dark" : "light");
		try {
			if (dark) FlatDarkLaf.setup(); else FlatLightLaf.setup();
			SwingUtilities.updateComponentTreeUI(this);
			this.repaint();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Failed to apply theme: " + ex.getMessage(), "Theme", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void changeScale(float scale) {
		PropertiesManager.getInstance().set("ui.scale", String.valueOf(scale));
		System.setProperty("flatlaf.uiScale", String.valueOf(scale));
		JOptionPane.showMessageDialog(this, "UI scale will be applied after restarting the application.", "UI Scale", JOptionPane.INFORMATION_MESSAGE);
	}

	private JMenu makeSetupMenu() {
		JMenu menu = new JMenu("Setup");
		JMenuItem run = new JMenuItem("Run Setup Wizard...");
		run.addActionListener(e -> runSetupWizard());
		menu.add(run);
		return menu;
	}

	private void runSetupWizard() {
		Context ctx = Context.getGlobal();
		Wizard wizard = new Wizard(this, "Experiment Setup");
		ExperimentNameWizardPanel p1 = new ExperimentNameWizardPanel();
		ChooseVideoWizardPanel p2 = ctx.getInstance(ChooseVideoWizardPanel.class);
		CalibrationWizardPanel p3 = ctx.getInstance(CalibrationWizardPanel.class);
		MazeSetupWizardPanel p4 = ctx.getInstance(MazeSetupWizardPanel.class);
		TrackingSetupWizardPanel p5 = ctx.getInstance(TrackingSetupWizardPanel.class);
		wizard.registerWizardPanels(p1, p2, p3, p4, p5);
  wizard.setCurrentPanel(p1.getId());
		wizard.show();
	}
}
