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
package dcc.mouseglob.shape;

import dcc.graphics.Paintable;
import dcc.graphics.binary.BinaryMask2D;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.labelable.Point;

/**
 * Class that represents a generic zone object.
 * 
 * @author Daniel Coelho de Castro
 */
public abstract class Shape implements Paintable, InspectableObject,
		BinaryMask2D {

	private Inspector inspector;

	/**
	 * Determines whether this is a valid shape or not.
	 * 
	 * @return true if it is, false otherwise
	 */
	abstract boolean exists();

	/**
	 * Determines whether the given point is inside the <code>Shape</code>.
	 * 
	 * @param x
	 *            - horizontal coordinate of the point.
	 * @param y
	 *            - vertical coordinate of the point.
	 * @return true if it belongs to this <code>Shape</code>, false otherwise.
	 */
	public abstract boolean contains(double x, double y);

	/**
	 * Determines whether the given <code>Vector</code> is inside this
	 * <code>Shape</code>.
	 * 
	 * @param p
	 *            - the point
	 * @return <code>true</code> if it belongs to this <code>Shape</code>,
	 *         <code>false</code> otherwise
	 */
	public final boolean contains(Vector p) {
		return contains(p.x, p.y);
	}

	@Override
	public final boolean get(int i, int j) {
		return contains(i, j);
	}

	/**
	 * Gets a formatted string corresponding to the shape's information. This
	 * function should be used to save the shapes to a file.
	 * 
	 * @return the formatted string
	 */
	public abstract String getCoordinates();

	/**
	 * Gets the <code>Point</code> which is currently selected.
	 * 
	 * @return the <code>Point</code> or <code>null</code> if none is selected
	 */
	abstract Point getSelectedPoint(int mouseX, int mouseY);

	/**
	 * Gets the <code>Vector</code> which determines this <code>Shape</code>'s
	 * label's position, usually the northwesternmost point.
	 * 
	 * @return the handle
	 */
	public abstract Vector getLabelPosition();

	protected abstract Inspector makeInspector();

	void updateInspector() {
		if (inspector != null)
			inspector.update();
	}

	@Override
	public final Inspector getInspector() {
		if (inspector == null)
			inspector = makeInspector();
		return inspector;
	}

}
