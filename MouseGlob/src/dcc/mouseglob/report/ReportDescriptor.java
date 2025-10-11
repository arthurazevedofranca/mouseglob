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

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.report.ReportHandle.DefaultReportHandle;
import dcc.mouseglob.report.ReportHandle.ScalarReportHandle;
import dcc.mouseglob.tracking.Tracker;
import dcc.util.ClassCache;
import dcc.util.ImageLoader;

public class ReportDescriptor implements Comparable<ReportDescriptor> {

	public static ReportDescriptor fromClassName(String className)
			throws ClassCastException {
		ClassCache classCache = ClassCache.getInstance();
		Class<? extends Report> reportClass = classCache.forName(className,
				Report.class);
		return new ReportDescriptor(reportClass);
	}

	private final Class<? extends Report> reportClass;
	private final ReportInfo info;
	private final ReportIcon icon;

	public ReportDescriptor(Class<? extends Report> reportClass) {
		this.reportClass = reportClass;
		info = reportClass.getAnnotation(ReportInfo.class);
		icon = reportClass.getAnnotation(ReportIcon.class);
	}

	/**
	 * Gets the name of the report as specified in its {@code @ReportInfo}
	 * annotation, if present, or extracts it from the class name (e.g.
	 * {@code FooBarReport} -> {@code "Foo Bar"}).
	 * 
	 * @return the name of the report
	 */
	public String getName() {
		if (info == null)
			return getSplitClassName(getReportClass());
		return info.value();
	}

	public String getIconPath() {
		return icon != null ? icon.value() : null;
	}

	public final Icon getIcon() {
		String iconPath = getIconPath();
		if (iconPath == null)
			return null;
		return new ImageIcon(getIconImage());
	}

	public final Image getIconImage() {
		String iconPath = getIconPath();
		if (iconPath == null)
			return null;
		return ImageLoader.load(getIconPath());
	}

	public final Class<? extends Report> getReportClass() {
		return reportClass;
	}

	public ReportHandle getHandle(Tracker tracker) {
		return new DefaultReportHandle(this, tracker);
	}

	@Override
	public String toString() {
		return getName();
	}

	private static String getSplitClassName(Class<?> clazz) {
		String className = clazz.getSimpleName();
		// Splits behind every capital letter not at the beginning
		String[] split = className.split("(?<!^)(?=[A-Z])");
		StringBuilder sb = new StringBuilder(split[0]);
		for (int i = 1; i < split.length - 1; i++)
			sb.append(' ').append(split[i]);
		return sb.toString();
	}

	@Override
	public int compareTo(ReportDescriptor o) {
		return getName().compareTo(o.getName());
	}

	public static class ScalarReportDescriptor extends ReportDescriptor {

		public static ScalarReportDescriptor fromAnalysisClassName(
				String analysisClassName) throws ClassCastException {
			ClassCache classCache = ClassCache.getInstance();
			Class<?> clazz = classCache.forName(analysisClassName);
			Class<? extends ScalarAnalysis> analysisClass = clazz
					.asSubclass(ScalarAnalysis.class);
			return new ScalarReportDescriptor(analysisClass);
		}

		private final Class<? extends ScalarAnalysis> analysisClass;

		public ScalarReportDescriptor(
				Class<? extends ScalarAnalysis> analysisClass) {
			super(Report.class);
			this.analysisClass = analysisClass;
		}

		@Override
		public String getName() {
			return getSplitClassName(analysisClass);
		}

		@Override
		public String getIconPath() {
			return "/resource/timeSeriesReport16.png";
		}

		public Class<? extends ScalarAnalysis> getAnalysisClass() {
			return analysisClass;
		}

		@Override
		public ScalarReportHandle getHandle(Tracker tracker) {
			return new ScalarReportHandle(this, tracker);
		}

	}

}
