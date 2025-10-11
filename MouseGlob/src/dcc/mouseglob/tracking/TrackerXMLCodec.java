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

import org.w3c.dom.Element;

import dcc.graphics.math.Vector;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.BoundaryMask;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;
import dcc.xml.XMLProcessor;

class TrackerXMLCodec extends XMLProcessor {

	protected TrackerXMLCodec(XMLProcessor processor) {
		super(processor);
	}

	public Element encode(Tracker tracker) {
		Element trackerElement = createElement("tracker");

		String name = tracker.getName();
		if (name != null)
			trackerElement.setAttribute("name", name);
		Vector position = tracker.getPosition();
		trackerElement.setAttribute("x", Double.toString(position.x));
		trackerElement.setAttribute("y", Double.toString(position.y));
		trackerElement
				.setAttribute("size", Integer.toString(tracker.getSize()));

		return trackerElement;
	}

	public Tracker decode(Element trackerElement,
			BoundariesManager boundariesManager) {
		try {
			double x = getDoubleAttribute(trackerElement, "x");
			double y = getDoubleAttribute(trackerElement, "y");
			int size = getIntAttribute(trackerElement, "size");

			BoundaryMask mask = boundariesManager.getMask(x, y);
			Tracker tracker = new Tracker(x, y, size, mask);
			try {
				String name = getAttribute(trackerElement, "name");
				tracker.setName(name);
			} catch (XMLNotFoundException e) {
				e.printStackTrace();
			}

			return tracker;
		} catch (XMLParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
