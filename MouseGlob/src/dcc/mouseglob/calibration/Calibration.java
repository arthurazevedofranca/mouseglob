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
package dcc.mouseglob.calibration;

import java.util.ArrayList;
import java.util.Collection;

import processing.core.PApplet;
import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.Inspectable;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.tree.DefaultTreeable;

/**
 * Class that holds information about calibration in centimeters/pixel.
 * 
 * @author Daniel Coelho de Castro
 */
public class Calibration extends DefaultTreeable implements Paintable,
		InspectableObject {

	private Vector p1, p2;
	private double cm, px;
	private double scale;

	private int mouseX, mouseY;

	private Collection<CalibrationListener> listeners;

	private Inspector inspector;

	/**
	 * Constructor for the <code>Calibration</code> class.
	 */
	Calibration() {
		super("Calibration", "/resource/calibrate16.png");

		listeners = new ArrayList<CalibrationListener>();
		inspector = new Inspector("Calibration", this);
	}

	/**
	 * Determines whether the experiment has been calibrated.
	 * 
	 * @return <code>true</code> if a scale has been set, <code>false</code>
	 *         otherwise
	 */
	public boolean hasScale() {
		return scale != 0;
	}

	/**
	 * Sets the first point of the calibration.
	 * 
	 * @param x
	 *            - horizontal coordinate of the point
	 * @param y
	 *            - vertical coordinate of the point
	 */
	void setP1(double x, double y) {
		p1 = new Vector(x, y);
		p2 = null;
	}

	/**
	 * Sets the second point of the calibration.
	 * 
	 * @param x
	 *            - horizontal coordinate of the point
	 * @param y
	 *            - vertical coordinate of the point
	 */
	void setP2(double x, double y) {
		p2 = new Vector(x, y);
		if (p1 != null)
			setPx(p2.distance(p1));
	}

	void setTemporary(double x, double y) {
		mouseX = (int) x;
		mouseY = (int) y;
	}

	/**
	 * Sets the length in centimeters.
	 * 
	 * @param value
	 *            - distance in centimeters
	 */
	void setCm(double value) {
		scale = 0;
		cm = value;
		if (px != 0)
			setScale(cm / px);
	}

	/**
	 * Gets the length in centimeters.
	 * 
	 * @return distance in centimeters
	 */
	public double getCm() {
		return cm;
	}

	/**
	 * Sets the length in pixels.
	 * 
	 * @param value
	 *            - distance in pixels
	 */
	void setPx(double value) {
		p1 = p2 = null;
		scale = 0;
		px = value;
		if (cm != 0)
			setScale(cm / px);
	}

	/**
	 * Gets the length in pixels.
	 * 
	 * @return distance in pixels
	 */
	public double getPx() {
		return px;
	}

	/**
	 * Determines whether both the distance in pixels and in centimeters were
	 * already set.
	 * 
	 * @return <code>true</code> if they were, <code>false</code> otherwise
	 */
	public boolean isCalibrated() {
		return px != 0 && cm != 0;
	}

	/**
	 * Sets the scale value.
	 * 
	 * @param value
	 *            - the scale, in cm/px
	 */
	public void setScale(double value) {
		scale = value;
		for (CalibrationListener listener : listeners)
			listener.onCalibrationSet(scale);
		inspector.update();
	}

	/**
	 * Gets the scale value.
	 * 
	 * @return the scale value, in cm/px
	 */
	@Inspectable(value = "Scale (cm/px)", format = "%.3f")
	public double getScale() {
		return scale;
	}

	void reset() {
		p1 = p2 = null;
		cm = px = 0;
		scale = 0;
	}

	void clearPoints() {
		p1 = p2 = null;
	}

	public Vector pxToCm(Vector v) {
		return v.multiply(scale);
	}

	public Vector cmToPx(Vector v) {
		return v.multiply(1.0 / scale);
	}

	/**
	 * Displays the calibration ruler.
	 * 
	 * @param g
	 *            - the graphics context onto which to paint
	 */
	@Override
	public void paint(PGraphics g) {
		if (p1 != null) {
			g.pushStyle();
			g.noFill();
			g.stroke(255);
			g.ellipse((float) p1.x, (float) p1.y, 8, 8);
			g.ellipse(mouseX, mouseY, 8, 8);
			g.line((float) p1.x, (float) p1.y, mouseX, mouseY);
			String s = String.format("%.2f px",
					p1.distance(new Vector(mouseX, mouseY)));
			g.fill(0);
			g.noStroke();
			g.rectMode(PApplet.CORNER);
			g.rect(mouseX + 10, mouseY - 10, g.textWidth(s) + 2, 10);
			g.fill(255);
			g.textAlign(PApplet.LEFT, PApplet.TOP);
			g.text(s, mouseX + 11, mouseY - 9);
			g.popStyle();
		}
	}

	public void addCalibratrionListener(CalibrationListener listener) {
		listeners.add(listener);
	}

	@Override
	public Inspector getInspector() {
		return inspector;
	}

}
