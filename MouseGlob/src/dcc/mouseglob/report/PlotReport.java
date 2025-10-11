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
package dcc.mouseglob.report;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;

import dcc.graphics.plot.Plot;
import dcc.graphics.plot.Plot.Probe;
import dcc.mouseglob.applet.CursorListener.Cursor;
import dcc.mouseglob.applet.MouseEvent;
import dcc.mouseglob.applet.MouseListener;
import dcc.ui.StatusBar;

public class PlotReport<P extends Plot> extends AppletReport implements
		MouseListener {

	private final P plot;
	private final PlotApplet applet;
	private final StatusBar statusBar = new StatusBar();
	private final JLabel statusLabel = new JLabel(" ");
	private boolean isMouseInside = false;

	public PlotReport(P plot) {
		this.plot = plot;
		applet = new PlotApplet(plot);
		setApplet(applet);
		applet.addMouseListener(this);

		statusBar.add(statusLabel);
	}

	protected void setSize(int width, int height) {
		plot.setSize(width, height);
		applet.setSize(width, height);
	}

	protected P getPlot() {
		return plot;
	}

	protected PlotApplet getApplet() {
		return applet;
	}

	@Override
	public JDialog getDialog() {
		JDialog dialog = super.getDialog();
		dialog.add(statusBar, BorderLayout.SOUTH);
		dialog.pack();
		return dialog;
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		Probe probe = plot.probe(event.getMouseX(), event.getMouseY());
		if (probe != null) {
			String probeText = probe.getText();
			statusLabel.setText(probeText);
			if (!isMouseInside)
				applet.setCursor(Cursor.CROSS);
			isMouseInside = true;
		} else {
			statusLabel.setText(" ");
			if (isMouseInside)
				applet.setCursor(Cursor.ARROW);
			isMouseInside = false;
		}
		return false;
	}

	protected StatusBar getStatusBar() {
		return statusBar;
	}

}
