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
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.analysis.ScalarAnalysis;

@AnalysisInfo("Turning")
public class TurningAnalysis extends ScalarAnalysis {

	@Inject
	private AngleAnalysis angle;
	private double turns = 0;

	@Override
	protected double calculate() {
		double d = angle.diff(-1) / 360;
		turns += d - Math.round(d);
		return turns;
	}

	@Override
	public Axis getAxis() {
		return Axis.autoscaling(1).setFixedPoint(0).setFormat("%.1f")
				.setLabel("Complete turns");
	}

}
