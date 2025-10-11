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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;

import dcc.module.AbstractView;

/**
 * @author Daniel Coelho de Castro
 */
public class TreeUI extends AbstractView<Tree> {
	private final static int MINIMUM_WIDTH = 250;

	@Override
	public JPanel makePanel() {
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.PAGE_AXIS));

		TreeManager manager = getModule().getModel();
		TreeController controller = getModule().getController();

		JTree tree = manager.getTree();
		tree.setCellRenderer(new MGTreeCellRenderer());
		tree.setCellEditor(controller.editor);
		tree.expandRow(0);

		JScrollPane scroll = new JScrollPane(tree);

		JPanel aux = new JPanel();
		aux.setLayout(new BoxLayout(aux, BoxLayout.LINE_AXIS));
		aux.add(controller.deleteAction.getIconButton());

		treePanel.add(scroll);
		treePanel.add(aux);

		Dimension dim = new Dimension(MINIMUM_WIDTH, 0);
		scroll.setMinimumSize(dim);

		return treePanel;
	}

	@SuppressWarnings("serial")
	private static class MGTreeCellRenderer extends DefaultTreeCellRenderer {
		// private static final Dimension DEFAULT_SIZE = new Dimension(200, 20);
		private static final Border BORDER = BorderFactory.createEmptyBorder(2,
				0, 2, 0);

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object node,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, node, sel, expanded, leaf,
					row, hasFocus);

			if (node instanceof TreeNode) {
				Icon icon = ((TreeNode) node).getIcon();
				if (icon != null)
					setIcon(icon);
				// TODO Properly resize when text is changed
				setText(((TreeNode) node).getName());
			}

			// TODO Add a small gap between tree nodes
			setBorder(BORDER);
			// setPreferredSize(DEFAULT_SIZE);

			return this;
		}
	}
}
