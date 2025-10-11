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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Inspector {
	private final String name;
	private List<OrderedInspector> propertyInspectors;
	private List<Inspector> parents;
	private JPanel panel;

	protected Inspector(String name) {
		this.name = name;
		propertyInspectors = new ArrayList<OrderedInspector>();
		parents = new ArrayList<Inspector>();
	}

	public Inspector(String name, InspectableObject object) {
		this(name);
		Inspector inspector = InspectorBuilder.build(object);
		merge(inspector);
	}

	public final JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			JLabel nameLabel = new JLabel(name);
			nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(nameLabel);
			panel.add(Box.createRigidArea(new Dimension(0, 20)));

			Collections.sort(propertyInspectors);
			for (OrderedInspector p : propertyInspectors) {
				panel.add(p.inspector.makePanel());
				panel.add(Box.createRigidArea(new Dimension(0, 20)));
			}

			panel.add(Box.createVerticalGlue());
			update();
		}
		return panel;
	}

	public void update() {
		for (OrderedInspector o : propertyInspectors)
			o.inspector.update();
		panel = getPanel();
		panel.invalidate();
		for (Inspector parent : parents)
			parent.update();
	}

	public final void add(PropertyInspector<?> p) {
		add(p, Integer.MAX_VALUE);
	}

	public final void add(PropertyInspector<?> p, int order) {
		propertyInspectors.add(new OrderedInspector(p, order));
	}

	public final void merge(Inspector i) {
		for (OrderedInspector o : i.propertyInspectors)
			add(o.inspector, o.order);
		i.parents.add(this);
	}

	@Override
	public String toString() {
		String s = name;
		for (OrderedInspector p : propertyInspectors)
			s += "\r\n - " + p;
		return s;
	}

	private static class OrderedInspector implements
			Comparable<OrderedInspector> {

		private final PropertyInspector<?> inspector;
		private final int order;

		private OrderedInspector(PropertyInspector<?> inspector, int order) {
			this.inspector = inspector;
			this.order = order;
		}

		@Override
		public int compareTo(OrderedInspector o) {
			return Integer.compare(order, o.order);
		}

		@Override
		public String toString() {
			return order + ": " + inspector.toString();
		}

	}

}
