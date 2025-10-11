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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dcc.mouseglob.MouseGlob;
import dcc.mouseglob.labelable.Point;
import dcc.mouseglob.maze.Region;
import dcc.mouseglob.shape.Circle;
import dcc.mouseglob.shape.Polygon;
import dcc.mouseglob.shape.Shape;

class MazeCSVWriter {
	private static final int NUM_VERTICES_CIRCLE = 50;
	private static final DecimalFormat FORMAT = (DecimalFormat) DecimalFormat
			.getInstance();
	static {
		FORMAT.applyPattern("\"###.###\"");
	}

	private PrintWriter writer;
	private List<Region> zones;
	private List<Region> boundaries;
	private List<Region> regions;
	private double scale = 0;

	/**
	 * Constructor for the <code>MazeCSVWriter</code> class.
	 * 
	 * @param filename
	 *            - the coordinates file name
	 * @throws FileNotFoundException
	 *             if the file could not be created or written to
	 */
	MazeCSVWriter(String filename) throws FileNotFoundException {
		writer = new PrintWriter(filename);
	}

	void setZones(List<? extends Region> zones) {
		this.zones = new ArrayList<Region>(zones);
	}

	void setBoundaries(List<? extends Region> boundaries) {
		this.boundaries = new ArrayList<Region>(boundaries);
	}

	void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Writes the header of the file, containing MouseGlob version and author,
	 * calibration scale and table header.
	 */
	void writeHeader(int width, int height) {
		writer.println(MouseGlob.HEADER);
		writer.println("Size," + width + "x" + height);
		if (scale != 0)
			writer.println("Scale," + FORMAT.format(scale) + ",cm/px");

		for (Region region : zones)
			writer.print("Zone," + region.getName() + ",");
		for (Region region : boundaries)
			writer.print("Boundary," + region.getName() + ",");
		writer.println();
		regions = new ArrayList<Region>();
		regions.addAll(zones);
		regions.addAll(boundaries);
		for (int i = 0; i < regions.size(); i++)
			writer.print("x,y,");
		writer.println();
	}

	void writeFile() {
		List<Polygon> polygons = new ArrayList<>();
		for (Region region : regions) {
			Shape s = region.getShape();
			if (s instanceof Circle)
				polygons.add(((Circle) s).getPolygon(NUM_VERTICES_CIRCLE));
			else
				polygons.add((Polygon) s);
		}

		int maxVertices = 0;
		for (Polygon polygon : polygons) {
			int numVertices = polygon.getVertexCount();
			if (numVertices > maxVertices)
				maxVertices = numVertices;
		}

		for (int i = 0; i <= maxVertices; i++) {
			StringBuilder sb = new StringBuilder();
			for (Polygon p : polygons) {
				int numVertices = p.getVertexCount();
				if (i < numVertices)
					appendPoint(p.getVertex(i), sb);
				else if (i == numVertices)
					appendPoint(p.getVertex(0), sb); // Close the polygon
				else
					sb.append(",,"); // Empty columns
			}
			writer.print(sb.toString());
			writer.println();
		}
	}

	private void appendPoint(Point p, StringBuilder sb) {
		if (scale != 0) {
			sb.append(FORMAT.format(p.x * scale)).append(',');
			sb.append(FORMAT.format(p.y * scale)).append(',');
		} else {
			sb.append(FORMAT.format(p.x)).append(',');
			sb.append(FORMAT.format(p.y)).append(',');
		}
	}

	void close() {
		writer.flush();
		writer.close();
	}
}
