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

import dcc.graphics.plot.oned.Axis;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;

@AnalysisInfo(value = "Angle", format = "%5.1f\u00B0")
public class AngleAnalysis extends ScalarAnalysis {

	@Inject
	private OrientationCorrectionAnalysis orientation;

	@Override
	public double calculate() {
		// Minus sign because the Y axis is inverted
		return -Math.toDegrees(orientation.getMajorAxis().angle());
	}

	@Override
	public Axis getAxis() {
		return new Axis(-180, 180, 30).setFixedPoint(0)
				.setFormat("%5.1f\u00B0").setLabel("Angle");
	}

}
