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
package dcc.tree;

import dcc.module.AbstractModule;

public class Tree extends AbstractModule<TreeManager, TreeUI, TreeController> {
	private static Tree instance;

	public Tree(javax.swing.tree.TreeNode root) {
		super(new TreeManager(root), new TreeUI(), new TreeController());

		getModel().addTreeSelectionListener(getController());
		instance = this;
	}

	public static TreeManager getManager() {
		return instance.getModel();
	}
}
