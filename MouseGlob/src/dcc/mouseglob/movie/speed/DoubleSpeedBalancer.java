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

class DoubleSpeedBalancer extends SpeedBalancer {
	private static final float SPEED_FACTOR = 1.1f;
	private static final int LOWER_LIMIT = 5;
	private static final int UPPER_LIMIT = 15;

	@Override
	protected float update(int size) {
		if (size <= LOWER_LIMIT)
			speed *= SPEED_FACTOR;
		else if (size >= UPPER_LIMIT)
			speed /= SPEED_FACTOR;
		return speed;
	}
}
