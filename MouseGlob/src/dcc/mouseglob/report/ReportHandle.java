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
package dcc.mouseglob.report;

import dcc.mouseglob.analysis.Dataset;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.report.ReportDescriptor.ScalarReportDescriptor;
import dcc.mouseglob.tracking.Tracker;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;

public abstract class ReportHandle implements Treeable {

	private final ReportDescriptor descriptor;

	public ReportHandle(ReportDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public ReportDescriptor getDescriptor() {
		return descriptor;
	}

	public abstract Report getReport();

	@Override
	public TreeNode getNode() {
		return new TreeNode(this, descriptor.getName(),
				descriptor.getIconPath());
	}

	static class DefaultReportHandle extends ReportHandle {

		private final Tracker tracker;

		DefaultReportHandle(ReportDescriptor descriptor, Tracker tracker) {
			super(descriptor);
			this.tracker = tracker;
		}

		public Tracker getTracker() {
			return tracker;
		}

		@Override
		public Report getReport() {
			Dataset dataset = tracker.getDataset();
			return dataset.getReport(getDescriptor().getReportClass());
		}

	}

	static class ScalarReportHandle extends DefaultReportHandle {

		private final ScalarReportDescriptor descriptor;

		ScalarReportHandle(ScalarReportDescriptor descriptor, Tracker tracker) {
			super(descriptor, tracker);
			this.descriptor = descriptor;
		}

		@Override
		public ScalarReport getReport() {
			Dataset dataset = getTracker().getDataset();
			Time time = dataset.require(Time.class);
			ScalarAnalysis analysis = dataset.require(descriptor
					.getAnalysisClass());
			return new ScalarReport(time, analysis);
		}

	}

}
