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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PGraphics;

public class Snake {
	private List<Vector> points;
	private int nPoints;

	public Snake() {
		points = new ArrayList<Vector>();
		nPoints = 0;
	}

	public Snake(int n) {
		this();
		for (int i = 0; i < n; i++)
			add(Vector.ZERO);
	}

	@Override
	public Snake clone() {
		Snake s = new Snake();
		for (Vector p : points)
			s.add(new Vector(p.x, p.y));
		return s;
	}

	public void clear() {
		points.clear();
		nPoints = 0;
	}

	public void stretch(Vector head, Vector tail) {
		for (int i = 0; i < nPoints; i++) {
			double s = (double) i / (nPoints - 1);
			points.set(i, head.lerp(tail, s));
		}
	}

	public void stretch(double x1, double y1, double x2, double y2) {
		stretch(new Vector(x1, y1), new Vector(x2, y2));
	}

	public void add(Vector p) {
		points.add(p);
		nPoints++;
	}

	public int getPointCount() {
		return nPoints;
	}

	public Vector getHead() {
		return points.get(0);
	}

	public Vector getTail() {
		return points.get(nPoints - 1);
	}

	public double getLength() {
		double length = 0;
		Iterator<Vector> it = points.iterator();
		Vector p1, p2 = it.next();
		while (it.hasNext()) {
			p1 = p2;
			p2 = it.next();
			length += p1.distance(p2);
		}
		return length;
	}

	public double getLength(int i) {
		if (i < 0 || i >= nPoints - 2)
			return 0;
		return points.get(i).distance(points.get(i + 1));
	}

	public Vector get(int i) {
		if (i < 0 || i >= nPoints)
			return null;
		return points.get(i);
	}

	/**
	 * Gets the point of parameter s.
	 * 
	 * @param s
	 *            - <code>0 <= s <= 1</code>
	 * @return the point
	 */
	public Vector get(double s) {
		if (s <= 0)
			return getHead();
		if (s >= 1)
			return getTail();
		double r = s * (nPoints - 1);
		int i = (int) Math.floor(r);
		Vector p1 = get(i);
		Vector p2 = get(i + 1);
		return p1.lerp(p2, r - i);
	}

	public void set(Vector p, int i) {
		points.set(i, p);
	}

	public Vector getTangent(int i) {
		if (i < 0 || i >= nPoints || nPoints < 2)
			return null;
		if (i == 0)
			return getTangent(getHead(), points.get(1));
		if (i == nPoints - 1)
			return getTangent(points.get(i - 1), getTail());
		return getTangent(points.get(i - 1), points.get(i + 1));
	}

	/**
	 * Gets the tangent to the curve at the point of parameter s.
	 * 
	 * @param s
	 *            - <code>0 <= s <= 1</code>
	 * @return the tangent vector
	 */
	public Vector getTangent(double s) {
		if (s < 0 || s > 1)
			return null;
		double r = s * (nPoints - 1);
		int i = (int) Math.floor(r);
		Vector t1 = getTangent(i);
		Vector t2 = getTangent(i + 1);
		return t1.lerp(t2, r - i);
	}

	private static Vector getTangent(Vector p1, Vector p2) {
		return p2.subtract(p1).multiply(0.5f);
	}

	@SuppressWarnings("unused")
	private static Vector getTangent(Vector p1, Vector p2, Vector p3) {
		Vector d1 = p2.subtract(p1);
		Vector d2 = p3.subtract(p2);
		return p3.subtract(p1).multiply(1 / (d1.magnitude() + d2.magnitude()));
	}

	public Vector getNormal(int i) {
		Vector tangent = getTangent(i);
		if (tangent == null)
			return null;
		return tangent.rotate90CCW().normalize();
	}

	public Vector getNormal(double s) {
		Vector tangent = getTangent(s);
		if (tangent == null)
			return null;
		return tangent.rotate90CCW().normalize();
	}

	public Vector getCurvature(int i) {
		if (i < 0 || i >= nPoints || nPoints < 2)
			return null;
		if (i == 0)
			return Vector.ZERO;
		if (i == nPoints - 1)
			return Vector.ZERO;
		return getCurvature(points.get(i + 1), points.get(i), points.get(i - 1));
	}

	public Vector getCurvature(double s) {
		if (s < 0 || s > 1)
			return null;
		double r = s * (nPoints - 1);
		int i = (int) Math.floor(r);
		Vector k1 = getCurvature(i);
		Vector k2 = getCurvature(i + 1);
		return k1.lerp(k2, r - i);
	}

