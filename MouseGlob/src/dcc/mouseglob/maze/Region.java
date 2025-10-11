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
package dcc.mouseglob.maze;

import dcc.graphics.binary.BinaryMask2D;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.shape.Shape;
import dcc.tree.EditableTreeable;

public interface Region extends EditableTreeable, InspectableObject,
		BinaryMask2D {

	String getName();

	Shape getShape();

	boolean contains(Vector p);

	boolean contains(double x, double y);

}
