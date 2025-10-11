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
package dcc.mouseglob.analysis;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dcc.graphics.plot.oned.Axis;
import dcc.graphics.series.Series1D;
import dcc.inject.Context;
import dcc.inject.Indexer;
import dcc.inject.InjectionUtils;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.report.Report;
import dcc.mouseglob.report.ReportDescriptor;
import dcc.mouseglob.tracking.Tracker;

public class Dataset implements Iterable<Analysis> {

	private final Context context;
	private final Map<Class<? extends Analysis>, Analysis> analyses;
	private final Time time;

	Dataset(Tracker tracker, List<Class<? extends Analysis>> analysisClasses) {
		context = new Context();
		context.merge(Context.getGlobal());
		context.putInstance(tracker);
		context.inject(Indexer.load(analysisClasses));

		Map<Class<? extends Analysis>, Analysis> analyses = new LinkedHashMap<>();
		for (Class<? extends Analysis> clazz : analysisClasses)
			analyses.put(clazz, context.getInstance(clazz));
		this.analyses = Collections.unmodifiableMap(analyses);

		System.err.println(context);

		tracker.setDataset(this);

		time = require(Time.class);
	}

	public void update(long time) {
		this.time.add(time / 1e3);
		for (Analysis analysis : analyses.values())
			analysis.update();
	}

	@SuppressWarnings("unchecked")
	public <T extends Analysis> T get(Class<T> clazz) {
		return (T) analyses.get(clazz);
	}

	/**
	 * Same as {@code get(Class<T>)}, but throws a {@code RuntimeException} if
	 * the required {@code Analysis} was not found.
	 */
	public <T extends Analysis> T require(Class<T> clazz) {
		T analysis = get(clazz);
		if (analysis == null)
			throw new RuntimeException("The required analysis ("
					+ clazz.getName() + ") is not available in the dataset.");
		return analysis;
	}

	public <T extends Report> T getReport(Class<T> reportClass) {
		Set<Class<?>> dependencies = InjectionUtils
				.getAllDependencies(reportClass);
		if (!context.containsAll(dependencies))
			throw new RuntimeException("Some of " + reportClass.getName()
					+ "\'s dependencies (" + dependencies
					+ ") are not available in the dataset.");
		return context.getNewInstance(reportClass);
	}

	public Report getReport(ReportDescriptor reportDescriptor) {
		return getReport(reportDescriptor.getReportClass());
	}

	public Context getContext() {
		return context;
	}

	@Override
	public Iterator<Analysis> iterator() {
		return analyses.values().iterator();
	}

	@AnalysisInfo("Time")
	public static class Time extends Series1D implements Analysis {

		@Override
		public void update() {
		}

		public int find(double time) {
			int n = size();
			double t0 = get(0);
			double tf = get(-1);
			if (time < t0 || time > tf)
				return -1;
			int c = (int) ((n - 1) * (time - t0) / (tf - t0));
			if (get(c) < time) {
				int i = c;
				while (i < n) {
					if (get(i) >= time)
						return i;
					i++;
				}
			} else {
				int i = c;
				while (i >= 0) {
					if (get(i) <= time)
						return i;
					i--;
				}
			}
			return -1;
		}

		public long getMs(int i) {
			return (long) (get(i) * 1000);
		}

		public Axis getAxis() {
			return Axis.autoscaling(1).setFormat("%.2f").setLabel("Time (s)");
		}

	}

}
