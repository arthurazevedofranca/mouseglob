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
package dcc.graphics.pool;

public class DoubleMatrixPool {

	private static PoolMap poolMap;

	private static void initialize() {
		if (poolMap == null)
			poolMap = new PoolMap();
	}

	public static double[][] get(int width, int height) {
		initialize();
		return poolMap.get(width, height);
	}

	public static void release(double[][] matrix) {
		initialize();
		poolMap.release(matrix);
	}

	public static void clear() {
		initialize();
		poolMap.clear();
	}

	public static String status() {
		initialize();
		return poolMap.toString();
	}

	private static class PoolMap extends ObjectPoolMap<double[][]> {

		private double[][] get(int width, int height) {
			if (width == 0 || height == 0)
				return null;
			return get((width << 16) | height);
		}

		private void release(double[][] matrix) {
			if (matrix == null)
				return;
			int width = matrix.length;
			int height = matrix[0].length;
			int hash = (width << 16) | height;
			release(matrix, hash);
		}

		@Override
		protected ObjectPool<double[][]> newPool(int hash) {
			int width = (hash >> 16) & 0xffff;
			int height = hash & 0xffff;
			return new Pool(width, height);
		}

	}

	private static class Pool extends ObjectPool<double[][]> {

		private final int width, height;

		private Pool(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		protected double[][] newInstance() {
			return new double[width][height];
		}

		@Override
		protected boolean isValid(double[][] matrix) {
			int mWidth = matrix.length, mHeight = matrix[0].length;
			return mWidth == width && mHeight == height;
		}

		@Override
		public String toString() {
			return String.format("[%d x %d: %d (%d)]", width, height, size(),
					getHitCount());
		}

	}

}
