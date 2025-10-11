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
import dcc.graphics.series.Series2D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.analysis.Dataset.Time;

@AnalysisInfo("Velocity")
public class VelocityAnalysis extends Series2D implements Analysis {

	@Inject
	private DisplacementAnalysis displacement;
	@Inject
	private Time time;

	@Override
	public void update() {
		if (displacement.size() < 2) {
			add(Vector.ZERO);
		} else {
			Vector ds = displacement.get(-1);
			double dt = time.diff(-1);
			add(ds.multiply(1.0 / dt));
		}
	}

	public Axis getXAxis() {
		return time.getAxis();
	}

	public Axis getYAxis() {
		return Axis.autoscaling(1).setFixedPoint(0).setFormat("%.1f")
				.setLabel("Speed (cm/s)");
	}

}
