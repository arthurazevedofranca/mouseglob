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

enum Operator {
	NOT, THRESHOLD, ADD {
		@Override
		int operate(int x, int y) {
			return x + y;
		}
	},
	OFFSET, SUBTRACT, DIFFERENCE, MULIPLY, DIVIDE, AND, OR, XOR;

	int operate(int x, int y) {
		switch (this) {
		case OFFSET:
			return Math.min(Math.max(x + y, 0), 255);
		case SUBTRACT:
			return x - y;
		case DIFFERENCE:
			return Math.abs(x - y);
		case MULIPLY:
			return x * y;
		case DIVIDE:
			return x / y;
		case AND:
			return x & y;
		case OR:
			return x | y;
		case XOR:
			return x ^ y;
		default:
			return 0;
		}
	}
}
