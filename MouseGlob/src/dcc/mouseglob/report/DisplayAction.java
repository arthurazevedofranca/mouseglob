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
package dcc.mouseglob.report;

import dcc.graphics.Paintable;
import dcc.graphics.PaintingEnvironment;
import dcc.ui.ToggleAction;

@SuppressWarnings("serial")
public class DisplayAction extends ToggleAction {

	private final PaintingEnvironment environment;
	private final Paintable paintable;

	public DisplayAction(String name, PaintingEnvironment environment,
			Paintable paintable) {
		super(name);
		this.environment = environment;
		this.paintable = paintable;
	}

	@Override
	public void itemStateChanged(boolean state) {
		if (state)
			environment.addPaintable(paintable);
		else
			environment.removePaintable(paintable);
	}

}
