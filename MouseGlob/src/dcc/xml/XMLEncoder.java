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
package dcc.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dcc.xml.XMLProcessor.IXMLEncoder;

public abstract class XMLEncoder extends XMLProcessor implements IXMLEncoder {

	protected XMLEncoder(XMLProcessor processor) {
		super(processor);
	}

	XMLEncoder(Document document) {
		super(document);
	}

	protected final void encodeChild(Element parent, XMLEncodable encodable) {
		if (!shouldEncode())
			return;
		IXMLEncoder encoder = encodable.getEncoder(this);
		String tagName = encodable.getTagName();
		Element child = createElement(tagName);
		encoder.encode(child);
		if (child != null)
			parent.appendChild(child);
	}

	@Override
	public boolean shouldEncode() {
		return true;
	}

}
