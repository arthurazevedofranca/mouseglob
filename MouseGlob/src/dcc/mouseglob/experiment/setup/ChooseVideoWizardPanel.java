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

import javax.swing.JPanel;

import dcc.inject.Inject;
import dcc.mouseglob.movie.MovieEvent;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;
import dcc.mouseglob.movie.MovieListener;
import dcc.mouseglob.movie.MovieManager;
import dcc.mouseglob.movie.MovieUI;

public class ChooseVideoWizardPanel extends DefaultMovieAppletWizardPanel
		implements MovieListener {

	static final Object ID = "choose_video";

	private MovieUI movieUI;

	@Inject
	public ChooseVideoWizardPanel(MovieManager movieManager, MovieUI movieUI) {
		super(ID, ExperimentNameWizardPanel.ID, CalibrationWizardPanel.ID,
				movieManager, movieUI);
		movieManager.addMovieListener(this);
		this.movieUI = movieUI;
//		setNextEnabled(false);
	}

	@Override
	public JPanel getComponent() {
		JPanel panel = super.getComponent();
		panel.add(movieUI.makeOpenMoviePanel(), BorderLayout.NORTH);
		return panel;
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		if (event.getType() == MovieEventType.OPEN)
			setNextEnabled(false);
		else if (event.getType() == MovieEventType.LOAD)
			setNextEnabled(true);
	}

}
