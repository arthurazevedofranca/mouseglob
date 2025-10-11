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

public abstract class AbstractModule<M, V extends View, C extends Controller>
		implements Module {
	private M model;
	private V view;
	private C controller;

	public AbstractModule(M model, V view, C controller) {
		setModel(model);
		setView(view);
		setController(controller);
	}

	public AbstractModule() {
		this(null, null, null);
	}

	public final M getModel() {
		return model;
	}

	protected final void setModel(M model) {
		this.model = model;
	}

	public final V getView() {
		return view;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void setView(V view) {
		this.view = view;
		if (view instanceof AbstractView)
			((AbstractView) this.view).module = this;
	}

	public final C getController() {
		return controller;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void setController(C controller) {
		this.controller = controller;
		if (this.controller instanceof AbstractController)
			((AbstractController) this.controller).module = this;
	}
}
