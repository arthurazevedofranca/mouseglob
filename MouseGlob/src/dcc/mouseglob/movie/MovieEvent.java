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
import dcc.event.Event;

public class MovieEvent extends Event {
	public enum MovieEventType {
		OPEN, LOAD, PLAY, PAUSE, REWIND, JUMP, CLOSE;
	}

	private final MovieEventType type;
	private final PImage image; // may be null depending on event type
	private final String fileName;

	MovieEvent(MovieEventType type, PImage image, String fileName) {
		this.type = type;
		this.image = image;
		this.fileName = fileName;
	}

	public MovieEventType getType() {
		return type;
	}

	public PImage getImage() {
		return image;
	}

	public String getFileName() {
		return fileName;
	}
}
