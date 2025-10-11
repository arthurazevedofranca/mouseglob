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

import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.tree.TreeSelectionListener;
import dcc.tree.TreeStructureListener;
import dcc.tree.Treeable;

public class InspectorController extends AbstractController<InspectorModule>
		implements TreeStructureListener, TreeSelectionListener {

	private static final JPanel EMPTY_PANEL = new JPanel();

	@Inject
	private InspectorUI view;

	@Override
	public void nodeAdded(Treeable object) {
	}

	@Override
	public void nodeRemoved(Treeable object) {
		if (object instanceof InspectableObject)
			view.setPanel(EMPTY_PANEL);
	}

	@Override
	public void nodeSelected(Treeable object) {
		if (object instanceof InspectableObject) {
			Inspector inspector = ((InspectableObject) object).getInspector();
			JPanel panel = inspector.getPanel();
			view.setPanel(panel);
		} else {
			view.setPanel(EMPTY_PANEL);
		}
	}

	@Override
	public void nodeDoubleClicked(Treeable object) {
	}

}
