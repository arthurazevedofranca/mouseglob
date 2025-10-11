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

import dcc.graphics.math.Matrix.SingularMatrixException;

public class CoordinateSystem {

	private final Vector origin;
	private final Vector e1, e2;

	public CoordinateSystem(Vector origin, Vector e1, Vector e2) {
		this.origin = origin;
		this.e1 = e1;
		this.e2 = e2;
	}

	public Vector getOrigin() {
		return origin;
	}

	public Vector getE1() {
		return e1;
	}

	public Vector getE2() {
		return e2;
	}

	public CoordinateSystem translate(Vector translation) {
		return new CoordinateSystem(origin.add(translation), e1, e2);
	}

	public CoordinateSystem rotate(double angle) {
		return new CoordinateSystem(origin, e1.rotate(angle), e2.rotate(angle));
	}

	public CoordinateSystem rotate180() {
		return new CoordinateSystem(origin, e1.reflect(), e2.reflect());
	}

	public CoordinateSystem reflectE1() {
		return new CoordinateSystem(origin, e1.reflect(), e2);
	}

	public CoordinateSystem reflectE2() {
		return new CoordinateSystem(origin, e1, e2.reflect());
	}

	public Vector get(double u, double v) {
		Matrix m = Matrix.columns(e1, e2);
		return m.multiply(new Vector(u, v)).add(origin);
	}

	public Vector getInverse(double x, double y) throws SingularMatrixException {
		Matrix m = Matrix.columns(e1, e2).inverse();
		return m.multiply(new Vector(x, y).subtract(origin));
	}

	public CoordinateSystem scale(double scale) {
		return new CoordinateSystem(origin.multiply(scale), e1.multiply(scale),
				e2.multiply(scale));
	}

}
