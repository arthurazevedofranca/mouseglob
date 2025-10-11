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

public class Matrix {

	public static final Matrix IDENTITY = new Matrix(1, 0, 0, 1);

	private static final double THRESHOLD = 1e-11;
	public final double m11, m12, m21, m22;

	public Matrix(double m11, double m12, double m21, double m22) {
		this.m11 = m11;
		this.m12 = m12;
		this.m21 = m21;
		this.m22 = m22;
	}

	public double determinant() {
		return m11 * m22 - m12 * m21;
	}

	public double trace() {
		return m11 + m22;
	}

	public Matrix transpose() {
		return new Matrix(m11, m21, m12, m22);
	}

	public Matrix add(Matrix m) {
		return new Matrix(m11 + m.m11, m12 + m.m12, m21 + m.m21, m22 + m.m22);
	}

	public Matrix subtract(Matrix m) {
		return new Matrix(m11 - m.m11, m12 - m.m12, m21 - m.m21, m22 - m.m22);
	}

	public Matrix multiply(Matrix m) {
		double p11 = m11 * m.m11 + m12 * m.m21;
		double p12 = m11 * m.m12 + m12 * m.m22;
		double p21 = m21 * m.m11 + m22 * m.m21;
		double p22 = m21 * m.m12 + m22 * m.m22;
		return new Matrix(p11, p12, p21, p22);
	}

	public Vector multiply(Vector v) {
		return new Vector(m11 * v.x + m12 * v.y, m21 * v.x + m22 * v.y);
	}

	public Matrix multiply(double s) {
		return new Matrix(m11 * s, m12 * s, m21 * s, m22 * s);
	}

	public Vector solve(Vector b) throws IndeterminateSystemException {
		double det = determinant();
		if (isZero(b.x) && isZero(b.y)) { // Homogeneous system
			if (isZero(det))
				return new Vector(1, -m11 / m12).normalize();
			return Vector.ZERO; // Trivial solution
		}

		if (isZero(det))
			throw new IndeterminateSystemException();

		double x = (m22 * b.x - m12 * b.y) / det;
		double y = (m11 * b.y - m21 * b.x) / det;

		return new Vector(x, y);
	}

	public double[] eigenvalues() {
		double k = Math.sqrt((m11 - m22) * (m11 - m22) + 4 * m12 * m21);
		double lambda1 = (m11 + m22 + k) / 2;
		double lambda2 = (m11 + m22 - k) / 2;
		return new double[] { lambda1, lambda2 };
	}

	public Eigendecomposition eigendecomposition() {
		double[] lambdas = eigenvalues();
		Vector[] eigenvectors = new Vector[2];
		if (isZero(lambdas[0] - lambdas[1]))
			// If double eigenvalue, assume canonical basis
			eigenvectors = Vector.CANONICAL_BASIS;
		else {
			for (int i = 0; i < 2; i++) {
				try {
					Matrix m = subtract(diagonal(lambdas[i]));
					eigenvectors[i] = m.solve(Vector.ZERO);
				} catch (IndeterminateSystemException e) {
					e.printStackTrace();
				}
			}
		}
		return new Eigendecomposition(eigenvectors[0], eigenvectors[1],
				lambdas[0], lambdas[1]);
	}

	public Matrix inverse() throws SingularMatrixException {
		double det = determinant();
		if (isZero(det))
			throw new SingularMatrixException();
		return new Matrix(m22 / det, -m12 / det, -m21 / det, m11 / det);
	}

	@Override
	public String toString() {
		return "((" + m11 + ", " + m12 + "), (" + m21 + ", " + m22 + "))";
	}

	public static Matrix rows(Vector row1, Vector row2) {
		return new Matrix(row1.x, row1.y, row2.x, row2.y);
	}

	public static Matrix columns(Vector col1, Vector col2) {
		return new Matrix(col1.x, col2.x, col1.y, col2.y);
	}

	public static Matrix diagonal(double d1, double d2) {
		return new Matrix(d1, 0, 0, d2);
	}

	public static Matrix diagonal(double d) {
		return new Matrix(d, 0, 0, d);
	}

	public static Matrix symmetric(double d1, double d2, double off) {
		return new Matrix(d1, off, off, d2);
	}

	private static boolean isZero(double x) {
		return Math.abs(x) < THRESHOLD;
	}

	public static class Eigendecomposition {

		public final Vector e1, e2;
		public final double lambda1, lambda2;

		private Eigendecomposition(Vector e1, Vector e2, double lambda1,
				double lambda2) {
			this.e1 = e1;
			this.e2 = e2;
			this.lambda1 = lambda1;
			this.lambda2 = lambda2;
		}

	}

	@SuppressWarnings("serial")
	public static class IndeterminateSystemException extends Exception {
	}

	@SuppressWarnings("serial")
	public static class SingularMatrixException extends Exception {
	}

}
