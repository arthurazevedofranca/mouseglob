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
package dcc.mouseglob.movie.speed;

import dcc.util.Stopwatch;

class AdaptiveSpeedBalancer extends SpeedBalancer {
	private static final int VALUE = 20;
	private static final float SPEED_FACTOR = 1f / (2.3f * VALUE);

	private Stopwatch stopwatch = new Stopwatch();

	@Override
	protected float update(int size) {
		// int d = size - VALUE;
		// System.out.println("d = " + d);
		float speed = (float) (SPEED_UPPER_LIMIT * Math.exp(-SPEED_FACTOR
				* size));
		return speed;
	}

	@Override
	public boolean hasSpeedChanged() {
		// long dt = stopwatch.toc();
		// System.out.println("Estimated time per consumed frame: " + dt /
		// 1000000
		// + " ms (" + (1000000000f / dt) + " fps)");
		stopwatch.tic();
		return super.hasSpeedChanged();
	}
}
