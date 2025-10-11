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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLBuilder extends XMLEncoder {

	protected XMLBuilder() {
		super(getNewDocument());
	}

	final Document build() {
		String tagName = getRootTagName();
		Element root = createElement(tagName);
		encode(root);
		Document document = getDocument();
		document.appendChild(root);
		return document;
	}

	protected abstract String getRootTagName();

	private static Document getNewDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

}
