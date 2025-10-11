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
class Default2DAsyncTask extends AsyncTask {

	private final Operation2D operation;
	private final int minX, maxX;
	private final int minY, maxY;

	Default2DAsyncTask(Operation2D operation, int minX, int maxX, int minY,
			int maxY) {
		this.operation = operation;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	protected final void computeDirectly() {
		for (int x = minX; x < maxX; x++)
			for (int y = minY; y < maxY; y++)
				operation.compute(x, y);
	}

	@Override
	protected final void compute() {
		if (maxX * (maxY - minY) < THRESHOLD) {
			computeDirectly();
		} else {
			int split = (minY + maxY) / 2;
			Default2DAsyncTask top = sub(minY, split);
			Default2DAsyncTask bottom = sub(split, maxY);
			invokeAll(top, bottom);
		}
	}

	public final void execute() {
		execute(maxX * (maxY - minY) < THRESHOLD);
	}

	private final Default2DAsyncTask sub(int from, int to) {
		return new Default2DAsyncTask(operation, minX, maxX, from, to);
	}

}
