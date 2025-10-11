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

import dcc.identifiable.Identifiable;

/**
 * Basic class which describes that something relevant has happened.
 * 
 * @author Daniel Coelho de Castro
 * @see TimedEvent
 */
public class Event implements Identifiable {
	/**
	 * Brief description of this <code>Event</code>
	 */
	protected String description;

	/**
	 * Basic constructor for the <code>Event</code> class.
	 * <p>
	 * Creates an anonymous <code>Event</code>.
	 */
	public Event() {
		description = null;
	}

	/**
	 * Convenience constructor for the <code>Event</code> class.
	 * <p>
	 * Equivalent to calling the basic constructor then
	 * {@link #setDescription(String)}.
	 * 
	 * @param description
	 *            - a brief description of this <code>Event</code>
	 */
	public Event(String description) {
		this();
		setDescription(description);
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
	 * Gets the previously-set description of this <code>Event</code>.
	 * 
	 * @return the description if it was set beforehand, <code>null</code>
	 *         otherwise
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Defines the text that will describe this <code>Event</code>.
	 * 
	 * @param description
	 *            - a brief description of this <code>Event</code>
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
