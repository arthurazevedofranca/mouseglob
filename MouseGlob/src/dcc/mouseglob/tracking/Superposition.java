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
package dcc.mouseglob.tracking;

import dcc.graphics.binary.BinaryMask2D;
import dcc.graphics.binary.BinaryMask2DUtils;
import dcc.graphics.math.Vector;

final class Superposition {
	private final Tracker tracker1, tracker2;
	private Vector mean;
	private Vector distance;
	BinaryMask2D mask1, mask2;

	Superposition(Tracker t1, Tracker t2) {
		this.tracker1 = t1;
		this.tracker2 = t2;
		calculate();
	}

	private void calculate() {
		Vector p1 = tracker1.getPosition();
		Vector p2 = tracker2.getPosition();
		double s1 = tracker1.getSize();
		double s2 = tracker2.getSize();
		mean = p1.multiply(s1).add(p2.multiply(s2)).multiply(1.0 / (s1 + s2));
		double meanX = (p1.x * s1 + p2.x * s2) / (s1 + s2);
		double meanY = (p1.y * s1 + p2.y * s2) / (s1 + s2);
		mean = new Vector(meanX, meanY);
		distance = p2.subtract(p1);

		mask1 = new BinaryMask2D() {
			@Override
			public boolean get(int i, int j) {
				return new Vector(i, j).subtract(mean).dot(distance) <= 0;
			}
		};
		mask2 = BinaryMask2DUtils.not(mask1);
	}

	Vector getMean() {
		return mean;
	}

}
