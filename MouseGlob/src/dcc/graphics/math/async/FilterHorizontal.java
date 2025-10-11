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
package dcc.graphics.math.async;

public class FilterHorizontal extends Operation2D {

	private final double[][] src, dst;
	private final double[] filter;
	private final int width, radius;

	public FilterHorizontal(double[][] src, double[][] dst, double[] filter) {
		super(src.length, src[0].length);
		this.src = src;
		this.dst = dst;
		this.filter = filter;
		width = src.length;
		radius = (filter.length - 1) / 2;
	}

	@Override
	public void compute(int x, int y) {
		dst[x][y] = 0;
		for (int i = -radius; i < filter.length - radius; i++) {
			int cx = Math.min(Math.max(x + i, 0), width - 1);
			dst[x][y] += filter[i + radius] * src[cx][y];
		}
	}

}
