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
package dcc.mouseglob.movie;

import org.w3c.dom.Element;

import dcc.xml.XMLEncoder;
import dcc.xml.XMLProcessor;

class MovieXMLEncoder extends XMLEncoder {

	private final MovieManager movieManager;

	MovieXMLEncoder(XMLProcessor processor, MovieManager movieManager) {
		super(processor);
		this.movieManager = movieManager;
	}

	@Override
	public void encode(Element movieElement) {
		Element filenameElement = createSimpleElement("filename",
				movieManager.getMovieName());
		movieElement.appendChild(filenameElement);

		Element positionElement = createSimpleElement("position",
				String.valueOf(movieManager.getPosition()));
		movieElement.appendChild(positionElement);
	}

	@Override
	public boolean shouldEncode() {
		return movieManager.hasMovie();
	}

}
