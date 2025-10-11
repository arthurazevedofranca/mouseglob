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
import dcc.mouseglob.tracking.TrackingManager.TrackingMode;
import dcc.xml.XMLEncoder;
import dcc.xml.XMLProcessor;

class TrackingXMLEncoder extends XMLEncoder {

	private final TrackingManager trackingManager;
	private final TrackingController trackingController;

	TrackingXMLEncoder(XMLProcessor processor, TrackingManager trackingManager,
			TrackingController trackingController) {
		super(processor);
		this.trackingManager = trackingManager;
		this.trackingController = trackingController;
	}

	@Override
	public void encode(Element trackingElement) {
		Element thresholdElement = createSimpleElement("threshold",
				String.valueOf(trackingManager.getThreshold()));
		trackingElement.appendChild(thresholdElement);

		Element trackerSizeElement = createSimpleElement("trackersize",
				String.valueOf(trackingManager.getTrackerSize()));
		trackingElement.appendChild(trackerSizeElement);

		Element trackingModeElement = createElement("trackingmode");
		TrackingMode trackingMode = trackingManager.getTrackingMode();
		trackingModeElement.setTextContent(trackingMode.toString());
		if (trackingMode == TrackingMode.DIFFERENCE) {
			String backgroundFileName = trackingController
					.getBackgroundFileName();
			Element backgroundElement = createSimpleElement("background",
					backgroundFileName);

			try {
				GrayscaleImageIO.saveImage(trackingManager.getBackground(),
						backgroundFileName);
				trackingElement.appendChild(backgroundElement);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		trackingElement.appendChild(trackingModeElement);

		Element thresholdModeElement = createElement("thresholdmode");
		thresholdModeElement.setTextContent(trackingManager.getThresholdMode()
				.toString());
		trackingElement.appendChild(thresholdModeElement);

		if (!trackingManager.isEmpty())
			trackingElement.appendChild(buildTrackersElement(trackingManager));
	}

	private Element buildTrackersElement(TrackingManager trackingManager) {
		Element trackersElement = createElement("trackers");
		TrackerXMLCodec trackerCodec = new TrackerXMLCodec(this);
		for (Tracker t : trackingManager.getTrackers())
			trackersElement.appendChild(trackerCodec.encode(t));
		return trackersElement;
	}

}
