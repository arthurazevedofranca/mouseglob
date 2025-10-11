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

import processing.core.PImage;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;

public class AppletResizer implements MovieListener {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AppletResizer.class);
	private final MouseGlobApplet applet;

	public AppletResizer(MouseGlobApplet applet) {
		this.applet = applet;
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		if (event.getType() == MovieEventType.LOAD) {
			PImage image = event.getImage();
			log.debug("Applet size before: {} x {}", applet.width, applet.height);
			applet.setAppletSize(image.width, image.height);
			log.info("Applet resized to: {} x {}", applet.width, applet.height);
		}
	}

}
