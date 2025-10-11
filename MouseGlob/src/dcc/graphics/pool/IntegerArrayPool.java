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

public class IntegerArrayPool {

	private static PoolMap poolMap;

	private static void initialize() {
		if (poolMap == null)
			poolMap = new PoolMap();
	}

	public static int[] get(int length) {
		initialize();
		return poolMap.get(length);
	}

	public static void release(int[] array) {
		initialize();
		poolMap.release(array);
	}

	public static void clear() {
		initialize();
		poolMap.clear();
	}

	public static String status() {
		initialize();
		return poolMap.toString();
	}

	private static class PoolMap extends ObjectPoolMap<int[]> {

		void release(int[] array) {
			if (array == null)
				return;
			release(array, array.length);
		}

		@Override
		ObjectPool<int[]> newPool(int hash) {
			return new Pool(hash);
		}

	}

	private static class Pool extends ObjectPool<int[]> {

		private final int length;

		Pool(int length) {
			this.length = length;
		}

		@Override
		int[] newInstance() {
			return new int[length];
		}

		@Override
		boolean isValid(int[] array) {
			return array.length == length;
		}

		@Override
		public String toString() {
			return String
					.format("[%d: %d (%d)]", length, size(), getHitCount());
		}

	}
}
