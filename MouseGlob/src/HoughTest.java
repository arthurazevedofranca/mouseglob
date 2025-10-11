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
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.FilterFactory;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Histogram;
import dcc.graphics.image.Hough;
import dcc.graphics.image.Hough.Line;
import dcc.graphics.image.Image;
import dcc.graphics.math.Vector;
import dcc.graphics.plot.twod.ScalarPlot2D;

@SuppressWarnings("serial")
public class HoughTest extends PApplet {
	private Capture capture;
	private Image video;
	private GrayscaleImage gvideo;
	private GrayscaleImage xedges, yedges, edges;
	private BinaryImage tedges;
	private Hough hough;
	private ScalarPlot2D houghPlot;

	private PImage pgvideo;
	private PImage pedges;
	private PImage ptedges;

	private List<Vector> points;
	private List<Line> lines;

	private final int CAM_WIDTH = 160;
	private final int CAM_HEIGHT = 120;
	private final float SCALE = 3;
	private final int WIDTH = (int) (SCALE * CAM_WIDTH);
	private final int HEIGHT = (int) (SCALE * CAM_HEIGHT);

	private int threshold = 96;
	private int detection = 2;

	@Override
	public void setup() {
		size(3 * WIDTH, 2 * HEIGHT);

		capture = new Capture(this, CAM_WIDTH, CAM_HEIGHT);
		capture.start();

		video = new Image(CAM_WIDTH, CAM_HEIGHT);
		gvideo = new GrayscaleImage(CAM_WIDTH, CAM_HEIGHT);
		edges = new GrayscaleImage(CAM_WIDTH, CAM_HEIGHT);
		tedges = new BinaryImage(CAM_WIDTH, CAM_HEIGHT);
		hough = new Hough(360, 200);
		houghPlot = new ScalarPlot2D(WIDTH, HEIGHT, WIDTH, HEIGHT);

		points = new ArrayList<Vector>();
		lines = new ArrayList<Line>();
	}

	public void captureEvent(Capture c) {
		c.read();
		c.loadPixels();
		video = new Image(c.width, c.height, c.pixels);

		gvideo = video.luminance(gvideo).blur(2, gvideo);
		pgvideo = PImageAdapter.copyPImage(gvideo, pgvideo);

		xedges = gvideo.filter(FilterFactory.ROBERTS_CROSS_X, xedges);
		yedges = gvideo.filter(FilterFactory.ROBERTS_CROSS_Y, yedges);
		edges = xedges.add(yedges, edges).normalize(.1, edges);
		pedges = PImageAdapter.copyPImage(edges, pedges);

		tedges = edges.threshold(threshold, tedges);
		ptedges = PImageAdapter.copyPImage(tedges, ptedges);

		hough.calculate(tedges);
		houghPlot.setValues(hough.getValues());

		points = hough.getPoints(detection);
		lines = hough.getLines(detection);
	}

	@Override
	public void draw() {
		background(255);

		try {
			image(pgvideo, 0, 0, WIDTH, HEIGHT);

			stroke(255, 0, 0);
			noFill();
			pushMatrix();
			scale(SCALE);
			for (Line line : lines)
				line.paint(g);
			popMatrix();

			image(pedges, WIDTH, 0, WIDTH, HEIGHT);
			image(ptedges, 0, HEIGHT, WIDTH, HEIGHT);
			houghPlot.paint(g);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		for (Vector p : points) {
			int x = (int) (p.x * WIDTH / Math.PI);
			int y = (int) (p.y * HEIGHT / (2 * hough.max));
			ellipse(WIDTH + x, 1.5f * HEIGHT + y, 10, 10);
		}

		drawHistogram(gvideo.getHistogram(), 2 * WIDTH + 48, 139);
	}

	@Override
	public void exit() {
		capture.dispose();
		super.exit();
	}

	@Override
	public void keyPressed() {
		int step = 2;
		switch (key) {
		case '=':
		case '+':
			if (threshold <= 255 - step)
				threshold += step;
			break;
		case '-':
		case '_':
			if (threshold >= step)
				threshold -= step;
			break;
		case '[':
			if (detection >= 2)
				detection--;
			break;
		case ']':
			detection++;
			break;
		}
		println("threshold = " + threshold);
		println("detection = " + 1f / detection);
	}

	private void drawHistogram(Histogram histogram, int x, int y) {
		int[] h = histogram.getHistogram();
		for (int i = 0; i < 256; i++)
			line(x + i, y, x + i, y - 128 * h[i] / histogram.getMaxPixels());
	}
}
