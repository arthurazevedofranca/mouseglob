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

public class DefaultTreeable implements Treeable {

	private final TreeNode node;

	public DefaultTreeable(String name, String iconFileName) {
		node = new TreeNode(this, name, iconFileName);
	}

	@Override
	public final TreeNode getNode() {
		return node;
	}

}
