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
package dcc.mouseglob.keyevent;

import dcc.event.TimedEvent;
import dcc.event.TimedEventClass;
import dcc.mouseglob.inspector.EditablePropertyInspector;
import dcc.mouseglob.inspector.EditablePropertyInspector.TextEditor;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.inspector.PropertyInspector;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;

class KeyEventClass extends TimedEventClass implements Treeable,
		InspectableObject {
	private final int keyCode;
	private String label;
	private final String keyText;

	private TreeNode node;

	KeyEventClass(String label, int keyCode) {
		this.label = label;
		this.keyCode = keyCode;
		keyText = KeyDecoder.getKeyText(keyCode);

		node = new TreeNode(this, getDescription(), "/resource/keyEvent16.png");
	}

	@Override
	public String getDescription() {
		return label + " - \'" + keyText + "\'";
	}

	@Override
	public String getShortDescription() {
		return label;
	}

	String getLabel() {
		return label;
	}

	int getKeyCode() {
		return keyCode;
	}

	@Override
	public TimedEvent getNewEvent(long time) {
		return new TimedEvent(label, time);
	}

	@Override
	public TreeNode getNode() {
		return node;
	}

	@Override
	public Inspector getInspector() {
		return new KeyEventClassInspector();
	}

	private class KeyEventClassInspector extends Inspector {
		private KeyEventClassInspector() {
			super("Key Event");

			add(new EditablePropertyInspector<String>("Label", new TextEditor()) {
				@Override
				protected String getValue() {
					return label;
				}

				@Override
				protected void setValue(String value) {
					label = value;
				}
			});

			add(new PropertyInspector<String>("Key") {
				@Override
				protected String getValue() {
					return keyText;
				}
			});

			update();
		}
	}
}
