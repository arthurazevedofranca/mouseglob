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

import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLProcessor;

public class ShapeXMLCodec extends AtomicXMLCodec<Shape> {

	private CircleXMLCodec circleCodec;
	private PolygonXMLCodec polygonCodec;

	public ShapeXMLCodec(XMLProcessor processor) {
		super(processor);
		circleCodec = new CircleXMLCodec(this);
		polygonCodec = new PolygonXMLCodec(this);
	}

	@Override
	public Element encode(Shape shape) {
		if (shape instanceof Circle)
			return circleCodec.encode((Circle) shape);
		else
			return polygonCodec.encode((Polygon) shape);
	}

	@Override
	public Shape decode(Element element) {
		Shape shape = null;
		try {
			shape = circleCodec.decode(getChild(element, "circle"));
		} catch (XMLNotFoundException e) {
		}

		try {
			if (shape == null)
				shape = polygonCodec.decode(getChild(element, "polygon"));
		} catch (XMLNotFoundException e) {
		}
		return shape;
	}

}
