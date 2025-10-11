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
package dcc.mouseglob.visit;

import dcc.graphics.math.ScalarMap;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.tracking.Tracker;

// TODO @AnalysisInfo("Area Analysis")
public class AreaAnalysis implements Analysis {

	@Inject
	private Tracker tracker;

	@Override
	public void update() {
		ScalarMap map = tracker.getMap();
		double totalArea = map.getSum();
	}

}
