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

import dcc.event.Event;

public class BoundaryEvent extends Event {
	public enum BoundaryEventType {
		BOUNDARY_ADDED, BOUNDARY_REMOVED, BOUNDARIES_CLEARED;
	}

	private final BoundaryEventType type;
	private final Boundary boundary;

	BoundaryEvent(BoundaryEventType type, Boundary b) {
		this.type = type;
		this.boundary = b;
	}

	public BoundaryEventType getType() {
		return type;
	}

	public Boundary getBoundary() {
		return boundary;
	}
}
