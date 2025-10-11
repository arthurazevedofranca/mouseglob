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
import dcc.xml.XMLProcessor;

public class PolygonXMLCodec extends AtomicXMLCodec<Polygon> {

	public PolygonXMLCodec(XMLProcessor processor) {
		super(processor);
	}

	@Override
	public Element encode(Polygon polygon) {
		Element polygonElement = createElement("polygon");
		PointXMLCodec pointCodec = new PointXMLCodec(this, "vertex");
		for (Point vertex : polygon.getVertices())
			polygonElement.appendChild(pointCodec.encode(vertex));
		return polygonElement;
	}

	@Override
	public Polygon decode(Element element) {
		Polygon polygon = new Polygon();
		PointXMLCodec pointCodec = new PointXMLCodec(this);
		for (Element vertexElement : getChildren(element))
			polygon.addVertex(pointCodec.decode(vertexElement));
		return polygon;
	}

}
