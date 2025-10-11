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

import dcc.xml.XMLProcessor.IXMLDecoder;

public abstract class XMLDecoder extends XMLProcessor implements IXMLDecoder {

	protected XMLDecoder() {
		super((Document) null);
	}

	protected final void decodeChild(Element parent, XMLEncodable encodable) {
		String tagName = encodable.getTagName();
		Element child = getChild(parent, tagName);
		IXMLDecoder decoder = encodable.getDecoder(this);
		decoder.decode(child);
	}

	protected final void decodeChild(Element parent, XMLEncodable encodable,
			boolean isOptional) {
		if (isOptional) {
			try {
				decodeChild(parent, encodable);
			} catch (XMLNotFoundException e) {
			}
		} else {
			decodeChild(parent, encodable);
		}

	}

}
