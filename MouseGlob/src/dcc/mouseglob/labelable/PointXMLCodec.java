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
package dcc.mouseglob.labelable;

import org.w3c.dom.Element;

import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;
import dcc.xml.XMLProcessor;

public class PointXMLCodec extends AtomicXMLCodec<Point> {

	private final String elementName;

	public PointXMLCodec(XMLProcessor processor) {
		this(processor, "point");
	}

	public PointXMLCodec(XMLProcessor processor, String elementName) {
		super(processor);
		this.elementName = elementName;
	}

	@Override
	public Element encode(Point point) {
		Element pointElement = createElement(elementName);
		setAttribute(pointElement, "x", point.x);
		setAttribute(pointElement, "y", point.y);
		return pointElement;
	}

	@Override
	public Point decode(Element element) {
		try {
			double x = getDoubleAttribute(element, "x");
			double y = getDoubleAttribute(element, "y");
			return new Point(x, y);
		} catch (XMLNotFoundException | XMLParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
