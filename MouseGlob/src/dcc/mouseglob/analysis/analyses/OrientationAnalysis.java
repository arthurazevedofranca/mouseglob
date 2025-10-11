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
package dcc.mouseglob.analysis.analyses;

import dcc.graphics.math.CoordinateSystem;
import dcc.graphics.math.Vector;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;

@AnalysisInfo("Orientation")
public class OrientationAnalysis implements Analysis {

	@Inject
	private MomentsAnalysis moments;
	// TODO Use displacement to make orientation more robust
	// @Inject
	// private DisplacementAnalysis displacement;
	private CoordinateSystem coordinateSystem;
	private Vector lastReliableAxis;
	private boolean isOriented = false;

	@Override
	public void update() {
		if (moments.isEmpty())
			return;

		coordinateSystem = moments.getCoordinateSystem();

		double length = moments.getLength();
		double width = moments.getWidth();

		if (length / width > 1.5) { // Reliable orientation measurement
			Vector majorAxis = coordinateSystem.getE1();
			double skewness = moments.getDirectionalSkewness(majorAxis);
			if (skewness < 0)
				coordinateSystem.rotate180();
			lastReliableAxis = coordinateSystem.getE1();
			isOriented = true;
		} else if (hasFlipped(coordinateSystem.getE1(), lastReliableAxis)) {
			coordinateSystem = coordinateSystem.rotate180();
		}
	}

	private static boolean hasFlipped(Vector axis, Vector reference) {
		if (axis != null && reference != null)
			return axis.dot(reference) < 0;
		return false;
	}

	boolean isOriented() {
		return isOriented;
	}

	Vector getMajorAxis() {
		return coordinateSystem.getE1();
	}

	Vector getMinorAxis() {
		return coordinateSystem.getE2();
	}

	CoordinateSystem getCoordinateSystem() {
		return coordinateSystem;
	}

}
