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
package dcc.mouseglob.maze.io;

import dcc.inject.Inject;
import dcc.module.AbstractModule;

public class MazeIO extends
		AbstractModule<MazeIOManager, MazeIOUI, MazeIOController> {

	@Inject
	private MazeIO(MazeIOManager manager, MazeIOUI view,
			MazeIOController controller) {
		super(manager, view, controller);

		controller.setManager(manager);
		controller.setUI(view);
	}

}
