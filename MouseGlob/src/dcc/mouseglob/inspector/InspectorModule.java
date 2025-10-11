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
package dcc.mouseglob.inspector;

import dcc.inject.Inject;
import dcc.module.AbstractModule;
import dcc.module.ModuleNotInitializedException;

public class InspectorModule extends
		AbstractModule<InspectorModel, InspectorUI, InspectorController> {
	private static InspectorModule instance;

	@Inject
	private InspectorModule(InspectorModel model, InspectorUI view,
			InspectorController controller) {
		super(model, view, controller);

		instance = this;
	}

	public static InspectorModule getInstance()
			throws ModuleNotInitializedException {
		if (instance == null)
			throw new ModuleNotInitializedException("Inspector");
		return instance;
	}
}
