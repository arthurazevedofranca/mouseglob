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
package dcc.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class ImageLoader {

	private ImageLoader() {
	}

	public static Image load(String fileName) {
		BufferedImage image = null;
		URL imageURL = ImageLoader.class.getResource(fileName);
		if (imageURL != null) {
			try {
				image = ImageIO.read(imageURL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	public static ImageIcon loadIcon(String fileName) {
		ImageIcon icon = null;
		URL iconURL = ImageLoader.class.getResource(fileName);
		if (iconURL != null)
			icon = new ImageIcon(iconURL);
		return icon;
	}

}
