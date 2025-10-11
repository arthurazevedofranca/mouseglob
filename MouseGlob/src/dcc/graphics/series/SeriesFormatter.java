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
package dcc.graphics.series;

import java.util.ArrayList;
import java.util.List;

import dcc.graphics.plot.oned.Axis;

public class SeriesFormatter {

	private final List<Series1D> series;
	private final List<String> labels;
	private final List<String> formats;

	public SeriesFormatter() {
		series = new ArrayList<Series1D>();
		labels = new ArrayList<String>();
		formats = new ArrayList<String>();
	}

	public void add(Series1D series, Axis axis) {
		add(series, axis.getLabel(), axis.getFormat());
	}

	public void add(Series1D series, String label) {
		add(series, label, null);
	}

	public void add(Series1D series, String label, String format) {
		this.series.add(series);
		labels.add(label);
		formats.add(format);
	}

	public String format() {
		StringBuilder sb = new StringBuilder();
		buildHeader(sb);
		int numRows = series.get(0).size();
		for (int row = 0; row < numRows; row++)
			buildLine(sb, row);
		return sb.toString();
	}

	private void buildHeader(StringBuilder sb) {
		for (int column = 0; column < series.size(); column++) {
			if (column > 0)
				sb.append('\t');
			sb.append(labels.get(column));
		}
		sb.append('\n');
	}

	private void buildLine(StringBuilder sb, int row) {
		for (int column = 0; column < series.size(); column++) {
			if (column > 0)
				sb.append('\t');
			String format = formats.get(column);
			if (format != null) {
				sb.append(String.format(format, series.get(column).get(row)));
			} else {
				sb.append(series.get(column).get(row));
			}
		}
		sb.append('\n');
	}

}
