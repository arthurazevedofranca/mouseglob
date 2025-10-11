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

import dcc.mouseglob.ui.StatusUI;
import dcc.xml.XMLDecoder;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;

class MovieXMLDecoder extends XMLDecoder {

	private final MovieController movieController;

	MovieXMLDecoder(MovieController movieController) {
		this.movieController = movieController;
	}

	@Override
	public void decode(Element element) {
		try {
			String movieName = getStringChild(element, "filename");
			float moviePosition = 0;
			try {
				moviePosition = getFloatChild(element, "position");
			} catch (XMLParseException e) {
				e.printStackTrace();
			}
			try {
				movieController.loadMovieFile(movieName, moviePosition);
			} catch (Exception e) {
				e.printStackTrace();
				StatusUI.setStatusText("Error while loading movie.");
			}
		} catch (XMLNotFoundException e) {
			e.printStackTrace();
		}
	}

}
