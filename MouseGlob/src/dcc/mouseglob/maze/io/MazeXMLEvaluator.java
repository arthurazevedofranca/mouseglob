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

import dcc.mouseglob.maze.BoundariesController;
import dcc.mouseglob.maze.BoundaryXMLCodec;
import dcc.mouseglob.maze.ZoneXMLCodec;
import dcc.mouseglob.maze.ZonesController;
import dcc.xml.XMLDecoder;

/**
 * @author Daniel Coelho de Castro
 */
class MazeXMLEvaluator extends XMLDecoder {

	private ZonesController zonesController;
	private BoundariesController boundariesController;

	MazeXMLEvaluator(ZonesController zonesController,
			BoundariesController boundariesController) {
		this.zonesController = zonesController;
		this.boundariesController = boundariesController;
	}

	@Override
	public void decode(Element root) {
		Element zonesElement = getChild(root, "zones");
		ZoneXMLCodec zoneCodec = new ZoneXMLCodec(this);
		for (Element zoneElement : getChildren(zonesElement))
			zonesController.add(zoneCodec.decode(zoneElement));

		Element boundariesElement = getChild(root, "boundaries");
		BoundaryXMLCodec boundaryCodec = new BoundaryXMLCodec(this);
		for (Element boundaryElement : getChildren(boundariesElement))
			boundariesController.add(boundaryCodec.decode(boundaryElement));
	}

}
