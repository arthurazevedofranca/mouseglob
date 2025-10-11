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
package dcc.mouseglob.tracking;

import java.io.IOException;

import org.w3c.dom.Element;

import dcc.graphics.image.GrayscaleImageIO;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.xml.XMLDecoder;
import dcc.xml.XMLNotFoundException;
import dcc.xml.XMLParseException;

class TrackingXMLDecoder extends XMLDecoder {

	private final TrackingManager trackingManager;
	private final TrackingController trackingController;
	private final BoundariesManager boundariesManager;

	TrackingXMLDecoder(TrackingManager trackingManager,
			TrackingController trackingController,
			BoundariesManager boundariesManager) {
		this.trackingManager = trackingManager;
		this.trackingController = trackingController;
		this.boundariesManager = boundariesManager;
	}

	@Override
	public void decode(Element trackingElement) {
		try {
			int trackerSize = getIntChild(trackingElement, "trackersize");
			int threshold = getIntChild(trackingElement, "threshold");
			String thresholdMode = getStringChild(trackingElement,
					"thresholdmode");
			String trackingMode = getStringChild(trackingElement,
					"trackingmode");

			trackingController.setTrackerSize(trackerSize);
			trackingController.setThreshold(threshold);
			trackingController.setThresholdMode(thresholdMode);
			trackingController.setTrackingMode(trackingMode);

			try {
				String backgroundFileName = getStringChild(trackingElement,
						"background");
				trackingManager.setBackground(GrayscaleImageIO
						.readImage(backgroundFileName));
			} catch (XMLNotFoundException | IOException e) {
			}

			try {
				parseTrackers(getChild(trackingElement, "trackers"),
						trackingManager, boundariesManager);
			} catch (XMLNotFoundException e) {
			}
		} catch (XMLNotFoundException | XMLParseException e) {
			e.printStackTrace();
		}
	}

	private void parseTrackers(Element trackersElement,
			TrackingManager trackingManager, BoundariesManager boundariesManager) {
		TrackerXMLCodec trackerCodec = new TrackerXMLCodec(this);
		for (Element trackerElement : getChildren(trackersElement))
			trackingManager.add(trackerCodec.decode(trackerElement,
					boundariesManager));
	}

}
