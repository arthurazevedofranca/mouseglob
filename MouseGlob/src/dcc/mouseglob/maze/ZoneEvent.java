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

public class ZoneEvent extends Event {
	public enum ZoneEventType {
		ZONE_ADDED, ZONE_REMOVED, ZONES_CLEARED;
	}

	private final ZoneEventType type;
	private final Zone zone;

	ZoneEvent(ZoneEventType type, Zone zone) {
		this.type = type;
		this.zone = zone;
	}

	public ZoneEventType getType() {
		return type;
	}

	public Zone getZone() {
		return zone;
	}
}
