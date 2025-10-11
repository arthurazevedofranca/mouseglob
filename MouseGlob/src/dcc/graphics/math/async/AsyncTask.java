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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

@SuppressWarnings("serial")
public abstract class AsyncTask extends RecursiveAction {

	public static final int THRESHOLD = 2000;
	private static final ForkJoinPool POOL = new ForkJoinPool();
	private static final boolean MULTI_CORE = (Runtime.getRuntime()
			.availableProcessors() > 1);

	protected abstract void computeDirectly();

	public final void execute(boolean async) {
		if (MULTI_CORE && async)
			POOL.invoke(this);
		else
			computeDirectly();
	}

}
