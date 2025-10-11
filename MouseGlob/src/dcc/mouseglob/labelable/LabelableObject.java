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
import dcc.graphics.math.Vector;
import dcc.identifiable.Identifiable;

/**
 * Abstract class that manages objects which have labels. Supports
 * <code>Zone</code>, <code>Tracker</code>, <code>Point</code>,
 * <code>Trajectory</code> and <code>Calibration</code> objects.
 * 
 * @author Daniel Coelho de Castro
 */
public abstract class LabelableObject extends StyleableObject implements
		Identifiable {

	private String name;
	private Label label = new Label();

	/**
	 * Displays the label. Should be called at the end of the
	 * <code>paint()</code> method.
	 * 
	 * @param g
	 *            - the graphics context onto which to paint
	 */
	public void paintLabel(PGraphics g) {
		g.pushStyle();
		label.setStyle(getStyle().getLabelStyle());
		label.setText(getName());
		label.paint(g, getLabelPosition());
		g.popStyle();
	}

	/**
	 * Gets the position of the label.
	 * 
	 * @return coordinates of the point where the label is to be displayed
	 */
	protected abstract Vector getLabelPosition();

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
