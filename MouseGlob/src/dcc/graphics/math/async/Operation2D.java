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

public abstract class Operation2D {

	private final int minX, maxX;
	private final int minY, maxY;

	protected Operation2D(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	protected Operation2D(int width, int height) {
		this(0, width, 0, height);
	}

	protected abstract void compute(int x, int y);

	public final void execute() {
		new Default2DAsyncTask(this, minX, maxX, minY, maxY).execute();
	}

	public final void execute(boolean async) {
		new Default2DAsyncTask(this, minX, maxX, minY, maxY).execute(async);
	}

}
