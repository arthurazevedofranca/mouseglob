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
package dcc.event;

import java.util.ArrayList;
import java.util.List;

import dcc.graphics.math.stats.Statistics;

public abstract class FilteredTimedEventClass extends TimedEventClass {

	public interface FilteredTimedEventListener extends TimedEventListener {

		void onEventResumed(FilteredTimedEventClass eventClass, TimedEvent event);

		void onEventSuppressed(FilteredTimedEventClass eventClass,
				TimedEvent event);

	}

	private final long durationThreshold;
	private final List<FilteredTimedEventListener> listeners;

	private Statistics backupStatistics;

	public FilteredTimedEventClass(long durationThreshold) {
		this.durationThreshold = durationThreshold;
		listeners = new ArrayList<FilteredTimedEventListener>();
	}

	@Override
	public void start(long time) {
		if (isActive())
			return;
		TimedEvent last = getLastEvent();
		if (last != null && (time - last.getEndTime() < durationThreshold)) {
			endCount--;
			last.resume();
			notifyResumed(last);
			currentEvent = last;
			if (backupStatistics != null)
				statistics = backupStatistics;
		} else {
			super.start(time);
		}
	}

	@Override
	public void stop(long time) {
		if (time - currentEvent.getStartTime() < durationThreshold) {
			startCount--;
			remove(currentEvent);
			notifySuppressed(currentEvent);
			if (firstEvent == currentEvent)
				firstEvent = null;
			currentEvent = null;
		} else {
			backupStatistics = new Statistics(getDurationStatistics());
			super.stop(time);
		}
	}

	public void addListener(FilteredTimedEventListener listener) {
		super.addListener(listener);
		listeners.add(listener);
	}

	public void removeListener(FilteredTimedEventListener listener) {
		super.removeListener(listener);
		listeners.remove(listener);
	}

	private void notifyResumed(TimedEvent event) {
		for (FilteredTimedEventListener listener : listeners)
			listener.onEventResumed(this, event);
	}

	private void notifySuppressed(TimedEvent event) {
		for (FilteredTimedEventListener listener : listeners)
			listener.onEventSuppressed(this, event);
	}

}
