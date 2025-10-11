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
package dcc.graphics.math.async;

@SuppressWarnings("serial")
class Default1DAsyncTask extends AsyncTask {

	private final Operation1D operation;
	private final int min, max;

	Default1DAsyncTask(Operation1D operation, int min, int max) {
		this.operation = operation;
		this.min = min;
		this.max = max;
	}

	@Override
	protected final void computeDirectly() {
		for (int i = min; i < max; i++)
			operation.compute(i);
	}

	@Override
	protected final void compute() {
		if (max - min < THRESHOLD) {
			computeDirectly();
		} else {
			int split = (min + max) / 2;
			Default1DAsyncTask left = sub(min, split);
			Default1DAsyncTask right = sub(split, max);
			invokeAll(left, right);
		}
	}

	public final void execute() {
		execute(max - min < THRESHOLD);
	}

	private final Default1DAsyncTask sub(int from, int to) {
		return new Default1DAsyncTask(operation, from, to);
	}

}
