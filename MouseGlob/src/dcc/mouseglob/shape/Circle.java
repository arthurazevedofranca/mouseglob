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

import processing.core.PGraphics;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.Inspectable;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.labelable.Point;

/**
 * Class that represents a circular zone.
 * 
 * @author Daniel Coelho de Castro
 */
public class Circle extends Shape {
	@Inspectable("Center")
	private Point center;
	private Point anchor;
	@Inspectable(value = "Radius", format = "%.1f")
	private double radius;

	/**
	 * Constructor for the <code>Circle</code> class.
	 */
	Circle() {
		this(new Point(), 0);
	}

	/**
	 * Constructor for the <code>Circle</code> class.
	 * 
	 * @param x
	 *            horizontal coordinate of the center
	 * @param y
	 *            vertical coordinate of the center
	 * @param R
	 *            radius
	 */
	public Circle(double x, double y, double R) {
		this(new Point(x, y), R);
	}

	/**
	 * Constructor for the <code>Circle</code> class.
	 * 
	 * @param c
	 *            center point
	 * @param R
	 *            radius
	 */
	public Circle(Point c, double R) {
		center = c;
		anchor = new Point();
		this.radius = R;
	}

	/**
	 * Sets the anchor point for this circle.
	 * 
	 * @param x
	 *            horizontal coordinate of the anchor
	 * @param y
	 *            vertical coordinate of the anchor
	 */
	void setAnchor(double x, double y) {
		anchor.x = x;
		anchor.y = y;
	}

	/**
	 * Calculates the center of this circle and the radius, based on an already
	 * defined anchor point and the given opposite point.
	 * 
	 * @param x
	 *            horizontal coordinate of the point
	 * @param y
	 *            vertical coordinate of the point
	 */
	void findCenter(double x, double y) {
		center.x = (anchor.x + x) / 2;
		center.y = (anchor.y + y) / 2;
		radius = anchor.distance(x, y) / 2;
	}

	/**
	 * Gets the center point of this circle.
	 * 
	 * @return the center point
	 */
	Point getCenter() {
		return center;
	}

	/**
	 * Calculates the radius, given a point of this circle.
	 * 
	 * @param p
	 *            the point
	 */
	void setRadius(Point p) {
		radius = center.distance(p);
	}

	/**
	 * Gets the radius this circle.
	 * 
	 * @return the radius
	 */
	double getRadius() {
		return radius;
	}

	/**
	 * Gets a polygonal approximation of the circle.
	 * 
	 * @param vertices
	 *            - the number of vertices of the polygon
	 * @return a polygon that approximates the circle
	 */
	public Polygon getPolygon(int vertices) {
		Polygon polygon = new Polygon();

		double da = 2.0 * Math.PI / vertices;
		for (int i = 0; i < vertices; i++) {
			double a = i * da;
			double x = center.x + radius * Math.cos(a);
			double y = center.y + radius * Math.sin(a);
			polygon.addVertex(x, y);
		}

		return polygon;
	}

	@Override
	public boolean exists() {
		return radius != 0;
	}

	@Override
	public void paint(PGraphics g) {
		g.ellipse((float) center.x, (float) center.y, 2 * (float) radius,
				2 * (float) radius);
	}

	@Override
	public Vector getLabelPosition() {
		return new Vector(center.x - 0.71 * radius, center.y - 0.71 * radius);
	}

	@Override
	public boolean contains(double x, double y) {
		return center.distance(x, y) <= radius;
	}

	@Override
	public String getCoordinates() {
		return "circle\t" + center.x + "\t" + center.y + "\t" + radius;
	}

	@Override
	Point getSelectedPoint(int mouseX, int mouseY) {
		if (center.isOver(mouseX, mouseY)) {
			center.setSelected(true);
			center.setName(center.x + ", " + center.y);
			return center;
		}

		Point m = new Point(mouseX, mouseY);
		double d = center.distance(m);
		if (Math.abs(d - radius) < 5) {
			// Clamp mouse position to the circumference
			Point p = m.subtract(center).scale(radius / d).add(center);
			p.setSelected(true);
			p.setName(getRadiusPointName());
			return p;
		}

		return null;
	}

	String getRadiusPointName() {
		return String.format("radius = %.2f", radius);
	}

	@Override
	public Inspector makeInspector() {
		return new Inspector("Circle", this);
	}

}
