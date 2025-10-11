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
import dcc.graphics.plot.oned.Axis;

public class LogAxisTest {

	public static void main(String[] args) {
		Axis axis = new Axis(1e-5, 1);
		for (double x = 1e-5; x <= 1.01; x += 0.05)
			System.out.println(x + " -> " + axis.getRelativePosition(x));
	}

}