	private static Vector getCurvature(Vector p1, Vector p2, Vector p3) {
		Vector d1 = p2.subtract(p1);
		Vector d2 = p3.subtract(p2);
		double s = 2.0 / (d1.magnitude() + d2.magnitude());
		Vector t1 = d1.normalize();
		Vector t2 = d2.normalize();
		return new Vector(s * (t2.x - t1.x), s * (t2.y - t1.y));
	}

	public void update(ScalarMap map, double range) {
		for (int i = 0; i < nPoints; i++) {
			Vector p = get(i);
			Vector n = getNormal(i).multiply(range);
			LinearScan scan = new LinearScan(map, p.add(n), p.subtract(n),
					(int) (range * 4));
			points.set(i, scan.getMean());
		}
	}

	public void update(ScalarMap map, double range, int numTimes) {
		for (int i = 0; i < numTimes; i++)
			update(map, range);
	}

	public void paint(PGraphics g) {
		if (nPoints >= 2) {
			g.beginShape();
			for (Vector p : points)
				g.vertex((float) p.x, (float) p.y);
			g.endShape();
		}
	}

	public void paintNormals(PGraphics g, double r) {
		if (nPoints >= 2) {
			for (int i = 0; i < nPoints; i++) {
				Vector.Float p = get(i).toFloat();
				Vector.Float n = getNormal(i).multiply(r).toFloat();
				g.line(p.x + n.x, p.y + n.y, p.x - n.x, p.y - n.y);
			}
		}
	}

	public void paintNormals(PGraphics g, int num, double r) {
		if (nPoints >= 2) {
			for (int i = 0; i < num; i++) {
				double s = (double) i / num;
				Vector.Float p = get(s).toFloat();
				Vector.Float n = getNormal(s).multiply(r).toFloat();
				g.line(p.x + n.x, p.y + n.y, p.x - n.x, p.y - n.y);
			}
		}
	}

	public void paintTangents(PGraphics g) {
		if (nPoints >= 2) {
			for (int i = 0; i < nPoints; i++) {
				Vector.Float p = get(i).toFloat();
				Vector.Float t = getTangent(i).toFloat();
				g.line(p.x, p.y, p.x + t.x, p.y + t.y);
			}
		}
	}

	public void paintTangents(PGraphics g, double scale) {
		if (nPoints >= 2) {
			for (int i = 0; i < nPoints; i++) {
				Vector.Float p = get(i).toFloat();
				Vector.Float t = getTangent(i).multiply(scale).toFloat();
				g.line(p.x, p.y, p.x + t.x, p.y + t.y);
			}
		}
	}

	public void paintTangents(PGraphics g, int num, double scale) {
		if (nPoints >= 3) {
			for (int i = 0; i < num; i++) {
				double s = (double) i / num;
				Vector.Float p = get(s).toFloat();
				Vector.Float t = getTangent(s).multiply(scale).toFloat();
				g.line(p.x, p.y, p.x + t.x, p.y + t.y);
			}
		}
	}

	public void paintCurvatures(PGraphics g) {
		if (nPoints >= 2) {
			for (int i = 0; i < nPoints; i++) {
				Vector.Float p = get(i).toFloat();
				Vector.Float k = getCurvature(i).toFloat();
				g.line(p.x, p.y, p.x + k.x, p.y + k.y);
			}
		}
	}

	public void paintCurvatures(PGraphics g, double scale) {
		if (nPoints >= 2) {
			for (int i = 0; i < nPoints; i++) {
				Vector.Float p = get(i).toFloat();
				Vector.Float k = getCurvature(i).multiply(scale).toFloat();
				g.line(p.x, p.y, p.x + k.x, p.y + k.y);
			}
		}
	}

	public void paintCurvatures(PGraphics g, int num, double scale) {
		if (nPoints >= 2) {
			for (int i = 0; i < num; i++) {
				double s = (double) i / num;
				Vector.Float p = get(s).toFloat();
				Vector.Float k = getCurvature(s).multiply(scale).toFloat();
				g.line(p.x, p.y, p.x + k.x, p.y + k.y);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(getHead());
		for (int i = 1; i < nPoints; i++)
			sb.append(", ").append(points.get(i));
		return sb.append("]").toString();
	}
}
