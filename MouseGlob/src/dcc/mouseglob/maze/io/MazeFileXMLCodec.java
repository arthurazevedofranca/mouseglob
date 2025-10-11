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
package dcc.mouseglob.maze.io;

import org.w3c.dom.Element;

import dcc.xml.XMLCodec;
import dcc.xml.XMLProcessor;

public class MazeFileXMLCodec extends XMLCodec {

	private final MazeIOManager manager;

	public MazeFileXMLCodec(XMLProcessor processor, MazeIOManager manager) {
		super(processor);
		this.manager = manager;
	}

	@Override
	public void encode(Element element) {
		String fileName = manager.getFileName();
		setAttribute(element, "filename", fileName);
	}

	@Override
	public void decode(Element mazeElement) {
		String mazeName = getAttribute(mazeElement, "filename");
		manager.load(mazeName);
	}

	@Override
	public boolean shouldEncode() {
		return !manager.isEmpty();
	}

}
