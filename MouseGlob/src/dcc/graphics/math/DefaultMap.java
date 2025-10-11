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
package dcc.graphics.math;

import java.util.Iterator;

public abstract class DefaultMap<T> implements Map<T> {

	public final boolean dimensionsMatch(Map<?> otherMap) {
		return this.getWidth() == otherMap.getWidth()
				&& this.getHeight() == otherMap.getHeight();
	}

	@Override
	public final Iterator<T> iterator() {
		return new MapIterator();
	}

	class MapIterator implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			return index < getWidth() * getHeight() - 1;
		}

		@Override
		public T next() {
			T value = get(index % getWidth(), index / getHeight());
			index++;
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove a value from a map.");
		}

	}

}
