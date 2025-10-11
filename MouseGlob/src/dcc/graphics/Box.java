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
package dcc.graphics;

import dcc.graphics.math.Vector;

public class Box {

	public final double top, bottom;
	public final double left, right;

	public final double width, height;

	private Box(Vector topLeft, Vector bottomRight) {
		this(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
	}

	private Box(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;

		width = right - left;
		height = bottom - top;
	}

	public boolean contains(double x, double y) {
		return x <= right && x >= left && y <= bottom && y >= top;
	}

	public boolean contains(Vector p) {
		return contains(p.x, p.y);
	}

	public Vector clamp(Vector p) {
		double x = Math.min(Math.max(p.x, left), right);
		double y = Math.min(Math.max(p.y, top), bottom);
		return new Vector(x, y);
	}

	public Box clamp(Box box) {
		return new Box(box.clamp(new Vector(left, top)), box.clamp(new Vector(
				right, bottom)));
	}

	public Box growToFit(Box box) {
		double l = Math.min(left, box.left);
		double r = Math.max(right, box.right);
		double t = Math.min(top, box.top);
		double b = Math.min(bottom, box.bottom);
		return new Box(l, t, r, b);
	}

	public boolean isSuperposed(Box box) {
		return contains(box.left, box.top) || contains(box.right, box.top)
				|| contains(box.left, box.bottom)
				|| contains(box.right, box.bottom);
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)x(%f, %f)", left, top, right, bottom);
	}

	public static Box fromCorners(double left, double top, double right,
			double bottom) {
		return new Box(left, top, right, bottom);
	}

	public static Box fromCorners(Vector topLeft, Vector bottomRight) {
		return new Box(topLeft, bottomRight);
	}

	public static Box fromCorner(double left, double top, double width,
			double height) {
		return new Box(left, top, left + width, top + height);
	}

	public static Box fromCorner(Vector topLeft, double width, double height) {
		return new Box(topLeft, topLeft.translate(width, height));
	}

	public static Box fromSize(double width, double height) {
		return new Box(0, 0, width, height);
	}

	public static Box fromRadius(Vector center, double radius) {
		Vector p1 = new Vector(center.x - radius, center.y - radius);
		Vector p2 = new Vector(center.x + radius, center.y + radius);
		return new Box(p1, p2);
	}

	public static Box fromRadius(double centerX, double centerY, double radius) {
		return new Box(centerX - radius, centerY - radius, centerX + radius,
				centerY + radius);
	}

	public Box.Int toInt() {
		return new Int((int) top, (int) bottom, (int) left, (int) right);
	}

	public class Int {

		public final int top, bottom;
		public final int left, right;

		public final int width, height;

		private Int(int top, int bottom, int left, int right) {
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;

			width = right - left;
			height = bottom - top;
		}

	}

}
