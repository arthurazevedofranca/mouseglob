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

import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.Boundary;
import dcc.mouseglob.maze.BoundaryXMLCodec;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.maze.ZoneXMLCodec;
import dcc.mouseglob.maze.ZonesManager;
import dcc.xml.XMLBuilder;

/**
 * @author Daniel Coelho de Castro
 */
class MazeXMLBuilder extends XMLBuilder {

	private final ZonesManager zonesManager;
	private final BoundariesManager boundariesManager;

	MazeXMLBuilder(ZonesManager zonesManager,
			BoundariesManager boundariesManager) {
		this.zonesManager = zonesManager;
		this.boundariesManager = boundariesManager;
	}

	@Override
	public void encode(Element root) {
		Element zonesElement = createElement("zones");
		ZoneXMLCodec zoneCodec = new ZoneXMLCodec(this);
		for (Zone zone : zonesManager.getZones())
			zonesElement.appendChild(zoneCodec.encode(zone));

		Element boundariesElement = createElement("boundaries");
		BoundaryXMLCodec boundaryCodec = new BoundaryXMLCodec(this);
		for (Boundary boundary : boundariesManager.getBoundaries())
			boundariesElement.appendChild(boundaryCodec.encode(boundary));

		root.appendChild(zonesElement);
		root.appendChild(boundariesElement);
	}

	@Override
	protected String getRootTagName() {
		return "Maze";
	}

}
