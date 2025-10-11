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

import dcc.mouseglob.maze.Boundary.BoundaryType;
import dcc.mouseglob.shape.Shape;
import dcc.mouseglob.shape.ShapeXMLCodec;
import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLProcessor;

public class BoundaryXMLCodec extends AtomicXMLCodec<Boundary> {

	private ShapeXMLCodec shapeCodec;

	public BoundaryXMLCodec(XMLProcessor processor) {
		super(processor);
		shapeCodec = new ShapeXMLCodec(this);
	}

	@Override
	public Element encode(Boundary boundary) {
		Element boundaryElement = createElement("boundary");

		BoundaryType type = boundary.getType();
		Shape shape = boundary.getShape();

		boundaryElement.setAttribute("type", type.toString());
		boundaryElement.appendChild(shapeCodec.encode(shape));

		return boundaryElement;
	}

	@Override
	public Boundary decode(Element element) {
		BoundaryType type = BoundaryType.get(element.getAttribute("type"));
		Shape shape = shapeCodec.decode(element);
		Boundary boundary = new Boundary(shape, type);

		return boundary;
	}

}
