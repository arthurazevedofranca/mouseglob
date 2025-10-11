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
package dcc.util;

import java.util.Iterator;

public class DoubleList implements Iterable<Double> {

	public static void main(String[] args) {
		DoubleList list = new DoubleList(5);
		for (int i = 0; i < 15; i++)
			list.add(i);
		for (int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));

		System.out.println("---");
		list.remove(10);
		for (int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
		try {
			list.get(-1);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		try {
			list.get(20);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		System.out.println("---");
		list.clear();
		for (int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
	}

	private static final int DEFAULT_INITIAL_CAPACITY = 100;

	private double[] values;
	private int size;

	public DoubleList(int initialCapacity) {
		values = new double[initialCapacity];
		size = 0;
	}

	public DoubleList() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	private void ensureSize(int newSize) {
		if (newSize >= values.length) {
			double[] newValues = new double[2 * values.length];
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}

	public void add(double x) {
		ensureSize(size);
		values[size] = x;
		size++;
	}

	public void add(int index, double x) {
		ensureSize(size);
		System.arraycopy(values, index, values, index + 1, size);
		values[index] = x;
		size++;
	}

	public void addAll(double... x) {
		ensureSize(size + x.length - 1);
		System.arraycopy(x, 0, values, size, x.length);
		size += x.length;
	}

	public double remove(int index) {
		double x = values[index];
		System.arraycopy(values, index + 1, values, index, size - index - 1);
		size--;
		return x;
	}

	public double get(int index) {
		if (index >= size())
			throw new ArrayIndexOutOfBoundsException(index);
		return values[index];
	}

	public void set(int index, double x) {
		if (index >= size())
			throw new ArrayIndexOutOfBoundsException(index);
		values[index] = x;
	}

	public void clear() {
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<Double> iterator() {
		return new ArrayIterator();
	}

	public int size() {
		return size;
	}

	public double[] toArray() {
		return values.clone();
	}

	private class ArrayIterator implements Iterator<Double> {

		private int i = 0;

		@Override
		public boolean hasNext() {
			return i < size;
		}

		@Override
		public Double next() {
			return values[i++];
		}

		@Override
		public void remove() {
			DoubleList.this.remove(i);
		}

	}

}
