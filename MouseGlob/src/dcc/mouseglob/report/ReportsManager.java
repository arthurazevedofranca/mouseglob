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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dcc.inject.Context;
import dcc.inject.Inject;
import dcc.inject.InjectionUtils;
import dcc.mouseglob.analysis.AnalysesManager;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.report.ReportDescriptor.ScalarReportDescriptor;
import dcc.util.ClassCache;

public class ReportsManager {

	private AnalysesManager analysesManager;

	private Set<ReportDescriptor> availableReports;
	private Set<ReportDescriptor> enabledReports;
	private Set<ReportDescriptor> selectedReports;

	@Inject
	public ReportsManager(AnalysesManager analysesManager) {
		ClassCache classCache = ClassCache.getInstance();
		availableReports = new HashSet<ReportDescriptor>();
		for (Class<? extends Report> reportClass : classCache
				.subclassesWithAnnotation(Report.class, ReportInfo.class))
			availableReports.add(new ReportDescriptor(reportClass));
		availableReports = Collections.unmodifiableSet(availableReports);

		this.analysesManager = analysesManager;
		updateEnabledReports();

		selectedReports = new HashSet<ReportDescriptor>();
	}

	Set<ReportDescriptor> getEnabledReports() {
		return enabledReports;
	}

	void select(ReportDescriptor report) {
		selectedReports.add(report);
	}

	void deselect(ReportDescriptor report) {
		selectedReports.remove(report);
	}

	boolean isSelected(ReportDescriptor report) {
		return selectedReports.contains(report);
	}

	Set<ReportDescriptor> getSelectedReports() {
		return selectedReports;
	}

	Set<Class<? extends Analysis>> getRequiredAnalyses() {
		Set<Class<? extends Analysis>> analyses = new HashSet<>();
		for (ReportDescriptor descriptor : selectedReports)
			analyses.addAll(InjectionUtils.getAllDependenciesOfType(
					descriptor.getReportClass(), Analysis.class));
		return analyses;
	}

	public void updateEnabledReports() {
		List<Class<? extends Analysis>> analyses = analysesManager
				.getAnalyses();
		Context context = Context.getGlobal();
		enabledReports = new HashSet<ReportDescriptor>();
		for (ReportDescriptor descriptor : availableReports) {
			Set<Class<?>> dependencies = InjectionUtils
					.getAllDependencies(descriptor.getReportClass());
			boolean dependenciesAvailable = true;
			for (Class<?> dependency : dependencies) {
				if (!analyses.contains(dependency)
						&& !context.contains(dependency)) {
					dependenciesAvailable = false;
					break;
				}
			}
			if (dependenciesAvailable)
				enabledReports.add(descriptor);
		}
		// Generate report descriptors for all scalar analyses available
		for (Class<? extends Analysis> analysis : analyses) {
			if (ScalarAnalysis.class.isAssignableFrom(analysis)) {
				ScalarReportDescriptor descriptor = new ScalarReportDescriptor(
						analysis.asSubclass(ScalarAnalysis.class));
				enabledReports.add(descriptor);
			}
		}
		enabledReports = Collections.unmodifiableSet(enabledReports);
	}

	public List<ReportDescriptor> getDescriptors() {
		List<ReportDescriptor> descriptors = new ArrayList<>(enabledReports);
		Collections.sort(descriptors);
		return descriptors;
	}

}
