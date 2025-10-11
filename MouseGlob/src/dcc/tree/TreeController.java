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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import dcc.module.AbstractController;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;

public class TreeController extends AbstractController<Tree> implements
		TreeSelectionListener {

	Action deleteAction = new DeleteAction();
	TreeCellEditor editor = new MGTreeCellEditor();

	@Override
	public void nodeSelected(Treeable object) {
		deleteAction.setEnabled(canDelete(object));
	}

	@Override
	public void nodeDoubleClicked(Treeable object) {
	}

	private boolean canDelete(Treeable object) {
		Treeable obj = getModule().getModel().getSelectedObject();

		TreeNode parentNode = (TreeNode) obj.getNode().getParent();
		if (parentNode == null)
			return false;

		Treeable parent = (Treeable) parentNode.getUserObject();
		return parent instanceof TreeBranch;
	}

	@SuppressWarnings("serial")
	private class DeleteAction extends Action {

		public DeleteAction() {
			super("Delete", ComponentFactory.getIcon("general/Delete16"));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed() {
			Treeable obj = getModule().getModel().getSelectedObject();

			TreeNode parentNode = (TreeNode) obj.getNode().getParent();
			if (parentNode == null)
				return;

			Treeable parent = (Treeable) parentNode.getUserObject();
			if (parent instanceof TreeBranch)
				((TreeBranch<Treeable>) parent).remove(obj);
		}

	}

	@SuppressWarnings("serial")
	private class MGTreeCellEditor extends AbstractCellEditor implements
			TreeCellEditor {

		private JTextField inputField;
		private EditableTreeable objectBeingEdited;

		private MGTreeCellEditor() {
			inputField = new JTextField(40);

			inputField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = inputField.getText();
					objectBeingEdited.getNode().setName(name);
					objectBeingEdited.setName(name);
					fireEditingStopped();
				}
			});

			objectBeingEdited = null;
		}

		@Override
		public Object getCellEditorValue() {
			return objectBeingEdited;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			if (value instanceof TreeNode) {
				TreeNode node = (TreeNode) value;
				Object selectedObject = node.getUserObject();
				if (selectedObject instanceof EditableTreeable) {
					objectBeingEdited = (EditableTreeable) selectedObject;
					inputField.setText(node.getName());
				}
			}
			return inputField;
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			if (e instanceof MouseEvent) {
				if (((MouseEvent) e).getClickCount() >= 2) {
					Treeable obj = getModule().getModel().getSelectedObject();
					return obj instanceof EditableTreeable;
				}
			}
			return false;
		}

	}

}
