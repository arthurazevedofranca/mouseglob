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

class SingleSpeedBalancer extends SpeedBalancer {
	private static final float SPEED_FACTOR = 0.01f;
	private static final int VALUE = 10;

	@Override
	protected float update(int size) {
		int d = size - VALUE;
		d = (d < 3 && d > -3) ? 0 : d;
		speed += -d * SPEED_FACTOR;
		return speed;
	}
}
