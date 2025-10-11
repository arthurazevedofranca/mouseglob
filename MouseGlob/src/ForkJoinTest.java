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
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import dcc.util.Stopwatch;

public class ForkJoinTest {

	private static final int SIZE = 640 * 480;
	private static final int MAX = 256;
	private static final int TIMES = 100;

	public static void main(String[] args) {
		int[] array = new int[SIZE];
		Random random = new Random();
		for (int i = 0; i < SIZE; i++)
			array[i] = random.nextInt(MAX);
		int[] map = new int[MAX];
		for (int i = 0; i < MAX; i++)
			map[i] = random.nextInt(MAX);

		Stopwatch stopwatch = new Stopwatch();
		ForkJoinPool pool = new ForkJoinPool();
		for (int i = 0; i < 1000; i++)
			pool.invoke(new Task(array, map, 0, array.length, array));

		Task.threshold = SIZE;
		stopwatch.tic();
		for (int i = 0; i < TIMES; i++)
			pool.invoke(new Task(array, map, 0, array.length, array));
		long dt = stopwatch.toc();
		System.out.println("Mapping " + SIZE + " elements: " + dt
				/ (1e6 * TIMES) + " ms");

		Task.threshold = 10000;
		stopwatch.tic();
		for (int i = 0; i < TIMES; i++)
			pool.invoke(new Task(array, map, 0, array.length, array));
		dt = stopwatch.toc();
		System.out.println("Mapping " + SIZE + " elements: " + dt
				/ (1e6 * TIMES) + " ms");
	}

	@SuppressWarnings("serial")
	private static class Task extends RecursiveAction {

		private static int threshold = 10000;
		private final int[] array, map, dest;
		private final int from, to;

		private Task(int[] array, int[] map, int from, int to, int[] dest) {
			this.array = array;
			this.map = map;
			this.from = from;
			this.to = to;
			this.dest = dest;
		}

		@Override
		protected void compute() {
			if (to - from <= threshold) {
				for (int i = from; i < to; i++)
					dest[i] = map[array[i]];
				return;
			}

			int split = (from + to) / 2;
			invokeAll(new Task(array, map, from, split, dest), new Task(array,
					map, split, to, dest));
		}
	}

}
