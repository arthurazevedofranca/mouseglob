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

import dcc.identifiable.Identifiable;

/**
 * Abstract class which manages a certain type of <code>Event</code>s.
 * 
 * @author Daniel Coelho de Castro
 * 
 * @param <T>
 *            - the type of <code>Event</code> that this class will manage
 */
abstract public class EventClass<T extends Event> implements Identifiable {
	// TODO Make EventClasses Inspectable
	private List<T> events;
	protected T lastEvent;

	/**
	 * Constructor for the <code>EventClass</code> class.
	 * 
	 * @param description
	 *            - a brief description of this <code>EventClass</code>
	 */
	protected EventClass() {
		events = new ArrayList<T>();
		lastEvent = null;
	}

	/**
	 * @deprecated Use {@link #getDescription()}
	 */
	@Deprecated
	@Override
	public String getName() {
		return getDescription();
	}

	/**
	 * Gets the description of this <code>EventClass</code>.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * Gets a short description of this <code>EventClass</code>, mostly for
	 * display purposes.
	 * 
	 * @return the short description. Defaults to <code>getDescription()</code>
	 *         if it has not been overridden.
	 */
	public String getShortDescription() {
		return getDescription();
	}

	/**
	 * Adds a new <code>Event</code> to this category.
	 * 
	 * @param event
	 *            - the newly-happened <code>Event</code>
	 */
	protected void add(T event) {
		events.add(event);
		lastEvent = event;
	}

	protected void remove(T event) {
		events.remove(event);
		lastEvent = events.isEmpty() ? null : events.get(getEventCount() - 1);
	}

	/**
	 * @return the number of registered <code>Event</code>s
	 */
	public int getEventCount() {
		return events.size();
	}

	/**
	 * @return the last <code>Event</code> which happened
	 */
	public T getLastEvent() {
		return lastEvent;
	}

	public List<T> getEvents() {
		return new ArrayList<T>(events);
	}

}
