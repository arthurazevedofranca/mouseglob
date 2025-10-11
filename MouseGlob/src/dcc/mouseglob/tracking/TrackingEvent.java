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
package dcc.mouseglob.tracking;

import dcc.event.Event;

public class TrackingEvent extends Event {
	public enum TrackingEventType {
		TRACKER_ADDED, TRACKER_REMOVED;
	}

	private final TrackingEventType type;
	private final Tracker tracker;
	private final int trackerCount;

	TrackingEvent(TrackingEventType type, Tracker tracker, int trackerCount) {
		this.type = type;
		this.tracker = tracker;
		this.trackerCount = trackerCount;
	}

	public TrackingEventType getType() {
		return type;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public int getTrackerCount() {
		return trackerCount;
	}
}
