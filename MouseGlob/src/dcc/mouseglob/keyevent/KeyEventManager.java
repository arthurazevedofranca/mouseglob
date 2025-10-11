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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dcc.event.EventManager.TimedEventManager;
import dcc.event.TimedEvent;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.report.EventTimelineReport;
import dcc.mouseglob.report.Report;
import dcc.mouseglob.report.ReportHandle;
import dcc.mouseglob.report.ReportInfo;
import dcc.tree.DefaultTreeable;
import dcc.tree.TreeBranch;
import dcc.tree.TreeNode;

/**
 * Listens to the keyboard's keystrokes and intercepts registered key events.
 * 
 * @author Daniel Coelho de Castro
 */
public final class KeyEventManager extends DefaultTreeable implements
		KeyEventDispatcher, TreeBranch<KeyEventClass>,
		TimedEventManager<KeyEventClass>, NewFrameListener {

	@ReportInfo("Key Events")
	class KeyEventReport extends EventTimelineReport {
		public KeyEventReport() {
			super(KeyEventManager.this);
		}
	}

	private final List<KeyEventClass> classes;
	private final List<KeyEventClass> sequence;
	private final EventTimelineReport report;
	private long time;

	// private SrtWriter writer;

	private final TreeNode node;

	public KeyEventManager() {
		super("Key Events", "/resource/keyEvent16.png");

		classes = new ArrayList<KeyEventClass>();
		sequence = new ArrayList<KeyEventClass>();

		// TODO Uncomment when SRT files are to be used
		// try {
		// writer = new SrtWriter("keypresses.srt");
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		report = new KeyEventReport();
		node = getNode();
	}

	@Inject
	private void initReport() {
		node.add(new ReportHandle(report.getDescriptor()) {
			@Override
			public Report getReport() {
				return report;
			}
		});
	}

	public void addKeyEventClass(String description, int keyCode) {
		add(new KeyEventClass(description, keyCode));
	}

	private KeyEventClass find(int keyCode) {
		for (KeyEventClass keyEventClass : classes)
			if (keyEventClass.getKeyCode() == keyCode)
				return keyEventClass;

		return null;
	}

	public String getClassesDescriptions() {
		StringBuilder sb = new StringBuilder();

		for (KeyEventClass eventClass : classes) {
			if (eventClass.isActive()) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(eventClass.getLabel());
			}
		}

		return sb.toString();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();
		KeyEventClass keyEventClass = find(keyCode);

		if (keyEventClass != null && time != 0) {
			switch (e.getID()) {
			case KeyEvent.KEY_PRESSED: {
				keyEventClass.start(time);
				sequence.add(keyEventClass);
				break;
			}

			case KeyEvent.KEY_RELEASED: {
				keyEventClass.stop(time);
				TimedEvent event = keyEventClass.getLastEvent();
				if (event != null) {
					System.out.printf(
							"%s pressed for %d ms, from %d ms to %d ms\n",
							keyEventClass.getDescription(),
							event.getDuration(), event.getStartTime(),
							event.getEndTime());
					// TODO Uncomment when SRT files are to be used
					// writer.write(event);
				}
				break;
			}
			}
		}

		return false;
	}

	@Override
	public void add(KeyEventClass kec) {
		classes.add(kec);
		node.add(kec);
	}

	@Override
	public void remove(KeyEventClass kec) {
		node.remove(kec);
		classes.remove(kec);
	}

	@Override
	public List<KeyEventClass> getEventClasses() {
		return classes;
	}

	@Override
	public void newFrame(Image frame, long time) {
		this.time = time;
		report.setMaxTime(time);
	}

	public EventTimelineReport getReport() {
		return report;
	}

	@Override
	public List<KeyEventClass> getEventClassSequence() {
		return Collections.unmodifiableList(sequence);
	}

}
