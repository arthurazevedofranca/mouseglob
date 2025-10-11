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
package dcc.mouseglob.experiment.setup;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.camera.Camera;
import dcc.mouseglob.experiment.setup.wizard.DefaultWizardPanel;
import dcc.mouseglob.movie.AppletResizer;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.movie.MovieUI;

public abstract class DefaultMovieAppletWizardPanel extends DefaultWizardPanel {

	private MouseGlobApplet applet;
	private AppletResizer resizer;
	private final MovieManager movieManager;
	private final MovieUI movieUI;

	public DefaultMovieAppletWizardPanel(Object id, Object back, Object next,
			MovieManager movieManager, MovieUI movieUI) {
		super(id, back, next);
		applet = new MouseGlobApplet();
		this.movieManager = movieManager;
		resizer = new AppletResizer(applet);
		this.movieUI = movieUI;
	}

	public static JPanel getAppletPanel(MouseGlobApplet applet) {
		JPanel appletPanel = new JPanel();
		appletPanel.setLayout(new BoxLayout(appletPanel, BoxLayout.Y_AXIS));
		appletPanel.add(Box.createVerticalGlue());
		appletPanel.add(applet);
		appletPanel.add(Box.createVerticalGlue());
		return appletPanel;
	}

	@Override
	public JPanel getComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(movieUI.makePlaybackPanel(), BorderLayout.SOUTH);
		panel.add(getAppletPanel(Camera.WIDTH, Camera.HEIGHT),
				BorderLayout.CENTER);
		return panel;
	}

	protected MouseGlobApplet getApplet() {
		return applet;
	}

	protected JPanel getAppletPanel(int appletWidth, int appletHeight) {
		applet.init();
		applet.setAppletSize(appletWidth, appletHeight);
		applet.init();
		return getAppletPanel(applet);
	}

	@Override
	public void aboutToDisplayPanel() {
		applet.init();
		movieManager.addMovieListener(resizer);
		movieManager.addNewFrameListener(applet);
	}

	@Override
	public void aboutToHidePanel() {
		movieManager.removeMovieListener(resizer);
		movieManager.removeNewFrameListener(applet);
		applet.noLoop();
		applet.destroy();
	}

}
