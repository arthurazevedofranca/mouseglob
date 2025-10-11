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
package dcc.mouseglob.movie;

import processing.core.PApplet;
import processing.core.PImage;
import dcc.graphics.Color;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.Image;
import dcc.graphics.plot.Plot.Label;
import dcc.graphics.plot.oned.Axis;
import dcc.graphics.plot.oned.SeriesPlot;
import dcc.graphics.series.Series1D;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.movie.FrameQueue.QueueMonitor;

@SuppressWarnings("serial")
public class MovieTest extends PApplet implements MovieListener,
		NewFrameListener {
	private static final int MOVIE_WIDTH = 600;
	private static final int MOVIE_HEIGHT = 400;
	private static final int PLOT_WIDTH = 800;

	private int delay = 50;

	private MovieManager movieManager;
	private PImage frame;
	private float duration;

	private QueueMonitor monitor;
	private Series1D queueSeries;
	private SeriesPlot queuePlot;
	private Series1D speedSeries;
	private SeriesPlot speedPlot;

	@Override
	public void setup() {
		size(MOVIE_WIDTH + PLOT_WIDTH, MOVIE_HEIGHT);
		movieManager = new MovieManager(this);
		movieManager.addMovieListener(this);
		movieManager.addNewFrameListener(this);
		movieManager.open("K:\\Daniel\\workspace\\MouseGlob\\data\\Video 1.mov",
				100);

		monitor = movieManager.getQueueMonitor();

		Axis Ot = Axis.autoscalingMax(0, 100);

		queueSeries = new Series1D();
		queuePlot = new SeriesPlot(MOVIE_WIDTH, 0, PLOT_WIDTH,
				MOVIE_HEIGHT / 2, Ot, Axis.autoscalingMax(0, 1));
		queuePlot.add(queueSeries, Color.RED);
		queuePlot.setLabel(new Label("Queue size"));

		speedSeries = new Series1D();
		speedPlot = new SeriesPlot(MOVIE_WIDTH, MOVIE_HEIGHT / 2, PLOT_WIDTH,
				MOVIE_HEIGHT / 2, Ot, Axis.autoscalingMax(0, 0.2).setFormat(
						"%.1f"));
		speedPlot.add(speedSeries, Color.rgb(0, 128, 0));
		speedPlot.setLabel(new Label("Playback speed"));
	}

	@Override
	public void draw() {
		background(255);

		if (frame != null) {
			image(frame, 0, 0, MOVIE_WIDTH, MOVIE_HEIGHT - 3);
		}
		noStroke();
		float p = movieManager.getPosition();
		duration = movieManager.getDuration();
		if (duration > 0) {
			int h = 5;
			fill(0);
			rect(0, height - h, width, h);
			fill(255);
			rect(1, height - h + 1, (p * (width - 2)) / duration, h - 2);
		}

		queueSeries.add(monitor.getSize());
		speedSeries.add(monitor.getSpeed());

		queuePlot.paint(g);
		speedPlot.paint(g);
	}

	@Override
	public void keyPressed() {
		switch (key) {
		case 'p':
			movieManager.play();
			break;
		case 'a':
			movieManager.pause();
			break;
		case 'r':
			movieManager.rewind();
			break;
		case 'f':
			movieManager.fastForward();
			break;
		case 's':
			movieManager.stop();
			break;
		case '=':
		case '+':
			delay <<= 1;
			break;
		case '-':
		case '_':
			delay = delay > 1 ? delay >> 1 : delay;
			break;
		}
	}

	@Override
	public void mouseReleased() {
		movieManager.jump(mouseX * duration / width);
	}

	@Override
	public void newFrame(Image frame, long time) {
		this.frame = PImageAdapter.imageToPImage(frame);
		System.out.println(time);
//		try {
//			Thread.sleep(delay);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		println(event.getType());
	}
}
