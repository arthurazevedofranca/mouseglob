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
package dcc.mouseglob.report;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import dcc.graphics.Box;
import dcc.graphics.math.Vector;
import dcc.inject.Context;
import dcc.inject.Indexer;
import dcc.mouseglob.analysis.AnalysesController;
import dcc.mouseglob.analysis.AnalysesManager;
import dcc.mouseglob.analysis.AnalysesModule;
import dcc.mouseglob.analysis.AnalysesView;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Dataset;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.AngleAnalysis;
import dcc.mouseglob.analysis.analyses.PositionAnalysis;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.tracking.Tracker;

public class ReportsTest {

	public static void main(String[] args) {
		Context context = new Context();
		context.inject(Indexer.load(AnalysesModule.class,
				AnalysesController.class, AnalysesManager.class,
				AnalysesView.class));

		AnalysesModule analysisModule = context
				.getInstance(AnalysesModule.class);

		System.out.println("*** Analyses");
		AnalysesManager analysisManager = analysisModule.getModel();
		analysisManager.select(AngleAnalysis.class);

		JOptionPane.showMessageDialog(null, analysisModule.getView()
				.makePanel(), "Analyses", JOptionPane.QUESTION_MESSAGE);

		for (Class<?> clazz : analysisManager.getAnalyses())
			System.out.println(clazz.getSimpleName());

		System.out.println("*** Reports");
		ReportsManager reportsManager = context
				.getInstance(ReportsManager.class);
		for (ReportDescriptor descriptor : reportsManager.getDescriptors())
			System.out.println(descriptor.getName());

		Tracker tracker = new Tracker(0, 0, 1, null);
		Dataset dataset = analysisManager.getNewDataset(tracker);
		System.err.println(dataset.getContext());
		simulate(dataset, reportsManager);

		WindowListener windowListener = new WindowAdapter() {
			private int count = 0;

			@Override
			public void windowOpened(WindowEvent e) {
				count++;
			}

			@Override
			public void windowClosed(WindowEvent e) {
				count--;
				if (count == 0) {
					System.exit(0);
				}
			}

		};

		ReportsView reportsView = context.getInstance(ReportsView.class);
		JOptionPane.showMessageDialog(null, reportsView.makePanel(), "Reports",
				JOptionPane.QUESTION_MESSAGE);
		for (ReportDescriptor descriptor : reportsManager.getSelectedReports()) {
			Report report = descriptor.getHandle(tracker).getReport();
			JDialog dialog = report.getDialog();
			dialog.addWindowListener(windowListener);
			dialog.setVisible(true);
		}
	}

	private static void simulate(Dataset dataset, ReportsManager reportsManager) {
		double scale = 30.0 / 400.0; // cm/px
		int width = 640, height = 480;
		Box bounds = Box.fromSize(width, height);
		double fps = 24;

		double a1 = 0.4, b1 = 0.55;
		double a0 = 50 * (1 - (a1 + b1));
		Garch11 g = new Garch11(a0, a1, b1);

		Time time = dataset.require(Time.class);
		PositionAnalysis position = dataset.require(PositionAnalysis.class);
		AngleAnalysis angle = dataset.require(AngleAnalysis.class);

		List<Analysis> otherAnalyses = new ArrayList<>();
		for (Analysis analysis : dataset) {
			if (!analysis.equals(time) && !analysis.equals(position)
					&& !analysis.equals(angle))
				otherAnalyses.add(analysis);
		}

		Calibration calibration = dataset.getContext().getInstance(
				Calibration.class);
		calibration.setScale(scale);

		position.setBounds(width, height);
		Vector p = new Vector(scale * width / 2, scale * height / 2);
		Random random = new Random();
		double a = 0;
		for (int i = 0; i < 600 * fps; i++) {
			time.add(i / fps);

			double da = 180 * random.nextGaussian();
			a += da / fps;
			angle.add(a - 360 * Math.round(a / 360));

			double d = g.next();
			double dx = d * Math.cos(a);
			double dy = d * Math.sin(a);
			double x = p.x + dx / fps;
			double y = p.y + dy / fps;

			p = bounds.clamp(new Vector(x, y));
			position.add(p);

			for (Analysis analysis : otherAnalyses)
				analysis.update();
		}
		for (Analysis analysis : dataset)
			System.err.println(analysis);
	}

	private static class Garch11 {

		private static final Random RANDOM = new Random();
		private final double a0, a1, b1;
		private double sigma2, epsilon;

		/**
		 * y<sub>t</sub> = s<sub>t</sub> . e<sub>t</sub>, e<sub>t</sub> ~
		 * NID(0,1) <br>
		 * s<sub>t</sub><sup>2</sup> = a<sub>0</sub> + a<sub>1</sub> .
		 * y<sub>t-1</sub><sup>2</sup> + b<sub>1</sub> .
		 * s<sub>t-1</sub><sup>2</sup>
		 */
		public Garch11(double a0, double a1, double b1) {
			this.a0 = a0;
			this.a1 = a1;
			this.b1 = b1;
			sigma2 = a0 / (1 - a1 - b1);
		}

		public double next() {
			sigma2 = a0 + a1 * epsilon * epsilon + b1 * sigma2;
			epsilon = Math.sqrt(sigma2) * RANDOM.nextGaussian();
			return epsilon;
		}

	}

}
