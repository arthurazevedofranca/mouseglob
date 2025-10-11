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

public class BinaryMap extends DefaultMap<Boolean> {

	private byte[][] v;
	private int width;
	private int height;

	public BinaryMap(int width, int height) {
		this.width = width;
		this.height = height;
		v = new byte[(int) Math.ceil(width / 8.0)][height];
	}

	@Override
	public Boolean get(int i, int j) {
		int p = i % 8;
		return ((v[i / 8][j] >> p) & 1) == 1;
	}

	void set(int i, int j, boolean value) {
		int p = 1 << (i % 8);
		if (value) {
			v[i / 8][j] |= p;
		} else {
			v[i / 8][j] &= ~p;
		}
	}

	@Override
	public Map<Boolean> get(int x, int y, int w, int h) {
		BinaryMap capture = new BinaryMap(w, h);
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				capture.set(i, j, get(i + x, j + y));
			}
		}
		return capture;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
