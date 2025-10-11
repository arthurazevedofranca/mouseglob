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
package dcc.mouseglob.maze;

import org.w3c.dom.Element;

import dcc.mouseglob.shape.Shape;
import dcc.mouseglob.shape.ShapeXMLCodec;
import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLProcessor;

public class ZoneXMLCodec extends AtomicXMLCodec<Zone> {

	private ShapeXMLCodec shapeCodec;

	public ZoneXMLCodec(XMLProcessor processor) {
		super(processor);
		shapeCodec = new ShapeXMLCodec(this);
	}

	@Override
	public Element encode(Zone zone) {
		Element zoneElement = createElement("zone");

		String name = zone.getName();
		Shape shape = zone.getShape();

		if (name != null)
			zoneElement.setAttribute("name", name);

		zoneElement.appendChild(shapeCodec.encode(shape));

		return zoneElement;
	}

	@Override
	public Zone decode(Element element) {
		String name = element.getAttribute("name");
		Shape shape = shapeCodec.decode(element);
		Zone zone = new Zone(shape);

		if (name != null)
			zone.setName(name);

		return zone;
	}
}
