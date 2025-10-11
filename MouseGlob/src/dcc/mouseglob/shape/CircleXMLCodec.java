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
package dcc.mouseglob.shape;

import org.w3c.dom.Element;

import dcc.mouseglob.labelable.Point;
import dcc.mouseglob.labelable.PointXMLCodec;
import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;
import dcc.xml.XMLProcessor;

public class CircleXMLCodec extends AtomicXMLCodec<Circle> {

	public CircleXMLCodec(XMLProcessor processor) {
		super(processor);
	}

	@Override
	public Element encode(Circle circle) {
		Element circleElement = createElement("circle");

		Point center = circle.getCenter();
		PointXMLCodec pointCodec = new PointXMLCodec(this, "center");

		setAttribute(circleElement, "radius", circle.getRadius());
		circleElement.appendChild(pointCodec.encode(center));

		return circleElement;
	}

	@Override
	public Circle decode(Element element) {
		try {
			double radius = getDoubleAttribute(element, "radius");
			Element centerElem = getChild(element, "center");
			Point center = new PointXMLCodec(this).decode(centerElem);
			Circle circle = new Circle(center, radius);
			return circle;
		} catch (XMLNotFoundException | XMLParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
