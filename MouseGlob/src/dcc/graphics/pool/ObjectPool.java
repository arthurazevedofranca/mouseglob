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

import java.util.ArrayList;
import java.util.List;

abstract class ObjectPool<T> {

	private static final int MAX_CAPACITY = 100;

	private final List<T> pool;
	private int hitCount;

	ObjectPool() {
		pool = new ArrayList<T>(10);
	}

	final T get() {
		hitCount++;
		if (!pool.isEmpty())
			return pool.remove(0);
		return newInstance();
	}

	final void release(T object) {
		if (object != null && isValid(object) && pool.size() < MAX_CAPACITY)
			pool.add(object);
	}

	final void clear() {
		pool.clear();
	}

	final int size() {
		return pool.size();
	}

	final int getHitCount() {
		return hitCount;
	}

	abstract T newInstance();

	abstract boolean isValid(T object);

}
