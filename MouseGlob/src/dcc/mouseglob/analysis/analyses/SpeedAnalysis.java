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
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.ScalarAnalysis;

@AnalysisInfo("Speed")
public class SpeedAnalysis extends ScalarAnalysis {

	@Inject
	private DistanceAnalysis distance;
	@Inject
	private Time time;

	@Override
	public double calculate() {
		double du = distance.get(-1);
		double dt = time.diff(-1);
		return du / dt;
	}

	@Override
	public Axis getAxis() {
		return Axis.autoscalingMax(0, 1).setLabel("Speed (cm/s)")
				.setFormat("%.2f");
	}

}
