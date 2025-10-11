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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import dcc.graphics.math.Vector;
import dcc.mouseglob.MouseGlob;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.calibration.CalibrationModule;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.tracking.Tracker;
import dcc.mouseglob.tracking.TrackingManager;

/**
 * @author Daniel Coelho de Castro
 */
class TrajectoryWriter {
	static final DecimalFormat FORMAT = (DecimalFormat) DecimalFormat
			.getInstance();
	static {
		FORMAT.applyPattern("\"###.##\"");
	}
	private static final String SEPARATOR = ",";

	private PrintWriter writer;

	private long initialTime;
	private long previousTime;

	private TrackingManager trackingManager;
	private ZonesManager zonesManager;
	private Calibration calibration;

	/**
	 * Constructor for the <code>CoordinatesWriter</code> class.
	 * 
	 * @param filename
	 *            - the coordinates file name
	 * @throws FileNotFoundException
	 */
	TrajectoryWriter(String filename, TrackingManager trackingManager,
			ZonesManager zonesManager) throws FileNotFoundException {
		this.trackingManager = trackingManager;
		this.zonesManager = zonesManager;
		writer = new PrintWriter(filename);
	}

	/**
	 * Writes the header of the file, containing MouseGlob version and author,
	 * number of circular and polygonal zones, calibration scale and table
	 * header.
	 */
	void writeHeader(int width, int height) {
		previousTime = initialTime = -1;
		writer.println(MouseGlob.HEADER);

		writer.println("Size" + SEPARATOR + width + "x" + height);
		writer.println(zonesManager.getZoneCount() + SEPARATOR + "Zones");
		calibration = CalibrationModule.getInstance().getModel();
		double scale = calibration.getScale();
		writer.println("Scale" + SEPARATOR + FORMAT.format(scale) + SEPARATOR
				+ "cm/px");
		writeTableHeader();
	}

	/**
	 * Writes the table header
	 */
	void writeTableHeader() {
		writer.print("Time");

		for (Tracker tracker : trackingManager.getTrackers()) {
			writer.print(SEPARATOR + tracker.getName() + " x" + SEPARATOR
					+ tracker.getName() + " y");

			for (Zone zone : zonesManager.getZones())
				writer.print(SEPARATOR + zone.getName());
		}

		writer.println();
	}

	/**
	 * Writes a line to the file containing the tracking time, pressed keys,
	 * and, for each tracker, its position and the time of permanence inside
	 * each zone.
	 * 
	 * @param time
	 *            - the time which corresponds to this set of coordinates
	 */
	void writeLine(long time) {
		// First line
		if (initialTime == -1)
			previousTime = initialTime = time;

		StringBuilder line = new StringBuilder();
		line.append(time - initialTime);

		long dt = time - previousTime;

		for (Tracker t : trackingManager.getTrackers()) {
			Vector positionPx = t.getPosition();
			Vector positionCm = calibration.pxToCm(positionPx);
			line.append(SEPARATOR).append(FORMAT.format(positionCm.x));
			line.append(SEPARATOR).append(FORMAT.format(positionCm.y));

			for (Zone z : zonesManager.getZones())
				line.append(SEPARATOR).append(z.contains(positionPx) ? dt : 0);
		}

		line.append(SEPARATOR).append(
				MouseGlob.keyEvent.getModel().getClassesDescriptions());

		writer.println(line.toString());

		previousTime = time;
	}

	/**
	 * Flushes and closes the file.
	 */
	void close() {
		writer.flush();
		writer.close();
	}

}
