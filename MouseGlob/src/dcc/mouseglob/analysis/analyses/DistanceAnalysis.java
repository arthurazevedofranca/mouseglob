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

import dcc.graphics.math.Vector;
import dcc.graphics.plot.oned.Axis;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;

@AnalysisInfo("Distance")
public class DistanceAnalysis extends ScalarAnalysis {

	@Inject
	private PositionAnalysis position;

	@Override
	public double calculate() {
		Vector curr = position.get(-1);
		Vector prev = position.get(-2);
		return curr.distance(prev);
	}

	@Override
	public Axis getAxis() {
		return Axis.autoscalingMax(0, 1).setLabel("Distance (cm)")
				.setFormat("%.1f");
	}

}
