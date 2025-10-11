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
package dcc.event;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import processing.core.PApplet;
import dcc.mouseglob.keyevent.KeyEventManager;
import dcc.tree.Tree;

@SuppressWarnings("serial")
public class TimelineTest extends PApplet implements KeyEventDispatcher {

	private static final int WIDTH = 800, HEIGHT = 500;

	private KeyEventManager manager;
	private EventTimeline timeline;

	@Override
	public void setup() {
		size(WIDTH, HEIGHT);

		manager = new KeyEventManager();
		new Tree(manager.getNode());
		for (int i = 0; i < 9; i++)
			manager.addKeyEventClass("Event " + (char) ('1' + i),
					KeyEvent.VK_1 + i);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

		timeline = new EventTimeline(manager, 0, 0, WIDTH, HEIGHT);
		timeline.setMinTime(System.currentTimeMillis());
	}

	@Override
	public void draw() {
		background(255);
		timeline.setMaxTime(System.currentTimeMillis());
		timeline.paint(g);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		manager.newFrame(null, System.currentTimeMillis());
		manager.dispatchKeyEvent(e);
		return false;
	}

}
