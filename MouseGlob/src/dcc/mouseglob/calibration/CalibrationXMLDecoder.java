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
package dcc.mouseglob.calibration;

import org.w3c.dom.Element;

import dcc.xml.XMLDecoder;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;

class CalibrationXMLDecoder extends XMLDecoder {

	private final CalibrationController controller;

	CalibrationXMLDecoder(CalibrationController controller) {
		this.controller = controller;
	}

	@Override
	public void decode(Element element) {
		controller.reset();
		try {
			float scale = getFloatAttribute(element, "scale");
			controller.setScaleValue(scale);
		} catch (XMLNotFoundException | XMLParseException e) {
			e.printStackTrace();
		}
	}

}
