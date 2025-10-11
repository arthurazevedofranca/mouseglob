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
import java.util.ArrayList;

import dcc.util.DoubleList;
import dcc.util.Stopwatch;

@SuppressWarnings("unused")
public class DoubleListBenchmark {

	private static final int INITIAL_CAPACITY = 1000000;

	public static void main(String[] args) {
		ArrayList<Double> arrayList = new ArrayList<>(INITIAL_CAPACITY);
		DoubleList doubleList = new DoubleList(INITIAL_CAPACITY);

		int n = 1000000, k = 100;

		for (int i = 0; i < 10 * n; i++) {
			arrayList.add(1.0);
			doubleList.add(1.0);
			if (i >= INITIAL_CAPACITY - 1) {
				arrayList.clear();
				doubleList.clear();
			}
		}

		arrayListBattery(n, k);
		doubleListBattery(n, k);
	}

	private static void arrayListBattery(int n, int k) {
		long sumAdd = 0, sumGet = 0;
		for (int j = 0; j < k; j++) {
			ArrayList<Double> arrayList = new ArrayList<>(INITIAL_CAPACITY);
			sumAdd += arrayListAddTest(arrayList, n);
			sumGet += arrayListGetTest(arrayList, n);
		}
		double avgAdd = (double) sumAdd / k;
		double avgGet = (double) sumGet / k;
		System.out.println("arrayListAdd: " + (avgAdd / 1e6) + " ms");
		System.out.println("arrayListGet: " + (avgGet / 1e6) + " ms");
	}

	private static void doubleListBattery(int n, int k) {
		long sumAdd = 0, sumGet = 0;
		for (int j = 0; j < k; j++) {
			DoubleList doubleList = new DoubleList(INITIAL_CAPACITY);
			sumAdd += doubleListAddTest(doubleList, n);
			sumGet += doubleListGetTest(doubleList, n);
		}
		double avgAdd = (double) sumAdd / k;
		double avgGet = (double) sumGet / k;
		System.out.println("doubleListAdd: " + (avgAdd / 1e6) + " ms");
		System.out.println("doubleListGet: " + (avgGet / 1e6) + " ms");
	}

	private static long arrayListAddTest(ArrayList<Double> arrayList, int n) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.tic();
		for (int i = 0; i < n; i++) {
			arrayList.add(1.0);
		}
		long dt = stopwatch.toc();
		return dt;
	}

	private static long doubleListAddTest(DoubleList doubleList, int n) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.tic();
		for (int i = 0; i < n; i++) {
			doubleList.add(1.0);
		}
		long dt = stopwatch.toc();
		return dt;
	}

	private static long arrayListGetTest(ArrayList<Double> arrayList, int n) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.tic();
		for (int i = 0; i < n; i++) {
			double x = arrayList.get(i);
		}
		long dt = stopwatch.toc();
		return dt;
	}

	private static long doubleListGetTest(DoubleList doubleList, int n) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.tic();
		for (int i = 0; i < n; i++) {
			double x = doubleList.get(i);
		}
		long dt = stopwatch.toc();
		return dt;
	}

}
