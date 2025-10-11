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

@AnalysisInfo(value = "Delta", format = "%5.1f\u00B0")
public class DeltaAnalysis extends ScalarAnalysis {

	@Inject
	private MouseModel mouseModel;

	@Override
	protected double calculate() {
		Vector head = mouseModel.getHead();
		Vector torso = mouseModel.getTorso();
		Vector hip = mouseModel.getHip();
		Vector upper = head.subtract(torso);
		Vector lower = torso.subtract(hip);
		// Minus sign because the Y axis is inverted
		return -Math.toDegrees(lower.angle(upper));
	}

	@Override
	public Axis getAxis() {
		return Axis.autoscaling(10).setFixedPoint(0).setFormat("%5.1f\u00B0")
				.setLabel("Delta");
	}

}
