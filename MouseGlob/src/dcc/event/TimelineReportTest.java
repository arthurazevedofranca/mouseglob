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

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.keyevent.KeyEventManager;
import dcc.mouseglob.report.EventTimelineReport;
import dcc.mouseglob.visit.EventDurationStatisticsReport;
import dcc.tree.Tree;

public class TimelineReportTest {

	public static void main(String[] args) {
		final KeyEventManager manager = new KeyEventManager();
		new Tree(manager.getNode());
		for (int i = 0; i < 9; i++)
			manager.addKeyEventClass("Event " + (char) ('1' + i), KeyEvent.VK_1
					+ i);

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(manager);

		final Time time = new Time();
		final EventDurationStatisticsReport summaryReport = new EventDurationStatisticsReport(
				time, manager, null);
		final long t0 = System.currentTimeMillis();
		new Thread(new Runnable() {
			@Override
			public void run() {
				long t = 0;
				while (true) {// time < 10000) {
					t = System.currentTimeMillis() - t0;
					manager.newFrame(null, t);
					time.add(t);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		EventTimelineReport report = manager.getReport();
		JDialog dialog = report.getDialog();
		WindowAdapter adapter = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		};
		dialog.addWindowListener(adapter);
		dialog.setVisible(true);
		JDialog summaryDialog = summaryReport.getDialog();
		summaryDialog.addWindowListener(adapter);
		summaryDialog.setVisible(true);
	}

}
