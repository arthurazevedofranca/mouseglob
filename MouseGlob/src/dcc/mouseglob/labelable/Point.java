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
package dcc.mouseglob.labelable;

import processing.core.PGraphics;
import dcc.graphics.math.Vector;

/**
 * @author Daniel Coelho de Castro
 */
public class Point extends LabelableObject {
	/**
	 * Horizontal coordinate.
	 */
	public double x;
	/**
	 * Vertical coordinate.
	 */
	public double y;

	/**
	 * Constructor for the <code>Point</code> class.
	 * 
	 * @param x
	 *            horizontal coordinate of the point
	 * @param y
	 *            vertical coordinate of the point
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Simple constructor for the <code>Point</code> class.
	 */
	public Point(Point p) {
		this(p.x, p.y);
	}

	public Point(Vector v) {
		this(v.x, v.y);
	}

	/**
	 * Simple constructor for the <code>Point</code> class.
	 */
	public Point() {
		this(0, 0);
	}

	@Override
	protected void doPaint(PGraphics g) {
		g.ellipse((float) x, (float) y, 6, 6);
		if (isSelected())
			paintLabel(g);
	}

	@Override
	protected Style getStyle() {
		return isSelected() ? Style.SELECTED_POINT : Style.POINT;
	}

	/**
	 * Determines whether the given point is close enough to this
	 * <code>Point</code>. Typically used with mouse cursor coordinates.
	 * 
	 * @param x
	 *            - horizontal coordinate
	 * @param y
	 *            - vertical coordinate
	 * @return <code>true</code> if it is closer than 5px, <code>false</code>
	 *         otherwise
	 */
	public boolean isOver(double x, double y) {
		return distance(x, y) < 5;
	}

	@Override
	protected Vector getLabelPosition() {
		return toVector();
	}

	/**
	 * Performs a coordinate-wise addition of two points.
	 * 
	 * @param p
	 *            - the <code>Point</code> to add to this one
	 * @return the new resulting <code>Point</code>
	 */
	public Point add(Point p) {
		return new Point(x + p.x, y + p.y);
	}

	/**
	 * Performs a coordinate-wise subtraction of two points.
	 * 
	 * @param p
	 *            - the <code>Point</code> to subtract from this one
	 * @return the new resulting <code>Point</code>
	 */
	public Point subtract(Point p) {
		return new Point(x - p.x, y - p.y);
	}

	public Point translate(double tx, double ty) {
		return new Point(x + tx, y + ty);
	}

	/**
	 * Scales this <code>Point</code>'s coordinates by a certain factor.
	 * 
	 * @param s
	 *            - the factor by which to scale the coordinates
	 * @return the new resulting <code>Point</code>
	 */
	public Point scale(double s) {
		return new Point(x * s, y * s);
	}

	public double norm() {
		return Math.hypot(x, y);
	}

	public Point normalize() {
		double norm = norm();
		if (norm == 0)
			return new Point(0, 0);
		return scale(1 / norm);
	}

	/**
	 * Rotates this <code>Point</code> a certain angle around the origin (0, 0).
	 * 
	 * @param a
	 *            - the angle to rotate this <code>Point</code>
	 * @return the new resulting <code>Point</code>
	 */
	public Point rotate(double a) {
		double cos = Math.cos(a);
		double sin = Math.sin(a);
		return new Point(x * cos - y * sin, x * sin + y * cos);
	}

	/**
	 * Rotates this <code>Point</code> 90 degrees counter-clockwise.
	 * <p>
	 * This is a convenient straightforward implementation, and is more
	 * efficient than calling <code>rotate(90)</code>.
	 * 
	 * @return the new resulting <code>Point</code>
	 */
	public Point rotate90CCW() {
		return new Point(-y, x);
	}

	/**
	 * Rotates this <code>Point</code> 90 degrees clockwise.
	 * <p>
	 * This is a convenient straightforward implementation, and is more
	 * efficient than calling <code>rotate(-90)</code>.
	 * 
	 * @return the new resulting <code>Point</code>
	 */
	public Point rotate90CW() {
		return new Point(y, -x);
	}

	/**
	 * Performs the dot-product between two points.
	 * 
	 * @param p
	 *            - the <code>Point</code> with which to perform the dot-product
	 * @return the new result of the dot-product
	 */
	public double dot(Point p) {
		return x * p.x + y * p.y;
	}

	/**
	 * Performs the cross-product between two points.
	 * 
	 * @param p
	 *            - the <code>Point</code> with which to perform the
	 *            cross-product
	 * @return the new result of the cross-product
	 */
	public double cross(Point p) {
		return x * p.y - y * p.x;
	}

	public double distance(Point p) {
		return distance(p.x, p.y);
	}

	public double distance(double x, double y) {
		return Math.hypot(x - this.x, y - this.y);
	}

	public double squaredDistance(Point p) {
		return squaredDistance(p.x, p.y);
	}

	public double squaredDistance(double x, double y) {
		double dx = x - this.x;
		double dy = y - this.y;
		return dx * dx + dy * dy;
	}

	public Point lerp(Point p, double k) {
		return new Point(k * x + (1 - k) * p.x, k * y + (1 - k) * p.y);
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}

	public Vector toVector() {
		return new Vector(x, y);
	}
}
