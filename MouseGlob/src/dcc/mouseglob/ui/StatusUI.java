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
package dcc.mouseglob.ui;

import javax.swing.JLabel;

import dcc.ui.StatusBar;

/**
 * @author Daniel Coelho de Castro
 */
public class StatusUI {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StatusUI.class);
	private static JLabel statusLabel = new JLabel("Ready.");
	private static JLabel metricsLabel = new JLabel("");

	public static StatusBar makeStatusBar() {
		StatusBar statusBar = new StatusBar();
		statusBar.add(statusLabel);
		statusBar.add(metricsLabel);
		return statusBar;
	}

	/**
	 * Sets the text of the status label.
	 * 
	 * @param text
	 *            - the desired text to be displayed
	 */
	public static void setStatusText(String text) {
		log.info("{}", text);
		statusLabel.setText(text);
	}

	public static void setErrorText(String text) {
		log.error("{}", text);
		statusLabel.setText(text);
	}

	public static void setMetricsText(String text) {
		metricsLabel.setText(text == null ? "" : text);
	}
}
