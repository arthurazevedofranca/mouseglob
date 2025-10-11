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

import dcc.xml.XMLEncoder;
import dcc.xml.XMLProcessor;

class CalibrationXMLEncoder extends XMLEncoder {

	private final Calibration calibration;

	CalibrationXMLEncoder(XMLProcessor processor, Calibration calibration) {
		super(processor);
		this.calibration = calibration;
	}

	@Override
	public void encode(Element element) {
		setAttribute(element, "scale", calibration.getScale());
	}

	@Override
	public boolean shouldEncode() {
		return calibration.hasScale();
	}

}
