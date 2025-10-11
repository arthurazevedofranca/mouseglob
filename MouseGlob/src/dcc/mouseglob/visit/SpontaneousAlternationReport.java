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

import java.util.List;

import dcc.inject.Inject;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.TextReport;
import dcc.mouseglob.visit.VisitAnalysis.VisitListener;

@ReportInfo("Spontaneous Alternation")
@ReportIcon("/resource/alternationReport16.png")
public class SpontaneousAlternationReport extends TextReport implements
		VisitListener {

	private final VisitAnalysis visitAnalysis;
	private final int cycleLength;

	@Inject
	public SpontaneousAlternationReport(VisitAnalysis visitAnalysis) {
		this.visitAnalysis = visitAnalysis;
		cycleLength = visitAnalysis.getEventClasses().size();
		update();
		visitAnalysis.addVisitListener(this);
	}

	private void update() {
		List<VisitEventClass> sequence = visitAnalysis.getEventClassSequence();
		AlternationCounter alternationCounter = new AlternationCounter(
				sequence, cycleLength);
		int visits = sequence.size();
		VisitEventClass startingVisit = visitAnalysis.getStartingVisit();
		int count = alternationCounter.getAlternationCount();
		double score = alternationCounter.getAlternationScore();

		TableBuilder table = new TableBuilder();
		table.setColumnWidths(-1, 50);
		if (startingVisit == null) {
			table.addRow("Visit Count:", visits);
		} else {
			table.addRow(String.format(
					"Visit Count:<br/>(minus starting \'%s\')",
					startingVisit.getShortDescription()), visits - 1);
		}
		table.addRow("Alternation Count:", count);
		table.addRow("Alternation Score:", formatScore(score));
		setText(table.toString());
	}

	private static String formatScore(double score) {
		if (Double.isFinite(score))
			return String.format("%3.2f %%", score * 100);
		return "- %";
	}

	@Override
	public void onVisitEvent() {
		update();
	}

	@Override
	public void onClose() {
		visitAnalysis.removeVisitListener(this);
	}

}
