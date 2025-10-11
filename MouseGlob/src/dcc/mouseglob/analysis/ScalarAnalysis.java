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

import dcc.graphics.plot.oned.Axis;
import dcc.graphics.series.Series1D;
import dcc.mouseglob.inspector.PropertyInspector;

public abstract class ScalarAnalysis extends Series1D implements Analysis {

	public static enum FaultPolicy {
		ADD_NAN, ADD_ZERO, REPEAT_LAST
	}

	private FaultPolicy policy = FaultPolicy.REPEAT_LAST;
	private double last;

	@Override
	public final void update() {
		try {
			double value = calculate();
			if (Double.isFinite(value)) {
				add(value);
				last = value;
			} else {
				handleFault();
			}
		} catch (Exception e) {
			handleFault();
		}
	}

	public final void setFaultPolicy(FaultPolicy policy) {
		this.policy = policy;
	}

	private void handleFault() {
		if (policy == FaultPolicy.ADD_NAN)
			add(Double.NaN);
		else if (policy == FaultPolicy.ADD_ZERO)
			add(0);
		else
			add(last);
	}

	protected abstract double calculate();

	public abstract Axis getAxis();

	public PropertyInspector<Double> getInspector() {
		return ScalarAnalysisInspector.build(this);
	}

	private static class ScalarAnalysisInspector extends
			PropertyInspector<Double> {

		private final ScalarAnalysis analysis;

		private ScalarAnalysisInspector(ScalarAnalysis analysis, String name,
				String format) {
			super(name, format);
			this.analysis = analysis;
		}

		@Override
		protected Double getValue() {
			return analysis.get(-1);
		}

		private static ScalarAnalysisInspector build(ScalarAnalysis analysis) {
			Class<?> clazz = analysis.getClass();
			AnalysisInfo info = clazz.getAnnotation(AnalysisInfo.class);
			String name = info != null ? info.value() : clazz.getSimpleName();
			String format = info != null ? info.format() : "";
			return new ScalarAnalysisInspector(analysis, name, format);
		}

	}

}
