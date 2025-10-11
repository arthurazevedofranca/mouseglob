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
package dcc.mouseglob.shape;

import java.util.ArrayList;
import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.inspector.PropertyCollectionInspector;
import dcc.mouseglob.labelable.Point;

/**
 * Class that represents a polygonal zone.
 * 
 * @author Daniel Coelho de Castro
 */
public class Polygon extends Shape {
	/**
	 * Number of vertices.
	 */
	private int vertexCount;
	/**
	 * Coordinates of each vertex of this polygon, in order.
	 */
	private List<Point> vertices;

	/**
	 * Constructor for the <code>Polygon</code> class.
	 */
	public Polygon() {
		vertexCount = 0;
		vertices = new ArrayList<Point>();
	}

	/**
	 * Adds a new vertex to this polygon.
	 * 
	 * @param x
	 *            - horizontal coordinate of the vertex
	 * @param y
	 *            - vertical coordinate of the vertex
	 */
	public void addVertex(double x, double y) {
		vertices.add(new Point(x, y));
		vertexCount++;
	}

	/**
	 * Adds a new vertex to this polygon.
	 * 
	 * @param p
	 *            - vertex
	 */
	public void addVertex(Point p) {
		addVertex(p.x, p.y);
	}

	@Override
	boolean exists() {
		return vertexCount != 0;
	}

	@Override
	public void paint(PGraphics g) {
		g.beginShape();
		for (Point vertex : vertices)
			g.vertex((float) vertex.x, (float) vertex.y);
		g.endShape(PConstants.CLOSE);
	}

	@Override
	public Vector getLabelPosition() {
		if (!exists())
			return null;

		Point point = null;
		double min = Double.MAX_VALUE;

		for (Point vertex : vertices) {
			double d = vertex.x + vertex.y;
			if (d < min) {
				min = d;
				point = vertex;
			}
		}

		return point.toVector();
	}

	@Override
	public boolean contains(double x, double y) {
		int j = vertexCount - 1;
		boolean oddNodes = false;
		for (int i = 0; i < vertexCount; i++) {
			Point v1 = vertices.get(i);
			Point v2 = vertices.get(j);
			if (v1.y < y && v2.y >= y || v2.y < y && v1.y >= y)
				if (v1.x + (y - v1.y) / (v2.y - v1.y) * (v2.x - v1.x) < x)
					oddNodes = !oddNodes;
			j = i;
		}
		return oddNodes;
	}

	/**
	 * Gets the vertex with the given index.
	 * 
	 * @param i
	 *            the index
	 * @return the vertex
	 */
	public Point getVertex(int i) {
		return vertices.get(i);
	}

	/**
	 * Gets this <code>Polygon</code>'s vertices.
	 * 
	 * @return the vertex list
	 */
	public List<Point> getVertices() {
		return vertices;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public String getCoordinates() {
		StringBuilder coords = new StringBuilder("polygon\t").append(
				vertexCount).append("\tvertices\r\n");
		for (int i = 0; i < vertexCount; i++) {
			Point vertex = vertices.get(i);
			coords.append(vertex.x).append("\t").append(vertex.y);
			if (i < vertexCount - 1)
				coords.append("\r\n");
		}
		return coords.toString();
	}

	@Override
	Point getSelectedPoint(int mouseX, int mouseY) {
		for (Point vertex : vertices) {
			if (vertex.isOver(mouseX, mouseY)) {
				vertex.setSelected(true);
				vertex.setName(vertex.x + ", " + vertex.y);
				return vertex;
			}
		}

		return null;
	}

	@Override
	public Inspector makeInspector() {
		return new PolygonInspector();
	}

	private class PolygonInspector extends Inspector {
		protected PolygonInspector() {
			super("Polygon");

			add(new PropertyCollectionInspector<List<Point>, Point>("Vertices") {
				@Override
				protected List<Point> getValue() {
					return vertices;
				}
			});
		}
	}
}
