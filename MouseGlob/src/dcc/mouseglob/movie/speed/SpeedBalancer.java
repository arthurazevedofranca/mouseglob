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

public abstract class SpeedBalancer {
	public static final SpeedBalancer ADAPTIVE = new AdaptiveSpeedBalancer();
	public static final SpeedBalancer ABSOLUTE = new AbsoluteSpeedBalancer();
	public static final SpeedBalancer SINGLE = new SingleSpeedBalancer();
	public static final SpeedBalancer DOUBLE = new DoubleSpeedBalancer();

	protected static final float SPEED_LOWER_LIMIT = 0.1f;
	protected static final float SPEED_UPPER_LIMIT = 10;
	private static final float THRESHOLD = 0.2f;

	protected float speed;
	private boolean updateSpeed = false;

	public final void update(float currentSpeed, int queueSize) {
		float newSpeed = update(queueSize);
		// System.out.println("v: " + speed + " -> " + newSpeed);
		speed = newSpeed;

		if (speed > SPEED_UPPER_LIMIT)
			speed = SPEED_UPPER_LIMIT;
		else if (speed < SPEED_LOWER_LIMIT)
			speed = SPEED_LOWER_LIMIT;

		float dv = (speed - currentSpeed) / currentSpeed;
		// System.out.println("dv: " + dv);
		if (dv >= THRESHOLD || dv <= -THRESHOLD)
			updateSpeed = true;
	}

	protected abstract float update(int size);

	public boolean hasSpeedChanged() {
		return updateSpeed;
	}

	public float getSpeed() {
		updateSpeed = false;
		return speed;
	}
}
