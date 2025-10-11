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
import java.util.Collections;
import java.util.List;

import dcc.event.EventManager.TimedEventManager;
import dcc.event.FilteredTimedEventClass;
import dcc.event.FilteredTimedEventClass.FilteredTimedEventListener;
import dcc.event.TimedEvent;
import dcc.event.TimedEventClass;
import dcc.inject.Inject;
import dcc.mouseglob.PropertiesManager;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.tracking.Tracker;

@AnalysisInfo("Visit Events")
public class VisitAnalysis implements Analysis,
		TimedEventManager<VisitEventClass>, FilteredTimedEventListener {

	static interface VisitListener {

		void onVisitEvent();

	}

	private static final String THRESHOLD_KEY = "visit.duration.threshold";
	private static final int DEFAULT_THRESHOLD = 2000;

	private final Time time;
	private final List<VisitEventClass> visitClasses, visitSequence;
	private final List<VisitListener> listeners;

	private boolean isStartingVisit = true;
	private VisitEventClass startingVisit = null;

	@Inject
	public VisitAnalysis(Tracker tracker, ZonesManager zonesManager, Time time) {
		int threshold = PropertiesManager.getInstance().getInteger(
				THRESHOLD_KEY, DEFAULT_THRESHOLD);
		this.time = time;
		visitClasses = new ArrayList<VisitEventClass>();
		for (Zone zone : zonesManager.getZones()) {
			VisitEventClass eventClass = new VisitEventClass(zone, tracker,
					threshold);
			eventClass.addListener(this);
			visitClasses.add(eventClass);
		}
		visitSequence = new ArrayList<VisitEventClass>();
		listeners = new ArrayList<VisitListener>();
	}

	@Override
	public void update() {
		long currentTime = time.getMs(-1);
		for (VisitEventClass visitClass : visitClasses)
			visitClass.update(currentTime);
		isStartingVisit = false;
	}

	@Override
	public List<VisitEventClass> getEventClasses() {
		return Collections.unmodifiableList(visitClasses);
	}

	@Override
	public List<VisitEventClass> getEventClassSequence() {
		return Collections.unmodifiableList(visitSequence);
	}

	VisitEventClass getStartingVisit() {
		return startingVisit;
	}

	void addVisitListener(VisitListener listener) {
		listeners.add(listener);
	}

	void removeVisitListener(VisitListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onEventStarted(TimedEventClass eventClass, TimedEvent event) {
		if (isStartingVisit)
			startingVisit = (VisitEventClass) eventClass;
		visitSequence.add((VisitEventClass) eventClass);
		for (VisitListener listener : listeners)
			listener.onVisitEvent();
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
		int index = visitSequence.lastIndexOf((VisitEventClass) eventClass);
		visitSequence.remove(index);
	}

}
