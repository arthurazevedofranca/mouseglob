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
package dcc.graphics.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class GrayscaleImageIO {

	private GrayscaleImageIO() {
	}

	public static GrayscaleImage readImage(String fileName) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(fileName));
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int[] pixels = bufferedImage
				.getRGB(0, 0, width, height, null, 0, width);
		GrayscaleImage image = new GrayscaleImage(width, height, pixels);
		return image;
	}

	public static void saveImage(Image image, String fileName)
			throws IOException {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		bufferedImage.setRGB(0, 0, width, height, image.getPixels(), 0, width);
		ImageIO.write(bufferedImage, "PNG", new File(fileName));
	}

}
