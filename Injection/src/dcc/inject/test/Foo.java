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
package dcc.inject.test;

import dcc.inject.Inject;

public class Foo {

	@Inject
	private Bar bar;

	public Foo() {
		System.out.println("Foo constructor");
	}

	@Override
	public String toString() {
		return super.toString()
				+ "[bar="
				+ (bar != null ? '@' + Integer.toHexString(bar.hashCode())
						: "null") + "]";
	}

}
