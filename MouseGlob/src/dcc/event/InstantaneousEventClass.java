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
 * Class which manages instantaneous <code>Event</code>s.
 * 
 * @author Daniel Coelho de Castro
 */
public abstract class InstantaneousEventClass extends EventClass<Event> {
	/**
	 * Registers that a new instantaneous <code>Event</code> has happened.
	 */
	public void trigger() {
		add(new Event());
	}

	/**
	 * Registers that a new instantaneous <code>Event</code> has happened.
	 * 
	 * @param description
	 *            - a brief description of the newly-happened <code>Event</code>
	 */
	public void trigger(String description) {
		add(new Event(description));
	}
}
