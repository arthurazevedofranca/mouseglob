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
package dcc.mouseglob.camera;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.movie.MovieEvent;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;
import dcc.mouseglob.movie.MovieListener;

public class CameraController extends AbstractController<Camera> implements
		MovieListener {

	@Inject
	private CameraManager manager;

	@Override
	public void onMovieEvent(MovieEvent event) {
		if (event.getType() == MovieEventType.LOAD)
			manager.stop();
	}
}
