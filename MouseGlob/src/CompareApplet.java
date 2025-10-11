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
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

/**
 * @author Daniel Coelho de Castro
 */
@SuppressWarnings("serial")
public class CompareApplet extends PApplet {
	private PImage template;
	private Capture camera;
	private Match best = null;
	private static final int MAX = 100000000;
	private boolean gray = false;
	private boolean threshold = false;
	private boolean erode = false;
	private boolean dilate = false;
	private boolean blur = false;
	private int fps = 1;

	private Thread thread;

	@Override
	public void setup() {
		template = loadImage("templates.gif");
		size(320, 240);
		fill(0);
		background(255);
		imageMode(CORNER);
		strokeWeight(3);
		camera = new Capture(this, 320, 240);

		thread = new Thread(new Runnable() {
			float delay;

			@Override
			public void run() {
				while (Thread.currentThread() == thread) {
					updateCamera();
					delay = 1000 / fps;
					try {
						Thread.sleep((int) delay);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		});
		thread.start();
	}

	@Override
	public void draw() {
		if (mousePressed)
			// ellipse(mouseX, mouseY, 3, 3);
			line(mouseX, mouseY, pmouseX, pmouseY);
		image(camera, 0, 0);
		if (best != null) {
			best.display();
		}
	}

	private void updateCamera() {
		camera.read();

		if (gray)
			camera.filter(GRAY);

		if (blur)
			camera.filter(BLUR, 2);

		if (erode)
			camera.filter(ERODE);

		if (dilate)
			camera.filter(DILATE);

		if (threshold) {
			float t = (float) otsuThreshold(camera) / 256;
			camera.filter(THRESHOLD, t);
		}
	}

	@Override
	public void keyPressed() {
		switch (key) {
		case ' ':
			loadPixels();
			template.loadPixels();
			best = findMatch(camera, template);
			println(best);
			break;
		case 'g':
		case 'G':
			gray = !gray;
			break;
		case 't':
		case 'T':
			threshold = !threshold;
			break;
		case 'e':
		case 'E':
			erode = !erode;
			break;
		case 'd':
		case 'D':
			dilate = !dilate;
			break;
		case 'b':
		case 'B':
			blur = !blur;
			break;
		case '=':
		case '+':
			fps++;
			println(fps + " fps");
			break;
		case '-':
		case '_':
			fps--;
			println(fps + " fps");
			break;
		default:
			background(255);
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { CompareApplet.class.getName() });
	}

	private Match findMatch(PImage search, PImage templates) {
		int step = templates.width / 9;
		Match[] matches = new Match[9];
		int minIndex = -1;
		float minSSD = MAX;

		for (int i = 0; i < 9; i++) {
			PImage part = templates.get(i * step, 0, step, templates.height);
			matches[i] = compare(search, part);
			matches[i].setSymbol((char) (i + '1'));

			if (matches[i].SSD < minSSD) {
				minSSD = matches[i].SSD;
				minIndex = i;
			}
		}

		return matches[minIndex];
	}

	private Match compare(PImage search, PImage template) {
		float minSSD = MAX;
		float SSD;
		int[] best = new int[2];
		int k = 2;
		search.loadPixels();
		template.loadPixels();

		// loop through the search image
		for (int x = 0; x <= search.width - template.width; x += k) {
			for (int y = 0; y <= search.height - template.height; y += k) {
				SSD = 0;

				// loop through the template image
				for (int i = 0; i < template.width; i += k) {
					for (int j = 0; j < template.height; j += k) {
						float s = red(search.pixels[x + i + search.width
								* (y + j)]);
						float t = red(template.pixels[i + template.width * j]);
						float d = s - t;
						SSD += d * d;
					}
				}

				// save the best found position
				if (minSSD > SSD) {
					minSSD = SSD;
					// give me VALUE_MAX
					best[0] = x;
					best[1] = y;
				}
			}
		}

		println("minSAD = " + minSSD);

		return new Match(best[0], best[1], minSSD, template);
	}

	/**
	 * Calculates a threshold value using Otsu's Method.
	 * 
	 * @param image
	 *            image to be analyzed
	 * @return calculated threshold value
	 */
	int otsuThreshold(PImage image) {
		int t;
		int total = image.width * image.height;
		int thr = 0;
		float m1 = 0, m2 = 0;
		float sum = 0, sum1 = 0;
		float max = 0;
		int w1 = 0, w2 = 0;
		float var;
		int[] hist = new int[256];
		image.loadPixels();

		for (int i = 0; i < image.pixels.length; i++) {
			int c = 0xFF & image.pixels[i];
			hist[c]++;
		}

		for (int i = 0; i < hist.length; i++) {
			sum += i * hist[i];
		}

		for (t = 0; t < 256; t++) {
			w1 += hist[t];
			if (w1 == 0)
				continue;

			w2 = total - w1;
			if (w2 == 0)
				break;
			sum1 += (t * hist[t]);
			m1 = sum1 / w1;
			m2 = (sum - sum1) / w2;
			var = (float) w1 * (float) w2 * (m1 - m2) * (m1 - m2);
			if (var > max) {
				max = var;
				thr = t;
			}
		}

		return thr;
	}

	private class Match {
		/** Horizontal coordinate. */
		public int x;
		/** Vertical coordinate. */
		public int y;
		/** SSD */
		public float SSD;
		/** The template matched. */
		public PImage template;
		/** The symbol matched. */
		public char symbol;

		public Match(int x, int y, float SSD, PImage template) {
			this.x = x;
			this.y = y;
			this.SSD = SSD;
			this.template = template;
		}

		public void setSymbol(char s) {
			symbol = s;
		}

		public void display() {
			image(template, x, y);
		}

		@Override
		public String toString() {
			return x + ", " + y + ": " + SSD
					+ (symbol != 0 ? (" (\'" + symbol + "\')") : "")
					+ (template != null ? " [template]" : "");
		}
	}
}
