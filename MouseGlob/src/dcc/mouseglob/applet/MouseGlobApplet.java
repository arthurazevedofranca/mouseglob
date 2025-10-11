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
package dcc.mouseglob.applet;

import processing.core.PImage;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.Image;

/**
 * TODO Use movie name as default for experiment, zone and coords file names
 * 
 * TODO Dynamic thresholding?
 * 
 * TODO Undo?
 */

/**
 * MouseGlob bugs & fixes wishlist
 *
 * 13mai12 (v. 10mai12)
 *
 * TODO 10. Escrever o nome do arquivo de vídeo (ou "Live from camera") no cabeçalho do arquivo de coordenadas.
 *
 * TODO 11. Escrever o nome do arquivo de vídeo (ou "Live from camera") na janela de exibição do vídeo (MouseGlob).
 *
 * TODO 13: BUG: a exibição está travando, pelo menos com arquivos AVI de > 350MB (~15 min). Nesses arquivos (testei 3 diferentes) trava sempre aos 49 segundos de exibição. Estranho: se estiver tracking ON, continua escrevendo no arquivo de coordenadas, apesar da imagem e a janela de Properties ficarem congeladas. 
 *
 *
 * ========================== 
 * 23out11
 *
 * TODO Main window: create a default zone corresponding to the whole image rectangle, so that the tracker statistics appear even if no zones are defined. 
 */

/**
 * Applet which tracks globs in a live video stream (camera) or a movie file.
 * 
 * @author Daniel Coelho de Castro
 */
@SuppressWarnings("serial")
public final class MouseGlobApplet extends DefaultApplet implements
		NewFrameListener, CursorListener {

	private PImage frame;
	private boolean sizeChanged = false;

	@Override
	public void setup() {
		super.setup();
		textFont(loadFont("AdvoCut-10.vlw"));
	}

	@Override
	public void draw() {
		background(0);

		if (frame != null && frame.pixels != null) {
			if (sizeChanged) {
				// invalidate caches if needed (no-op for Processing 3+)
				sizeChanged = false;
			}
			image(frame, 0, 0);
		}

		super.draw();
	}

	@Override
	public void newFrame(Image newFrame, long time) {
		if (frame != null && newFrame != null) {
			if (newFrame.getWidth() != frame.width
					|| newFrame.getHeight() != frame.height)
				sizeChanged = true;
		}
		frame = PImageAdapter.copyPImage(newFrame, frame);
		redraw();
	}

}
