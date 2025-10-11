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
 * Class which deals with toggle-type events.
 * 
 * @author Daniel Coelho de Castro
 */
public abstract class ToggleEventClass extends TimedEventClass {
	@Deprecated
	@Override
	public void start(long time) {
		trigger(time);
	}

	@Deprecated
	@Override
	public void stop(long time) {
		trigger(time);
	}

	/**
	 * Toggles an event.
	 * <p>
	 * If no event is active, this creates a new one. If there is already an
	 * active event, it is stopped.
	 * 
	 * @param time
	 *            - the time of the trigger call, in milliseconds
	 */
	public void trigger(long time) {
		triggerEvent(new TimedEvent(time), time);
	}

	/**
	 * Toggles an event.
	 * <p>
	 * If no event is active, this creates a new one. If there is already an
	 * active event, it is stopped.
	 * 
	 * @param description
	 *            - a brief description of the event
	 * @param time
	 *            - the time of the trigger call, in milliseconds
	 */
	public void trigger(String description, long time) {
		triggerEvent(new TimedEvent(description, time), time);
	}

	private void triggerEvent(TimedEvent event, long time) {
		if (!isActive())
			startEvent(event);
		else
			stopCurrentEvent(time);
	}

}
