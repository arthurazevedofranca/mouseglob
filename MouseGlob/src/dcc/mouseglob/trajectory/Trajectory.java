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

import java.util.Iterator;

import processing.core.PGraphics;
import dcc.graphics.math.Vector;
import dcc.graphics.series.Series2D;
import dcc.mouseglob.labelable.LabelableObject;
import dcc.mouseglob.labelable.Style;

/**
 * Class that manages trajectories.
 * 
 * @author Daniel Coelho de Castro
 */
public class Trajectory extends LabelableObject implements Iterable<Vector> {
	private double totalDistance;
	private long totalTime;
	private double avgSpeed;
	private final Series2D points;

	private Vector labelPosition;

	/**
	 * Constructor for the <code>Trajectory</code> class.
	 */
	public Trajectory() {
		totalDistance = 0;
		totalTime = 0;
		points = new Series2D();
	}

	/**
	 * Gets the total travelled distance.
	 * 
	 * @return the distance
	 */
	public double getTotalDistance() {
		return totalDistance;
	}

	/**
	 * Calculates the average speed.
	 * 
	 * @return the calculated speed
	 */
	public double getAverageSpeed() {
		avgSpeed = totalDistance / totalTime;
		return avgSpeed;
	}

	/**
	 * Adds a point to the trajectory.
	 * 
	 * @param x
	 *            - horizontal coordinate of the point
	 * @param y
	 *            - vertical coordinate of the point
	 * @param t
	 *            - time
	 */
	public void addPoint(double x, double y, long t) {
		synchronized (points) {
			Vector p = new Vector(x, y);
			if (!points.isEmpty()) {
				Vector last = points.get(points.size() - 1);
				totalDistance += p.distance(last);
			}
			totalTime = t;
			points.add(p);

			if (labelPosition == null
					|| p.x + p.y < labelPosition.x + labelPosition.y) {
				labelPosition = p;
			}
		}
	}

	@Override
	protected void doPaint(PGraphics g) {
		g.beginShape();
		synchronized (points) {
			for (Vector p : points)
				g.vertex((float) p.x, (float) p.y);
		}
		g.endShape();
		paintLabel(g);
	}

	@Override
	protected Style getStyle() {
		return Style.TRAJECTORY;
	}

	/**
	 * Determines whether the mouse is hovering the trajectory.
	 * 
	 * @param mouseX
	 *            - horizontal coordinate of the mouse cursor
	 * @param mouseY
	 *            - vertical coordinate of the mouse cursor
	 * @return <code>true</code> if the mouse is over, <code>false</code>
	 *         otherwise
	 */
	public boolean isHovering(int mouseX, int mouseY) {
		Vector cursor = new Vector(mouseX, mouseY);
		Vector p1, p2 = points.get(0);
		for (int i = 1; i < points.size(); i++) {
			p1 = p2;
			p2 = points.get(i);
			double td = lineDistance(cursor, p1, p2);
			if (td < 5)
				return true;
		}
		return false;
	}

	/**
	 * Calculates the distance from the cursor to a given line segment.
	 */
	private static double lineDistance(Vector cursor, Vector p1, Vector p2) {
		double m = (p2.y - p1.y) / (p2.x - p1.x);
		double h = p2.y - m * p2.x;
		double dis = Math.abs(m * cursor.x - cursor.y + h)
				/ Math.sqrt(m * m + 1);
		double d1 = p1.squaredDistance(cursor);
		double d2 = p2.squaredDistance(cursor);
		double l = p1.squaredDistance(p2);
		if (d1 < d2 + l && d2 < d1 + l)
			return dis;
		else
			return Math.sqrt(Math.min(d1, d2));
	}

	/**
	 * If the mouse is over the trajectory, gets the point closest to it.
	 * 
	 * @return index of the point or <code>-1</code> if the mouse is not
	 *         hovering it
	 */
	public int getHoveredPointIndex(double x, double y) {
		Vector m = new Vector(x, y);
		int over = -1;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.size(); i++) {
			Vector p = points.get(i);
			double td = p.distance(m);
			if (td < min) {
				min = td;
				if (td < 5)
					over = i;
			}
		}
		if (min < 5)
			return over;
		else
			return -1;
	}

	// TODO Use this
	@SuppressWarnings("unused")
	private Vector calculateLabelPosition() {
		Vector labelPosition = null;
		double min = Double.MAX_VALUE;
		for (Vector p : points) {
			double d = p.x + p.y;
			if (d < min) {
				min = d;
				labelPosition = p;
			}
		}
		return labelPosition;
	}

	@Override
	protected Vector getLabelPosition() {
		return labelPosition;
	}

	@Override
	public Iterator<Vector> iterator() {
		return points.iterator();
	}

}
