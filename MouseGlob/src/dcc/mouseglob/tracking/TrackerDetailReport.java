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

import java.awt.Dimension;

import processing.core.PApplet;
import dcc.graphics.Color;
import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.Vector;
import dcc.graphics.plot.twod.ScalarPlot2D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.analyses.MouseModel;
import dcc.mouseglob.report.AppletReport;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;

@ReportInfo("Tracker Detail")
@ReportIcon("/resource/tracker16.png")
public class TrackerDetailReport extends AppletReport {

	private final Tracker tracker;
	private final MouseModel mouseModel;

	@Inject
	public TrackerDetailReport(Tracker tracker, MouseModel mouseModel) {
		this.tracker = tracker;
		this.mouseModel = mouseModel;
		setApplet(new TrackerDetailApplet());
	}

	@SuppressWarnings("serial")
	private class TrackerDetailApplet extends PApplet {

		static final int SIZE = 300;
		private final ScalarPlot2D detailPlot;

		private TrackerDetailApplet() {
			super.setSize(SIZE, SIZE);
			setPreferredSize(new Dimension(SIZE, SIZE));
			detailPlot = new ScalarPlot2D(0, 0, SIZE, SIZE);
		}

		@Override
		public void setup() {
			size(SIZE, SIZE);
			background(0);
		}

		@Override
		public void draw() {
			if (!tracker.hasData())
				return;

			background(0);
			pushMatrix();
			// translate(g.width / 2, g.height / 2);
			// rotate((float) -mouseModel.getAngle());
			// translate(-g.width / 2, -g.height / 2);

			int radius = tracker.getSize();
			int size = 2 * radius + 1;
			float scale = (float) SIZE / size;

			ScalarMap map = tracker.getMap();
			int mapWidth = map.getWidth();
			int mapHeight = map.getHeight();

			scale(scale);
			translate(mapWidth / 2, mapHeight / 2);

			detailPlot.setValues(map.getValues());
			detailPlot.setPosition(-mapWidth / 2, -mapHeight / 2);
			detailPlot.setSize(mapWidth, mapHeight);
			detailPlot.paint(g);
			translate(-mapWidth / 2, -mapHeight / 2);

			Vector.Float hip = mouseModel.getHip().toFloat();
			Vector.Float torso = mouseModel.getTorso().toFloat();
			Vector.Float head = mouseModel.getHead().toFloat();

			noFill();
			stroke(Color.GREEN);
			ellipse(hip.x, hip.y, 4, 4);
			stroke(Color.BLUE);
			ellipse(torso.x, torso.y, 4, 4);
			stroke(Color.MAGENTA);
			ellipse(head.x, head.y, 3, 3);

			popMatrix();
		}

	}

}
