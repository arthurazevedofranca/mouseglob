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
package dcc.mouseglob.report.reports;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;

import dcc.graphics.math.HeatMap;
import dcc.graphics.math.Vector;
import dcc.graphics.plot.ColorBar;
import dcc.graphics.plot.ColorMap;
import dcc.graphics.plot.Plot.Probe;
import dcc.graphics.plot.oned.Axis;
import dcc.graphics.plot.oned.Log1pAxis;
import dcc.graphics.plot.twod.ScalarPlot2D;
import dcc.graphics.series.Series;
import dcc.graphics.series.Series2D;
import dcc.graphics.series.SeriesListener;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.PositionAnalysis;
import dcc.mouseglob.applet.CursorListener.Cursor;
import dcc.mouseglob.applet.DefaultApplet;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseListener;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.report.AppletReport;
import dcc.mouseglob.report.DisplayAction;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.ui.Action;
import dcc.ui.SliderAction;
import dcc.ui.StatusBar;
import dcc.ui.ToggleAction;

@ReportInfo("Heat Map")
@ReportIcon("/resource/heatMapBBReport16.png")
public class HeatMapReport extends AppletReport implements MouseListener,
		SeriesListener {

	private static final int DEFAULT_SIZE = 500;
	private static final int INITIAL_LEVEL = 0;
	private static final int MAX_LEVEL = 10;

	private static final double[] SIGMAS = new double[MAX_LEVEL + 1];
	static {
		for (int level = 0; level <= MAX_LEVEL; level++)
			SIGMAS[level] = Math.pow(2, 0.5 * (level - 2));
	}

	private final Time time;
	private final PositionAnalysis position;
	private final Calibration calibration;

	private final HeatMap[] heatMaps;
	private final ColorBar colorBar;
	private final ScalarPlot2D plot;
	private final DefaultApplet applet;
	private final boolean[] dirty;

	private final Axis presenceAxis = Axis.autoscalingMax(0, 1)
			.setLabel("Presence").setFormat("%.3g");
	private final Axis logPresenceAxis = Log1pAxis.autoscalingMax(0, 1)
			.setLabel("Presence").setFormat("%.3g");;

	private final JLabel statusLabel = new JLabel(" ");
	private final JLabel resolutionLabel = new JLabel(String.format(
			"Resolution level: %.1f cm", SIGMAS[INITIAL_LEVEL]));

	private boolean isMouseInside = false;

	@SuppressWarnings("serial")
	@Inject
	public HeatMapReport(PositionAnalysis position, Time time,
			Calibration calibration, BoundariesManager boundariesManager,
			ZonesManager zonesManager) {
		double boundsWidth = position.getWidth();
		double boundsHeight = position.getHeight();
		double ratio = boundsWidth / boundsHeight;
		int width = DEFAULT_SIZE;
		int height = DEFAULT_SIZE;
		if (boundsWidth > boundsHeight) {
			height = (int) (DEFAULT_SIZE / ratio);
		} else {
			width = (int) (DEFAULT_SIZE * ratio);
		}

		plot = new ScalarPlot2D(0, 0, width, height, presenceAxis,
				ColorMap.BLACK_BODY);

		applet = new DefaultApplet() {
			@Override
			public void draw() {
				background(255);
				super.draw();
			}
		};
		setApplet(applet);
		applet.setAppletSize(width + 100, height);
		applet.addMouseListener(this);

		this.position = position;
		this.time = time;
		this.calibration = calibration;

		position.addSeriesListener(this);

		heatMaps = new HeatMap[MAX_LEVEL + 1];
		colorBar = plot.getColorBar();
		applet.addPaintable(plot);
		applet.addPaintable(colorBar.getRenderer(width, 0, 10, height));
		applet.noLoop();

		dirty = new boolean[MAX_LEVEL + 1];
		for (int i = 0; i < dirty.length; i++)
			dirty[i] = true;

		DisplayAction displayBoundariesAction = new DisplayAction(
				"Display Boundaries", plot, boundariesManager.getRenderer());
		DisplayAction displayZonesAction = new DisplayAction("Display Zones",
				plot, zonesManager.getBasicRenderer());

		getPopupMenu().add(displayBoundariesAction.getCheckBoxMenuItem());
		getPopupMenu().add(displayZonesAction.getCheckBoxMenuItem());

		update();
	}

	private void update() {
		update(resolutionAction.getValue(), logAction.isSelected());
	}

	private void update(int level, boolean log) {
		if (dirty[level]) {
			if (heatMaps[level] != null)
				heatMaps[level].release();
			heatMaps[level] = calculate(SIGMAS[level] / calibration.getScale());
			dirty[level] = false;
		}
		plot.setAxis(log ? logPresenceAxis : presenceAxis);
		colorBar.setAxis(log ? logPresenceAxis : presenceAxis);
		plot.setValues(heatMaps[level].getValues());
		applet.redraw();
	}

	private HeatMap calculate(double sigma) {
		if (position.isEmpty())
			return null;
		HeatMap heatMap = new HeatMap((int) position.getWidth(),
				(int) position.getHeight(), sigma);
		int size = Math.min(position.size(), time.size());
		Series2D pxPosition = position.getPxTrajectory();
		double scale = calibration.getScale();
		for (int i = 1; i < size; i++) {
			Vector p1 = pxPosition.get(i - 1);
			Vector p2 = pxPosition.get(i);
			double dt = time.diff(i) * 1000;
			double n = p1.distance(p2) + 1;
			for (int j = 0; j < n - 1; j++) {
				double k = j / n;
				Vector p = p1.lerp(p2, k);
				heatMap.increment((int) p.x, (int) p.y, dt / (n * scale));
			}
		}
		return heatMap;
	}

	private SliderAction resolutionAction = new SliderAction(
			"Resolution level", 0, MAX_LEVEL, INITIAL_LEVEL) {
		@Override
		public void valueChanged(int value) {
			resolutionLabel.setText(String.format("Resolution level: %.1f cm",
					SIGMAS[value]));
			update();
		}
	};

	@SuppressWarnings("serial")
	private ToggleAction logAction = new ToggleAction("Log-scale") {
		@Override
		public void itemStateChanged(boolean state) {
			for (int i = 0; i < dirty.length; i++)
				dirty[i] = true;
			update();
		}
	};

	@SuppressWarnings("serial")
	private Action updateAction = new Action("Update", "/resource/update16.png") {
		@Override
		public void actionPerformed() {
			update();
		}
	};

	@Override
	public JDialog getDialog() {
		JDialog dialog = super.getDialog();
		StatusBar statusBar = new StatusBar();
		JSlider slider = resolutionAction
				.getSimpleSlider(new Dimension(100, 18));
		JCheckBox logCheckBox = logAction.getCheckBox();
		logCheckBox.setPreferredSize(new Dimension(85, 18));

		JButton updateButton = updateAction.getIconButton();
		Dimension dim = new Dimension(18, 18);
		updateButton.setMinimumSize(dim);
		updateButton.setPreferredSize(dim);
		updateButton.setMaximumSize(dim);

		statusBar.add(statusLabel);
		statusBar.add(resolutionLabel);
		statusBar.add(slider);
		statusBar.add(logCheckBox);
		statusBar.add(updateButton);

		dialog.add(statusBar, BorderLayout.SOUTH);
		dialog.setResizable(false);
		dialog.pack();
		return dialog;
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		Probe probe = plot.probe(event.getMouseX(), event.getMouseY());
		if (probe != null) {
			double scale = calibration.getScale();
			double x = probe.getXValue() * scale;
			double y = probe.getYValue() * scale;
			statusLabel.setText(String.format("%.2f, %.2f", x, y));
			if (!isMouseInside)
				applet.setCursor(Cursor.CROSS);
			isMouseInside = true;
		} else {
			statusLabel.setText(" ");
			if (isMouseInside)
				applet.setCursor(Cursor.ARROW);
			isMouseInside = false;
		}
		return false;
	}

	@Override
	public void onSeriesChanged(Series series) {
		for (int i = 0; i < dirty.length; i++)
			dirty[i] = true;
	}

	@Override
	public void onClose() {
		position.removeSeriesListener(this);
	}

}
