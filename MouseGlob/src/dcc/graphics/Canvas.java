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
package dcc.graphics;

import dcc.graphics.math.ScalarMap;

public class Canvas extends ScalarMap {
	public Canvas(int width, int height) {
		super(width, height);
	}

	public void resize(int newWidth, int newHeight) {
		if (newWidth == width && newHeight == height)
			return;
		copy(get(0, 0, newWidth, newHeight));
	}

	public void draw(int x, int y) {
		setValue(x, y, 1);
	}

	public void erase(int x, int y) {
		setValue(x, y, 0);
	}

	private void setValue(int x, int y, float value) {
		x = constrain(x, 0, width - 1);
		y = constrain(y, 0, height - 1);
		set(x, y, value);
	}

	private static int constrain(int x, int min, int max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}
}
