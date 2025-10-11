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
package dcc.mouseglob.visit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dcc.event.FilteredTimedEventClass;
import dcc.event.TimedEvent;
import dcc.event.TimedEventClass;
import dcc.event.EventManager.TimedEventManager;
import dcc.event.FilteredTimedEventClass.FilteredTimedEventListener;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.maze.ZoneEvent;
import dcc.mouseglob.maze.ZoneListener;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.tracking.Tracker;
import dcc.mouseglob.tracking.TrackingEvent;
import dcc.mouseglob.tracking.TrackingListener;
import dcc.mouseglob.tracking.TrackingManager;
import dcc.tree.DefaultTreeable;
import dcc.tree.TreeNode;

/**
 * Manages interaction between trackers and zones.
 * 
 * @author Daniel Coelho de Castro
 */
public final class VisitEventManager extends DefaultTreeable implements
		NewFrameListener, TrackingListener, ZoneListener,
		FilteredTimedEventListener, TimedEventManager<VisitEventClass> {

	private final List<VisitEventClass> visitClasses, sequence;

	@Inject
	private ZonesManager zonesManager;
	@Inject
	private TrackingManager trackingManager;

	private final TreeNode node;

	/**
	 * Constructor for the <code>VisitEventListener</code> class.
	 */
	public VisitEventManager() {
		super("Visit Events", "/resource/zoneEvent16.png");
		visitClasses = new ArrayList<VisitEventClass>();
		sequence = new ArrayList<VisitEventClass>();

		node = getNode();
	}

	synchronized private void add(Zone z, Tracker t) {
		VisitEventClass vec = new VisitEventClass(z, t, 0);
		visitClasses.add(vec);
		node.add(vec);
	}

	/**
	 * Gets the number of times the tracker entered the zone.
	 * 
	 * @param z
	 *            - the zone
	 * @param t
	 *            - the tracker
	 * @return the entry count or <code>-1</code> if the interaction doesn't
	 *         exist
	 */
	public int getEntryCount(Zone z, Tracker t) {
		VisitEventClass temp = search(z, t);
		return temp != null ? temp.getStartCount() : -1;
	}

	/**
	 * Gets the number of times the tracker left the zone.
	 * 
	 * @param z
	 *            - the zone
	 * @param t
	 *            - the tracker
	 * @return the exit count or <code>-1</code> if the interaction doesn't
	 *         exist
	 */
	public int getExitCount(Zone z, Tracker t) {
		VisitEventClass temp = search(z, t);
		return temp != null ? temp.getEndCount() : -1;
	}

	/**
	 * Get the total time the tracker spent inside the zone.
	 * 
	 * @param z
	 *            - the zone
	 * @param t
	 *            - the tracker
	 * @return the total time, in seconds
	 */
	public double getTotalTime(Zone z, Tracker t) {
		VisitEventClass temp = search(z, t);
		return temp != null ? temp.getDurationStatistics().getSum() : -1;
	}

	/**
	 * Gets all trackers that visited the zone.
	 * 
	 * @param z
	 *            - the zone
	 * @return an array containing the trackers or <code>null</code> if none was
	 *         found
	 */
	synchronized public List<Tracker> getTrackers(Zone z) {
		List<Tracker> trackers = new ArrayList<Tracker>();
		for (VisitEventClass i : visitClasses)
			if (i.getZone().equals(z) && i.getStartCount() > 0)
				trackers.add(i.getTracker());
		return trackers;
	}

	/**
	 * Gets all zones visited by the tracker.
	 * 
	 * @param t
	 *            - the tracker
	 * @return an array containing the zones
	 */
	synchronized public List<Zone> getZones(Tracker t) {
		List<Zone> zones = new ArrayList<Zone>();
		for (VisitEventClass temp : visitClasses)
			if (temp.getTracker().equals(t) && temp.getStartCount() > 0)
				zones.add(temp.getZone());
		return zones;
	}

	/**
	 * Searches for the specified interaction.
	 * 
	 * @param z
	 *            - the zone
	 * @param t
	 *            - the tracker
	 * @return the interaction or <code>null</code> if it doesn't exist
	 */
	private synchronized VisitEventClass search(Zone z, Tracker t) {
		VisitEventClass temp = new VisitEventClass(z, t, 0);
		int index = visitClasses.indexOf(temp);

		if (index == -1)
			return null;

		return visitClasses.get(index);
	}

	private synchronized void trackerAdded(Tracker t) {
		for (Zone z : zonesManager.getZones())
			add(z, t);
	}

	private synchronized void trackerRemoved(Tracker t) {
		Iterator<VisitEventClass> it = visitClasses.iterator();

		while (it.hasNext()) {
			VisitEventClass zec = it.next();
			if (zec.getTracker().equals(t)) {
				node.remove(zec);
				it.remove();
			}
		}
	}

	private synchronized void zoneAdded(Zone z) {
		for (Tracker t : trackingManager.getTrackers())
			add(z, t);
	}

	private synchronized void zoneRemoved(Zone z) {
		Iterator<VisitEventClass> it = visitClasses.iterator();

		while (it.hasNext()) {
			VisitEventClass zec = it.next();
			if (zec.getZone().equals(z)) {
				node.remove(zec);
				it.remove();
			}
		}
	}

	@Override
	public synchronized void newFrame(Image frame, long time) {
		for (VisitEventClass visitClass : visitClasses)
			visitClass.update(time);
	}

	@Override
	public void onTrackingEvent(TrackingEvent event) {
		Tracker tracker = event.getTracker();

		switch (event.getType()) {
		case TRACKER_ADDED:
			trackerAdded(tracker);
			break;

		case TRACKER_REMOVED:
			trackerRemoved(tracker);
			break;
		}
	}

	@Override
	public void onZoneEvent(ZoneEvent event) {
		Zone zone = event.getZone();

		switch (event.getType()) {
		case ZONE_ADDED:
			zoneAdded(zone);
			break;

		case ZONE_REMOVED:
			zoneRemoved(zone);
			break;

		case ZONES_CLEARED:
			visitClasses.clear();
			break;
		}
	}

	@Override
	public List<VisitEventClass> getEventClasses() {
		return new ArrayList<VisitEventClass>(visitClasses);
	}

	@Override
	public List<VisitEventClass> getEventClassSequence() {
		return new ArrayList<VisitEventClass>(sequence);
	}

	@Override
	public void onEventStarted(TimedEventClass eventClass, TimedEvent event) {
		sequence.add((VisitEventClass) eventClass);
	}

	@Override
	public void onEventStopped(TimedEventClass eventClass, TimedEvent event) {
	}

	@Override
	public void onEventResumed(FilteredTimedEventClass eventClass,
			TimedEvent event) {
	}

	@Override
	public void onEventSuppressed(FilteredTimedEventClass eventClass,
			TimedEvent event) {
		int index = sequence.lastIndexOf((VisitEventClass) eventClass);
		sequence.remove(index);
	}

}
