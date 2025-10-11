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
package dcc.graphics.math;

public class Vector {

	public static final Vector ZERO = new Vector(0, 0);
	public static final Vector I = new Vector(1, 0);
	public static final Vector J = new Vector(0, 1);
	public static final Vector[] CANONICAL_BASIS = { I, J };

	public final double x;
	public final double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double magnitude() {
		return Math.hypot(x, y);
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}

	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

	public Vector translate(double tx, double ty) {
		return new Vector(x + tx, y + ty);
	}

	public Vector multiply(double s) {
		return new Vector(s * x, s * y);
	}

	public Vector multiply(Matrix m) {
		return new Vector(m.m11 * x + m.m21 * x, m.m12 * y + m.m22 * y);
	}

	public double dot(Vector v) {
		return x * v.x + y * v.y;
	}

	public double cross(Vector v) {
		return x * v.y - y * v.x;
	}

	public Matrix outer(Vector v) {
		return new Matrix(x * v.x, x * v.y, y * v.x, y * v.y);
	}

	public double angle(Vector v) {
		double p = cross(v);
		double sin = p / (magnitude() * v.magnitude());
		return Math.asin(sin);
	}

	public Vector normalize() {
		double mag = magnitude();
		return new Vector(x / mag, y / mag);
	}

	public Vector rotate(double a) {
		double cos = Math.cos(a);
		double sin = Math.sin(a);
		return new Vector(x * cos - y * sin, x * sin + y * cos);
	}

	public Vector rotate90CCW() {
		return new Vector(-y, x);
	}

	public Vector rotate90CW() {
		return new Vector(y, -x);
	}

	public Vector project(Vector target) {
		return target.multiply(dot(target) / target.magnitude());
	}

	public Vector reflect() {
		return new Vector(-x, -y);
	}

	public Vector reflect(Vector axis) {
		Vector p = this.project(axis);
		return add(subtract(p).multiply(2));
	}

	public Vector lerp(Vector v, double k) {
		return new Vector((1 - k) * x + k * v.x, (1 - k) * y + k * v.y);
	}

	public double distance(Vector v) {
		return Math.hypot(x - v.x, y - v.y);
	}

	public double squaredDistance(Vector v) {
		double dx = x - v.x, dy = y - v.y;
		return dx * dx + dy * dy;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector) {
			Vector v = (Vector) o;
			return v.x == x && v.y == y;
		}
		return false;
	}

	public Vector.Float toFloat() {
		return new Float();
	}

	public class Float {

		public final float x, y;

		private Float() {
			this.x = (float) Vector.this.x;
			this.y = (float) Vector.this.y;
		}

	}

}
