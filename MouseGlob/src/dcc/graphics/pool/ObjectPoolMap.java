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

import java.util.HashMap;

abstract class ObjectPoolMap<T> {

	private HashMap<Integer, ObjectPool<T>> poolMap = new HashMap<>(10);

	T get(int hash) {
		ObjectPool<T> pool = getPool(hash);
		return pool.get();
	}

	final void release(T object, int hash) {
		if (object == null)
			return;
		ObjectPool<T> pool = getPool(hash);
		pool.release(object);
	}

	final ObjectPool<T> getPool(int hash) {
		ObjectPool<T> pool = poolMap.get(hash);
		if (pool == null) {
			pool = newPool(hash);
			poolMap.put(hash, pool);
		}
		return pool;
	}

	final void clear() {
		for (ObjectPool<T> pool : poolMap.values())
			pool.clear();
		poolMap.clear();
	}

	abstract ObjectPool<T> newPool(int hash);

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		boolean first = true;
		for (ObjectPool<T> pool : poolMap.values()) {
			if (!first)
				sb.append(", ");
			sb.append(pool.toString());
			first = false;
		}
		return sb.append(")").toString();
	}

}
