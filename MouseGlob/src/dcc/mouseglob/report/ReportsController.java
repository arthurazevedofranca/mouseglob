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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.module.Controller;
import dcc.mouseglob.report.ReportHandle.DefaultReportHandle;
import dcc.tree.TreeSelectionListener;
import dcc.tree.Treeable;
import dcc.ui.ToggleAction;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

public class ReportsController implements Controller, TreeSelectionListener,
		XMLEncodable {

	@Inject
	private ReportsManager manager;
	List<ReportAction> actions;

	@Inject
	private void initActions() {
		actions = new ArrayList<ReportAction>();
		List<ReportDescriptor> reports = new ArrayList<>(
				manager.getEnabledReports());
		Collections.sort(reports);
		for (ReportDescriptor report : reports)
			actions.add(new ReportAction(report));
	}

	@Override
	public void nodeSelected(Treeable object) {
	}

	@Override
	public void nodeDoubleClicked(Treeable object) {
		if (object instanceof ReportHandle) {
			ReportHandle handle = (ReportHandle) object;
			Report report = handle.getReport();
			JDialog dialog = report.getDialog();
			if (handle instanceof DefaultReportHandle) {
				String trackerName = ((DefaultReportHandle) handle)
						.getTracker().getName();
				dialog.setTitle(dialog.getTitle() + " - " + trackerName);
			}
			dialog.setVisible(true);
		}
	}

	@SuppressWarnings("serial")
	class ReportAction extends ToggleAction {

		private final ReportDescriptor report;

		public ReportAction(ReportDescriptor report) {
			super(report.getName(), report.getIconPath());
			this.report = report;
		}

		@Override
		public void itemStateChanged(boolean state) {
			if (state)
				manager.select(report);
			else
				manager.deselect(report);
		}

		public JPanel getDecoratedPanel() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(new JLabel(report.getIcon()));
			panel.add(getCheckBox());
			panel.add(Box.createHorizontalGlue());
			return panel;
		}

	}

	@Override
	public String getTagName() {
		return "reports";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new ReportsXMLCodec(processor, manager);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new ReportsXMLCodec(processor, manager);
	}

}
