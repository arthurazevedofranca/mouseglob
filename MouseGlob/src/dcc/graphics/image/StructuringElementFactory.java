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
package dcc.graphics.image;

import dcc.graphics.Color;

public class StructuringElementFactory {

	private static final int ONE = Color.WHITE;
	private static final int ZERO = Color.BLACK;
	private static final int BLANK = 0;

	public static int[][] rectangle(int width, int height) {
		int[][] element = new int[height][width];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				element[j][i] = ONE;
		return element;
	}

	public static int[][] disk(double radius) {
		int diameter = 2 * (int) Math.ceil(radius) + 1;
		int[][] disk = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				double dx = i - radius;
				double dy = j - radius;
				disk[j][i] = (Math.hypot(dx, dy) <= radius) ? ONE : BLANK;
			}
		}
		return disk;
	}

	public static int[][] rightEdge() {
		return new int[][] { { ONE, ZERO }, { ONE, ZERO } };
	}

	public static int[][] topEdge() {
		return new int[][] { { ZERO, ZERO }, { ONE, ONE } };
	}

	public static int[][] leftEdge() {
		return new int[][] { { ZERO, ONE }, { ZERO, ONE } };
	}

	public static int[][] bottomEdge() {
		return new int[][] { { ONE, ONE }, { ZERO, ZERO } };
	}

	public static int[][][] edges() {
		return new int[][][] { rightEdge(), topEdge(), leftEdge(), bottomEdge() };
	}

	public static int[][] topRightCorner() {
		return new int[][] { { BLANK, ZERO, ZERO }, { ONE, ONE, ZERO },
				{ BLANK, ONE, BLANK } };
	}

	public static int[][] topLeftCorner() {
		return rotate90(topRightCorner());
	}

	public static int[][] bottomLeftCorner() {
		return rotate90(topLeftCorner());
	}

	public static int[][] bottomRightCorner() {
		return rotate90(bottomLeftCorner());
	}

	public static int[][][] corners() {
		return new int[][][] { topRightCorner(), topLeftCorner(),
				bottomLeftCorner(), bottomRightCorner() };
	}

	private static int[][] rotate45(int[][] filter) {
		int[][] newFilter = new int[3][3];

		newFilter[1][1] = filter[1][1];
		newFilter[0][0] = filter[0][1];
		newFilter[0][1] = filter[0][2];
		newFilter[0][2] = filter[1][2];
		newFilter[1][2] = filter[2][2];
		newFilter[2][2] = filter[2][1];
		newFilter[2][1] = filter[2][0];
		newFilter[2][0] = filter[1][0];
		newFilter[1][0] = filter[0][0];

		return newFilter;
	}

	private static int[][] rotate90(int[][] filter) {
		return rotate45(rotate45(filter));
	}

	private static int[][] rotate135(int[][] filter) {
		return rotate45(rotate90(filter));
	}

	private static int[][] rotate180(int[][] filter) {
		return rotate45(rotate135(filter));
	}

	private static int[][] rotate225(int[][] filter) {
		return rotate45(rotate180(filter));
	}

	private static int[][] rotate270(int[][] filter) {
		return rotate45(rotate225(filter));
	}

	@SuppressWarnings("unused")
	private static int[][] rotate315(int[][] filter) {
		return rotate45(rotate270(filter));
	}

}
