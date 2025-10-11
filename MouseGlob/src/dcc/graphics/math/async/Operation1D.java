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

public abstract class Operation1D {

	private final int minX, maxX;

	protected Operation1D(int minX, int maxX) {
		this.minX = minX;
		this.maxX = maxX;
	}

	protected Operation1D(int length) {
		this(0, length);
	}

	protected abstract void compute(int i);

	public final void execute() {
		new Default1DAsyncTask(this, minX, maxX).execute();
	}

	public final void execute(boolean async) {
		new Default1DAsyncTask(this, minX, maxX).execute(async);
	}

}
