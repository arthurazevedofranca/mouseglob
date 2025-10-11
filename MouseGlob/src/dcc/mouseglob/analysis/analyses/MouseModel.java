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
import dcc.graphics.math.Snake;
import dcc.graphics.math.Vector;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.tracking.Tracker;

@AnalysisInfo("Mouse Model")
public class MouseModel implements Analysis {

	@Inject
	private Calibration calibration;
	@Inject
	private OrientationCorrectionAnalysis orientation;
	@Inject
	private Tracker tracker;
	private Snake spine;

	public MouseModel() {
		spine = new Snake(3);
	}

	@Override
	public void update() {
		if (orientation.isOriented()) {
			CoordinateSystem cs = orientation.getCoordinateSystem().scale(
					1 / calibration.getScale());
			double width = cs.getE2().magnitude();
			spine.stretch(cs.get(0.9, 0), cs.get(-0.5, 0));
			spine.update(tracker.getMap(), 3 * width, 2);
		}
	}

	public Vector getHead() {
		return getPoint(0);
	}

	public Vector getTorso() {
		return getPoint(1);
	}

	public Vector getHip() {
		return getPoint(2);
	}

	private Vector getPoint(int i) {
		return orientation.isOriented() ? spine.get(i) : null;
	}

}
