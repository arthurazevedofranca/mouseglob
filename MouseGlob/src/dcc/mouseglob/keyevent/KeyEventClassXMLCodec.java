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
package dcc.mouseglob.keyevent;

import org.w3c.dom.Element;

import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLParseException;
import dcc.xml.XMLProcessor;

class KeyEventClassXMLCodec extends AtomicXMLCodec<KeyEventClass> {

	public KeyEventClassXMLCodec(XMLProcessor processor) {
		super(processor);
	}

	@Override
	public Element encode(KeyEventClass kec) {
		Element kecElement = createElement("keyevent");
		kecElement.setAttribute("label", kec.getLabel());
		kecElement.setAttribute("keycode", Integer.toString(kec.getKeyCode()));
		return kecElement;
	}

	@Override
	public KeyEventClass decode(Element kecElement) {
		try {
			String label = getAttribute(kecElement, "label");
			int keyCode = getIntAttribute(kecElement, "keycode");
			return new KeyEventClass(label, keyCode);
		} catch (XMLParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
