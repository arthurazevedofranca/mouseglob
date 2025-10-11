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
package dcc.module;

public abstract class AbstractController<T extends AbstractModule<?, ?, ? extends AbstractController<T>>>
		implements Controller {
	T module;

	public AbstractController(T module) {
		this.module = module;
	}

	public AbstractController() {
		this(null);
	}

	protected final T getModule() {
		return module;
	}
}
