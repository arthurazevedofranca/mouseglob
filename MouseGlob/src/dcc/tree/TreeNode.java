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

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import dcc.util.ImageLoader;

@SuppressWarnings("serial")
public class TreeNode extends DefaultMutableTreeNode {

	private String name, iconPath;
	private TreeManager manager;

	public TreeNode(Treeable value, String name, String iconPath,
			TreeManager treeManager) {
		super(value);
		this.name = name;
		this.iconPath = iconPath;
		this.manager = treeManager;
	}

	public TreeNode(Treeable value, String name, String iconPath) {
		this(value, name, iconPath, null);
	}

	public final String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public final Icon getIcon() {
		if (iconPath == null)
			return null;
		return ImageLoader.loadIcon(iconPath);
	}

	public final void add(Treeable value) {
		add(value.getNode());
		if (getManager() != null)
			getManager().notifyNodeAdded(value);
	}

	@Override
	public final void add(MutableTreeNode child) {
		super.add(child);

		if (getManager() != null) {
			getManager().nodeStructureChanged(this);
			if (child instanceof DefaultMutableTreeNode) {
				TreePath path = new TreePath(
						((DefaultMutableTreeNode) child).getPath());
				getManager().getTree().scrollPathToVisible(path);
			}
		}
	}

	public final void remove(Treeable value) {
		remove(value.getNode());
		getManager().notifyNodeRemoved(value);
	}

	@Override
	public final void remove(MutableTreeNode child) {
		super.remove(child);
		if (getManager() != null)
			manager.nodeStructureChanged(this);
	}

	@Override
	public final void removeAllChildren() {
		super.removeAllChildren();
		if (getManager() != null) {
			manager.notifyNodeRemoved(null);
			manager.nodeStructureChanged(this);
		}
	}

	private TreeManager getManager() {
		if (manager == null)
			manager = Tree.getManager();
		return manager;
	}

}
