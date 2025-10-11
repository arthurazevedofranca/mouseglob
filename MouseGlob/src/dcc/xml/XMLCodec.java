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

import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;

public abstract class XMLCodec extends XMLProcessor implements IXMLEncoder,
		IXMLDecoder {

	protected XMLCodec(XMLProcessor processor) {
		super(processor);
	}

	@Override
	public boolean shouldEncode() {
		return true;
	}

}
