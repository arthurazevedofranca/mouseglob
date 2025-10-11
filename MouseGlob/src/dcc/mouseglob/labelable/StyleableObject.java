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
package dcc.mouseglob.labelable;

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.identifiable.Selectable;

public abstract class StyleableObject implements Selectable, Paintable {
	private boolean isSelected = false;

	@Override
	public void setSelected(boolean b) {
		isSelected = b;
	}

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	protected abstract Style getStyle();

	/**
	 * Displays the object.
	 * 
	 * @param g
	 *            - the graphics context onto which to paint
	 */
	@Override
	public final void paint(PGraphics g) {
		g.pushStyle();
		getStyle().apply(g);
		doPaint(g);
		g.popStyle();
	}

	protected abstract void doPaint(PGraphics g);
}
