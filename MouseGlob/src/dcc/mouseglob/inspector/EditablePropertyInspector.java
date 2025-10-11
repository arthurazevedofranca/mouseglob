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
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dcc.ui.ButtonGroup;
import dcc.ui.RadioAction;
import dcc.ui.SliderAction;
import dcc.ui.TextAction;

public abstract class EditablePropertyInspector<T> extends PropertyInspector<T> {
	private Editor<T> editor;

	protected EditablePropertyInspector(String name, Editor<T> editor) {
		super(name);
		this.editor = editor;
		editor.propertyName = name;
		editor.propertyInspector = this;
		editor.setValue(getValue());
	}

	@Override
	void update() {
		editor.setValue(getValue());
	}

	@Override
	Component getComponent() {
		return editor.getEditorComponent();
	}

	protected abstract void setValue(T value);

	@Override
	public String toString() {
		return super.toString() + " (" + editor + ")";
	}

	public abstract static class Editor<T> {
		EditablePropertyInspector<T> propertyInspector;
		String propertyName;

		abstract Component getEditorComponent();

		protected abstract void setValue(T value);
	}

	public static class TextEditor extends Editor<String> {
		private TextAction action;
		private boolean editing;

		@SuppressWarnings("serial")
		public TextEditor() {
			action = new TextAction(propertyName) {
				@Override
				public void textChanged(String text) {
					propertyInspector.setValue(text);
				}

				@Override
				public void focusGained() {
					editing = true;
				}

				@Override
				public void focusLost() {
					editing = false;
				}
			};
			editing = false;
		}

		@Override
		Component getEditorComponent() {
			return action.getTextField(FIELD_SIZE);
		}

		@Override
		protected void setValue(String value) {
			if (!editing)
				action.setText(value);
		}

		@Override
		public String toString() {
			return "TextEditor";
		}
	}

	public static class SliderEditor extends Editor<Integer> {
		private SliderAction action;

		public SliderEditor(int min, int max) {
			this(min, max, min);
		}

		public SliderEditor(int min, int max, int value) {
			action = new SliderAction(propertyName, min, max, value) {
				@Override
				public void valueChanged(int value) {
					propertyInspector.setValue(value);
				}
			};
		}

		@Override
		Component getEditorComponent() {
			return action.getSlider(new Dimension(120, 60));
		}

		@Override
		protected void setValue(Integer value) {
			action.setValue(value);
		}

		@Override
		public String toString() {
			return "SliderEditor [" + action.getMinimum() + ", "
					+ action.getMaximum() + "]";
		}
	}

	public static class EnumEditor<E extends Enum<E>> extends Editor<E> {
		private ButtonGroup buttonGroup;
		private HashMap<E, RadioAction> actions;

		@SuppressWarnings("serial")
		@SafeVarargs
		public EnumEditor(E... constants) {
			buttonGroup = new ButtonGroup();
			actions = new HashMap<E, RadioAction>();
			for (final E c : constants) {
				RadioAction action = new RadioAction(c.toString()) {
					@Override
					public void actionPerformed() {
						propertyInspector.setValue(c);
					}
				};
				buttonGroup.add(action);
				actions.put(c, action);
			}
		}

		@Override
		Component getEditorComponent() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(BorderFactory.createTitledBorder((String) null));
			for (RadioAction action : buttonGroup.getRadioActions())
				panel.add(action.getRadioButton());
			panel.doLayout();
			return panel;
		}

		public static <E extends Enum<E>> EnumEditor<E> getEditor(
				Class<E> enumClass) {
			return new EnumEditor<E>(enumClass.getEnumConstants());
		}

		@Override
		protected void setValue(E value) {
			actions.get(value).setSelected(true);
		}

		@Override
		public String toString() {
			return "EnumEditor [" + "]";
		}
	}
}
