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

/**
 * An {@link Event} whose start, current and end timestamps and duration are
 * relevant for the application.
 * 
 * @author Daniel Coelho de Castro
 */
public class TimedEvent extends Event {
	private long startTime;
	private long endTime;

	/**
	 * Basic constructor for the <code>TimedEvent</code> class.
	 * 
	 * @param time
	 *            - the initial timestamp, in milliseconds
	 */
	public TimedEvent(long time) {
		startTime = time;
		endTime = Long.MIN_VALUE;
	}

	/**
	 * Constructor for the <code>TimedEvent</code> class.
	 * 
	 * @param description
	 *            - a brief description of this <code>TimedEvent</code>
	 * @param time
	 *            - the initial timestamp, in milliseconds
	 */
	public TimedEvent(String description, long time) {
		this(time);
		setDescription(description);
	}

	/**
	 * Stops this event and calculates its final duration.
	 * 
	 * @param time
	 *            - the final timestamp, in milliseconds
	 */
	public void stop(long time) {
		endTime = time;
	}

	public void resume() {
		endTime = Long.MIN_VALUE;
	}

	/**
	 * @return the current duration of this event if it is active, or the final
	 *         duration otherwise
	 */
	public long getCurrentDuration(long time) {
		if (!isActive())
			return getDuration();
		return time - startTime;
	}

	/**
	 * @return the final duration of this event
	 */
	public long getDuration() {
		return endTime - startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public boolean isActive() {
		return endTime < 0;
	}

	@Override
	public String toString() {
		return startTime + " - " + (endTime > 0 ? endTime : "");
	}
}
