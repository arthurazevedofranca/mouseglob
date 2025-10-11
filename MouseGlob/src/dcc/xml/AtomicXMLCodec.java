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

import org.w3c.dom.Element;

public abstract class AtomicXMLCodec<T> extends XMLProcessor {

	protected AtomicXMLCodec(XMLProcessor processor) {
		super(processor);
	}

	public abstract Element encode(T object);

	public abstract T decode(Element element);

}
