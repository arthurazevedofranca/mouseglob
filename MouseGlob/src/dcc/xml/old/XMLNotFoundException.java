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
package dcc.xml.old;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
public class XMLNotFoundException extends DOMException {
	/**
	 * Constructor for the <code>XMLNotFoundException</code> class.
	 * 
	 * @param message
	 *            - the detail message
	 */
	public XMLNotFoundException(String message) {
		super(NOT_FOUND_ERR, message);
	}

	/**
	 * Constructor for the <code>XMLNotFoundException</code> class.
	 * 
	 * @param element
	 *            - the element whose child's retrieval failed
	 * @param name
	 *            - the name of the child to be retrieved
	 */
	public XMLNotFoundException(Element element, String name) {
		super(NOT_FOUND_ERR, "Unable to find \"" + element.getTagName()
				+ "\"\'s child \"" + name + "\"");
	}
}
