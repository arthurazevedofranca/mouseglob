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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public abstract class PropertyCollectionInspector<T extends Collection<E>, E>
		extends PropertyInspector<T> {
	private HashMap<E, PropertyInspector<E>> propertyInspectors;
	private JPanel panel;

	protected PropertyCollectionInspector(String name) {
		super(name);
		propertyInspectors = new HashMap<E, PropertyInspector<E>>();
		panel = new JPanel();
	}

	@Override
	public void update() {
		T collection = getValue();
		synchronized (collection) {
			for (E element : collection) {
				PropertyInspector<E> i = propertyInspectors.get(element);
				if (i != null)
					i.update();
				else {
					PropertyInspector<E> pi = PropertyInspector.getBasic(null,
							element);
					propertyInspectors.put(element, pi);
					panel.add(pi.makePanel());
				}
			}
		}
	}

	@Override
	protected JPanel makePanel() {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(name));
		return panel;
	}

	ArrayList<PropertyInspector<?>> getPropertyInspectors() {
		ArrayList<PropertyInspector<?>> pis = new ArrayList<>();
		for (E element : getValue())
			pis.add(propertyInspectors.get(element));
		return pis;
	}
}
