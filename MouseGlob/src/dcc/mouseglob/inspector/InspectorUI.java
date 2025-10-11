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
import java.awt.Container;

import javax.swing.JScrollPane;

import dcc.module.AbstractView;

/**
 * @author Daniel Coelho de Castro
 */
public class InspectorUI extends AbstractView<InspectorModule> {
	private JScrollPane scroll;

	InspectorUI() {
		scroll = new JScrollPane();
	}

	@Override
	public JScrollPane makePanel() {
		return scroll;
	}

	void setPanel(Component component) {
		scroll.setViewportView(component);
	}

	void print() {
		print(scroll);
	}

	private static void print(Container container) {
		System.out.println(container);
		doPrint(container, 1);
	}

	private static void doPrint(Container container, int lvl) {
		for (Component component : container.getComponents()) {
			int x = lvl;
			while (x-- > 0)
				System.out.print("\t");
			System.out.println(component);
			if (component instanceof Container)
				doPrint((Container) component, lvl + 1);
		}
	}
}
