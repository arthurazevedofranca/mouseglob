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
package dcc.graphics;

import java.awt.image.BufferedImage;

import processing.core.PConstants;
import processing.core.PImage;
import dcc.graphics.image.Image;

public final class PImageAdapter {

	private PImageAdapter() {
	}

	public static Image pImageToImage(PImage pImage) {
		if (pImage == null)
			return null;

		pImage.loadPixels();
		Image image = new Image(pImage.width, pImage.height, pImage.pixels);

		return image;
	}

	public static PImage imageToPImage(Image image) {
		if (image == null)
			return null;

		PImage pImage = new PImage(0, 0, PConstants.ARGB);
		pImage.pixels = image.getPixels();
		pImage.width = image.getWidth();
		pImage.height = image.getHeight();
		pImage.updatePixels();

		return pImage;
	}

	public static PImage copyPImage(Image image, PImage pImage) {
		if (image == null)
			return null;

		if (pImage != null && pImage.width == image.getWidth()
				&& pImage.height == image.getHeight()) {
			int[] pixels = image.getPixels();
			System.arraycopy(pixels, 0, pImage.pixels, 0, pixels.length);
			pImage.updatePixels();
		} else
			pImage = imageToPImage(image);

		return pImage;
	}

	/**
	 * Convert a BufferedImage to Image, reusing the destination buffer when possible
	 */
	public static Image bufferedToImage(BufferedImage src, Image reuse) {
		if (src == null) return null;
		int w = src.getWidth();
		int h = src.getHeight();
		Image out = reuse;
		if (out == null) out = new Image(w, h);
		else out.setSize(w, h);
		src.getRGB(0, 0, w, h, out.getPixels(), 0, w);
		return out;
	}
}
