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
import dcc.module.AbstractModule;
import dcc.module.ModuleNotInitializedException;

public class Camera extends
		AbstractModule<CameraManager, CameraView, CameraController> {
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;

	private static Camera instance;

	@Inject
	private Camera(CameraManager manager, CameraView view,
			CameraController controller) {
		super(manager, view, controller);

		instance = this;
	}

	public static Camera getInstance() {
		if (instance == null)
			throw new ModuleNotInitializedException("Camera");
		return instance;
	}
}
