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
package dcc.mouseglob.trajectory;

import org.w3c.dom.Element;

import dcc.mouseglob.FileType;
import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLProcessor;

public class TrajectoriesXMLCodec extends AtomicXMLCodec<String> {

	private final TrajectoriesIOManager manager;

	public TrajectoriesXMLCodec(XMLProcessor processor,
			TrajectoriesIOManager manager) {
		super(processor);
		this.manager = manager;
	}

	@Override
	public Element encode(String experimentName) {
		String trajectoriesName = manager.getTrajectoriesFileName();
		if (trajectoriesName == null) {
			trajectoriesName = FileType.TRAJECTORIES_FILE
					.replaceExtension(experimentName);
			manager.setTrajectoriesFileName(trajectoriesName);
		}
		Element trajectoriesElement = createElement("trajectories");
		setAttribute(trajectoriesElement, "filename", trajectoriesName);

		return trajectoriesElement;
	}

	@Override
	public String decode(Element trajectoriesElement) {
		String trajectoriesName = getAttribute(trajectoriesElement, "filename");
		manager.setTrajectoriesFileName(trajectoriesName);
		return trajectoriesName;
	}

}
