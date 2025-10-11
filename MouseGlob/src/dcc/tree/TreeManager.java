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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

public final class TreeManager {
	private JTree tree;
	private DefaultTreeModel treeModel;

	private List<TreeStructureListener> structureListeners;
	private List<TreeSelectionListener> selectionListeners;

	TreeManager(TreeNode root) {
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setEditable(true);

		class TreeListener extends MouseAdapter implements
				javax.swing.event.TreeSelectionListener {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				notifyNodeSelected(getSelectedObject());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2)
					notifyNodeDoubleClicked(getSelectedObject());
			}
		}
		TreeListener listener = new TreeListener();

		tree.addTreeSelectionListener(listener);
		tree.addMouseListener(listener);

		structureListeners = new ArrayList<TreeStructureListener>();
		selectionListeners = new ArrayList<TreeSelectionListener>();
	}

	JTree getTree() {
		return tree;
	}

	void nodeStructureChanged(TreeNode treeNode) {
		treeModel.nodeStructureChanged(treeNode);
	}

	void notifyNodeAdded(Treeable object) {
		for (TreeStructureListener listener : structureListeners)
			listener.nodeAdded(object);
	}

	void notifyNodeRemoved(Treeable object) {
		for (TreeStructureListener listener : structureListeners)
			listener.nodeRemoved(object);
	}

	void notifyNodeSelected(Treeable object) {
		if (object != null) {
			for (TreeSelectionListener listener : selectionListeners)
				listener.nodeSelected(object);
		}
	}

	void notifyNodeDoubleClicked(Treeable object) {
		System.out.println("Double clicked: " + object);
		if (object != null) {
			for (TreeSelectionListener listener : selectionListeners)
				listener.nodeDoubleClicked(object);
		}
	}

	public void setRoot(Treeable root) {
		treeModel = new DefaultTreeModel(root.getNode());
		tree.setModel(treeModel);
		for (int i = 0; i < tree.getRowCount(); i++)
			tree.expandRow(i);
	}

	/**
	 * Gets the object that is currently selected on the tree.
	 * 
	 * @return the selected object or <code>null</code> if nothing is selected
	 */
	public Treeable getSelectedObject() {
		if (tree != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();

			if (node == null)
				return null;

			Object obj = node.getUserObject();

			if (obj instanceof Treeable)
				return (Treeable) obj;
		}

		return null;
	}

	public void addTreeStructureListener(TreeStructureListener listener) {
		System.out.println("Adding listener (" + this + "): " + listener);
		structureListeners.add(listener);
	}

	public void addTreeSelectionListener(TreeSelectionListener listener) {
		System.out.println("Adding listener (" + this + "): " + listener);
		selectionListeners.add(listener);
	}

}
