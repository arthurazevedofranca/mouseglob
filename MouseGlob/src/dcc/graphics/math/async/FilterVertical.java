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

public class FilterVertical extends Operation2D {

	private final double[][] src, dst;
	private final double[] filter;
	private final int height, radius;

	public FilterVertical(double[][] src, double[][] dst, double[] filter) {
		super(src.length, src[0].length);
		this.src = src;
		this.dst = dst;
		this.filter = filter;
		height = src[0].length;
		radius = (filter.length - 1) / 2;
	}

	@Override
	public void compute(int x, int y) {
		dst[x][y] = 0;
		for (int j = -radius; j < filter.length - radius; j++) {
			int cy = Math.min(Math.max(y + j, 0), height - 1);
			dst[x][y] += filter[j + radius] * src[x][cy];
		}
	}

}
