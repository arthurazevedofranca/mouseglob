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

/**
 * Class which deals which {@link TimedEvent}s.
 * 
 * @author Daniel Coelho de Castro
 */
public abstract class TimedEventClass extends EventClass<TimedEvent> {

	public interface TimedEventListener {

		void onEventStarted(TimedEventClass eventClass, TimedEvent event);

		void onEventStopped(TimedEventClass eventClass, TimedEvent event);

	}

	protected final List<TimedEventListener> listeners;

	protected TimedEvent currentEvent, firstEvent;

	protected int startCount, endCount;
	protected Statistics statistics;

	/**
	 * Constructor for the <code>TimedEventClass</code> class.
	 * 
	 * @param description
	 *            - a brief description of this <code>TimedEventClass</code>
	 */
	public TimedEventClass() {
		listeners = new ArrayList<TimedEventListener>();
		statistics = new Statistics();
	}

	/**
	 * Registers that a new {@link TimedEvent} has started.
	 * 
	 * @param time
	 *            - the time at which the event has started, in milliseconds
	 */
	public void start(long time) {
		if (!isActive())
			startEvent(getNewEvent(time));
	}

	/**
	 * Registers a new {@link TimedEvent}.
	 * 
	 * @param event
	 *            - the event to be registered
	 */
	protected void startEvent(TimedEvent event) {
		if (!isActive()) {
			if (firstEvent == null)
				firstEvent = event;
			currentEvent = event;
			startCount++;
			add(currentEvent);
			for (TimedEventListener listener : listeners)
				listener.onEventStarted(this, currentEvent);
		}
	}

	/**
	 * Registers that the current {@link TimedEvent} has stopped.
	 * 
	 * @param time
	 *            - the time at which the event has stopped, in milliseconds
	 */
	public void stop(long time) {
		stopCurrentEvent(time);
	}

	/**
	 * Registers that the current {@link TimedEvent} has stopped.
	 * 
	 * @param time
	 *            - the time at which the event has stopped, in milliseconds
	 */
	protected void stopCurrentEvent(long time) {
		currentEvent.stop(time);
		statistics.add(currentEvent.getDuration());
		endCount++;
		lastEvent = currentEvent;
		currentEvent = null;
		for (TimedEventListener listener : listeners)
			listener.onEventStopped(this, lastEvent);
	}

	/**
	 * @return the number of {@link TimedEvent}s that have started
	 */
	public int getStartCount() {
		return startCount;
	}

	/**
	 * @return the number of {@link TimedEvent}s that have ended
	 */
	public int getEndCount() {
		return endCount;
	}

	/**
	 * Calculates duration statistics on all finished events
	 */
	public Statistics getDurationStatistics() {
		return statistics;
	}

	/**
	 * Calculates duration statistics on all events, including the current one.
	 */
	public Statistics getDurationStatistics(long time) {
		if (!isActive())
			return statistics;
		Statistics clone = new Statistics(statistics);
		clone.add(currentEvent.getCurrentDuration(time));
		return clone;
	}

	public long getLatency() {
		if (firstEvent != null)
			return firstEvent.getStartTime();
		return 0;
	}

	public boolean isActive() {
		return currentEvent != null;
	}

	public TimedEvent getEventAt(long time) {
		for (TimedEvent event : getEvents()) {
			if (event.getStartTime() < time && event.getEndTime() > time)
				return event;
		}
		return null;
	}

	public TimedEvent getNewEvent(long time) {
		return new TimedEvent(time);
	}

	public void addListener(TimedEventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(TimedEventListener listener) {
		listeners.remove(listener);
	}

}
