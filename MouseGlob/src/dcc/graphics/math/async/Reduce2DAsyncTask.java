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
public class Reduce2DAsyncTask extends AsyncTask {

	private final int minX, maxX;
	private final int minY, maxY;
	private final ReduceOperation operation;
	private double value;

	Reduce2DAsyncTask(ReduceOperation operation, int minX, int maxX, int minY,
			int maxY) {
		this.operation = operation;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	protected final void computeDirectly() {
		value = 0;
		for (int x = minX; x < maxX; x++)
			for (int y = minY; y < maxY; y++)
				value = operation.reduce(value, x, y);
	}

	@Override
	protected final void compute() {
		if (maxX * (maxY - minY) < THRESHOLD) {
			computeDirectly();
		} else {
			int split = (minY + maxY) / 2;
			Reduce2DAsyncTask top = sub(minY, split);
			Reduce2DAsyncTask bottom = sub(split, maxY);
			invokeAll(top, bottom);
		}
	}

	public double getResult() {
		return value;
	}

	public final void execute() {
		execute(maxX * (maxY - minY) < THRESHOLD);
	}

	private final Reduce2DAsyncTask sub(int from, int to) {
		return new Reduce2DAsyncTask(operation, minX, maxX, from, to);
	}

	public static abstract class ReduceOperation {

		private final int minX, maxX;
		private final int minY, maxY;

		public ReduceOperation(int minX, int maxX, int minY, int maxY) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}

		protected abstract double reduce(double currentValue, int x, int y);

		public final double execute() {
			Reduce2DAsyncTask task = new Reduce2DAsyncTask(this, minX, maxX,
					minY, maxY);
			task.execute();
			return task.getResult();
		}

		public final void execute(boolean async) {
			new Reduce2DAsyncTask(this, minX, maxX, minY, maxY).execute(async);
		}

	}

}
