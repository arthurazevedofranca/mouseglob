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

import java.awt.KeyEventDispatcher;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

/**
 * Controller the Key Event module.
 * 
 * @author Daniel Coelho de Castro
 */
public class KeyEventController extends AbstractController<KeyEventModule>
		implements KeyEventDispatcher, XMLEncodable {
	/**
	 * Displays the key event class creation dialog
	 */
	public final Action newKeyEventClassAction = new NewKeyEventClassAction();
	final Action chooseKeyAction = new ChooseKeyAction();
	final Action addKeyEventClassAction = new AddKeyEventClassAction();

	private int keyCode = -1;
	private boolean isAddingNew = false;

	@Inject
	private KeyEventManager manager;
	@Inject
	private KeyEventView view;

	@SuppressWarnings("serial")
	private class NewKeyEventClassAction extends Action {
		private NewKeyEventClassAction() {
			super("New Key Event Class...", ComponentFactory
					.getIcon("general/New16"));
			setDescription("New Key Event Class...");
		}

		@Override
		public void actionPerformed() {
			view.makeDialog().setVisible(true);
		}
	}

	@SuppressWarnings("serial")
	private class ChooseKeyAction extends Action {
		private ChooseKeyAction() {
			super("Choose Key...", "/resource/chooseKeyEvent16.png");
		}

		@Override
		public void actionPerformed() {
			setEnabled(false);
			isAddingNew = true;
		}
	}

	@SuppressWarnings("serial")
	private class AddKeyEventClassAction extends Action {
		private AddKeyEventClassAction() {
			super("Add Key Event Class", "/resource/addKeyEvent16.png");
		}

		@Override
		public void actionPerformed() {
			if (keyCode != -1) {
				manager.addKeyEventClass(view.getDescription(), keyCode);
				keyCode = -1;
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
		if (isAddingNew) {
			keyCode = e.getKeyCode();
			chooseKeyAction.setEnabled(true);
			isAddingNew = false;
		}

		else
			manager.dispatchKeyEvent(e);

		return false;
	}

	@Override
	public String getTagName() {
		return "keyevents";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new KeyEventManagerXMLCodec(processor, manager);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new KeyEventManagerXMLCodec(processor, manager);
	}

}
