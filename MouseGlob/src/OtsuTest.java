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
import processing.core.PConstants;
import processing.core.PImage;
import processing.video.Capture;
import dcc.graphics.Box;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Histogram;

@SuppressWarnings("serial")
public class OtsuTest extends PApplet {
	private Capture capture;
	private GrayscaleImage camImage;
	private PImage camPImage;
	private Histogram histogram;
	private int otsu;
	private BinaryImage thresImage;
	private PImage thresPImage;

	private final int WIDTH = 640;
	private final int HEIGHT = 360;
	private final int CAM_WIDTH = 1280;
	private final int CAM_HEIGHT = 720;

	private int size = 100;

	@Override
	public void setup() {
		size(2 * WIDTH, 2 * HEIGHT);

		rectMode(PConstants.CORNERS);
		// println(Capture.list());
		capture = new Capture(this, CAM_WIDTH, CAM_HEIGHT);
		capture.start();
	}

	public void captureEvent(Capture c) {
		c.read();
		c.loadPixels();
		camImage = PImageAdapter.pImageToImage(c).luminance(camImage);
		camPImage = PImageAdapter.copyPImage(camImage, camPImage);
		histogram = new Histogram(camImage.get(getBox().toInt()));
		otsu = histogram.getOtsuThreshold();
		thresImage = camImage.threshold(otsu, thresImage);
		thresPImage = PImageAdapter.copyPImage(thresImage, thresPImage);
	}

	@Override
	public void draw() {
		background(255);
		try {
			image(camPImage, 0, 0, WIDTH, HEIGHT);
			image(thresPImage, WIDTH, 0, WIDTH, HEIGHT);

			drawHistogram(histogram, 48, HEIGHT + 139);
			pushStyle();
			stroke(255, 0, 0);
			line(48 + otsu, HEIGHT + 139, 48 + otsu, HEIGHT + 11);
			popStyle();
		} catch (NullPointerException e) {
		}

		pushStyle();
		stroke(0, 255, 0);
		noFill();
		rect(getPX0(), getPY0(), getPX1(), getPY1());
		popStyle();
	}

	@Override
	public void keyPressed() {
		switch (key) {
		case '-':
		case '_':
			if (size > 1)
				size--;
			break;
		case '=':
		case '+':
			if (size < CAM_WIDTH)
				size++;
			break;
		}
	}

	private int getPX0() {
		return min(max(mouseX - size * WIDTH / CAM_WIDTH, 0), WIDTH - 1);
	}

	private int getPY0() {
		return min(max(mouseY - size * HEIGHT / CAM_HEIGHT, 0), HEIGHT - 1);
	}

	private int getPX1() {
		return min(max(mouseX + size * WIDTH / CAM_WIDTH, 0), WIDTH - 1);
	}

	private int getPY1() {
		return min(max(mouseY + size * HEIGHT / CAM_HEIGHT, 0), HEIGHT - 1);
	}

	private Box getBox() {
		Box CAM_BOX = Box.fromSize(CAM_WIDTH - 1, CAM_HEIGHT - 1);
		Box mouseBox = Box.fromRadius(mouseX * CAM_WIDTH / WIDTH, mouseY
				* CAM_HEIGHT / HEIGHT, size);
		return mouseBox.clamp(CAM_BOX);
	}

	@Override
	public void exit() {
		capture.dispose();
		super.exit();
	}

	private void drawHistogram(Histogram histogram, int x, int y) {
		int[] h = histogram.getHistogram();
		for (int i = 0; i < 256; i++)
			line(x + i, y, x + i, y - 128 * h[i] / histogram.getMaxPixels());
	}
}
