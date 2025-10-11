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
package dcc.mouseglob.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.FileType;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.experiment.Experiment;
import dcc.mouseglob.experiment.ExperimentIOManager;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;
import dcc.mouseglob.ui.FileChooser;
import dcc.mouseglob.ui.StatusUI;
import dcc.tree.Tree;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;
import dcc.ui.SliderAction;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

/**
 * @author Daniel Coelho de Castro
 */
public class MovieController extends AbstractController<MovieModule> implements
		NewFrameListener, MovieListener, XMLEncodable {
	Action openMovieAction;
	Action playAction;
	Action pauseAction;
	Action rewindAction;
	Action fastForwardAction;
	PositionAction positionAction;

	@Inject
	private ExperimentIOManager experimentIOManager;

	private FileChooser fileChooser;

	@Inject
	private MovieManager movieManager;

	@Inject
        MovieController() {
		openMovieAction = new OpenMovieAction();
		playAction = new PlayAction();
		pauseAction = new PauseAction();
		rewindAction = new RewindAction();
		fastForwardAction = new FastForwardAction();
		positionAction = new PositionAction();

		playAction.setEnabled(false);
		pauseAction.setEnabled(false);
		rewindAction.setEnabled(false);
		fastForwardAction.setEnabled(false);
		positionAction.setEnabled(false);

                fileChooser = new FileChooser(FileType.MOVIE_FILE, "movie");
        }

	/**
	 * Opens the file chooser dialog to select a movie file.
	 */
	private void openMovie() {
		try {
			String fileName = fileChooser.open();
			if (fileName != null)
				loadMovieFile(fileName, 0);
		} catch (Exception e) {
			e.printStackTrace();
			StatusUI.setStatusText("Error while loading movie.");
		}
	}

	public void loadMovieFile(final String fileName, final float position)
			throws FileNotFoundException {
		if (isMovie(fileName)) {
			new Thread(new MovieLoader(fileName, position)).start();
		}
	}

	private class MovieLoader implements Runnable {

		private final String filePath;
		private final float position;

		private MovieLoader(String filePath, float position) {
			this.filePath = filePath;
			this.position = position;
		}

		@Override
		public void run() {
			try {
				movieManager.open(filePath, position);

				String fileName = new File(filePath).getName();

				Experiment experiment = experimentIOManager
						.getCurrentExperiment();
				experiment.setName(FileType.removeExtension(fileName));
				Tree.getManager().setRoot(experiment);
                                JOptionPane.showMessageDialog(FileChooser.getParent(),
                                                "Movie loaded successfully: " + fileName);
                        } catch (Exception e) {
                                JOptionPane.showMessageDialog(FileChooser.getParent(),
                                                "Error loading movie!");
                                e.printStackTrace();
                        }
                }

	}

	/**
	 * Determines whether the specified file exists and has a supported movie
	 * file extension (.mp4, .mov, .avi or .wmv).
	 * 
	 * @param fileName
	 *            - absolute path of the file to be tested
	 * @return <code>true</code> if there is such file and it is valid,
	 *         <code>false</code> otherwise
	 * @throws FileNotFoundException
	 *             if the file wasn't found
	 */
	private static boolean isMovie(String fileName)
			throws FileNotFoundException {
		if (fileName == null)
			return false;

		if (!FileType.MOVIE_FILE.validateExtension(fileName))
			return false;

		File f = new File(fileName);

		if (!f.exists())
			throw new FileNotFoundException();

		return true;
	}

	@SuppressWarnings("serial")
	private class OpenMovieAction extends Action {
		public OpenMovieAction() {
			super("Open Movie...", ComponentFactory.getIcon("general/Open16"));
		}

		@Override
		public void actionPerformed() {
			openMovie();
		}
	}

	@SuppressWarnings("serial")
	private class PlayAction extends Action {
		public PlayAction() {
			super("Play", ComponentFactory.getIcon("media/Play16"));
		}

		@Override
		public void actionPerformed() {
			System.out.println("MovieController$PlayAction.actionPerformed()");
			if (movieManager.isOpen()) {
				movieManager.play();
				setEnabled(false);
				rewindAction.setEnabled(true);
				fastForwardAction.setEnabled(true);
				pauseAction.setEnabled(true);
			}
		}
	}

	@SuppressWarnings("serial")
	private class FastForwardAction extends Action {
		private FastForwardAction() {
			super("Fast Forward", ComponentFactory
					.getIcon("media/FastForward16"));
			// TODO Remove when FF works
			setDescription("Fast Forward (not yet available)");
			super.setEnabled(false);
		}

		// TODO Remove when FF works
		@Override
		public void setEnabled(boolean enabled) {
		}

		@Override
		public void actionPerformed() {
			System.out
					.println("MovieController$FastForwardAction.actionPerformed()");
			if (movieManager.isOpen()) {
				movieManager.fastForward();
				setEnabled(false);
				rewindAction.setEnabled(true);
				playAction.setEnabled(true);
				pauseAction.setEnabled(true);
			}
		}
	}

	@SuppressWarnings("serial")
	private class PauseAction extends Action {
		public PauseAction() {
			super("Pause", ComponentFactory.getIcon("media/Pause16"));
		}

		@Override
		public void actionPerformed() {
			System.out.println("MovieController$PauseAction.actionPerformed()");
			if (movieManager.isOpen()) {
				movieManager.pause();
				setEnabled(false);
				playAction.setEnabled(true);
				fastForwardAction.setEnabled(true);
			}
		}
	}

	@SuppressWarnings("serial")
	private class RewindAction extends Action {
		public RewindAction() {
			super("Rewind", ComponentFactory.getIcon("media/Rewind16"));
		}

		@Override
		public void actionPerformed() {
			System.out
					.println("MovieController$RewindAction.actionPerformed()");
			if (movieManager.isOpen()) {
				movieManager.rewind();
				setEnabled(false);
				playAction.setEnabled(true);
				fastForwardAction.setEnabled(true);
				pauseAction.setEnabled(false);
			}
		}
	}

	class PositionAction extends SliderAction {
		private String positionText;
		private Collection<JLabel> positionLabels;

		public PositionAction() {
			super(0, 0, 0);

			positionText = "00:00/00:00";
			positionLabels = new ArrayList<JLabel>();
		}

		@Override
		public void valueChanged(int value) {
			positionText = timeFormat(value) + "/" + timeFormat(getMaximum());
			for (JLabel label : positionLabels)
				label.setText(positionText);
		}

		@Override
		public void valueStartedAdjusting(int value) {
			if (pauseAction.isEnabled())
				pauseAction.actionPerformed();
		}

		@Override
		public void valueFinishedAdjusting(int value) {
			movieManager.jump(value);
		}

		JLabel getLabel() {
			JLabel label = new JLabel(positionText);
			positionLabels.add(label);
			return label;
		}

		private String timeFormat(int s) {
			int m = s / 60;
			s %= 60;
			return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
		}
	}

	@Override
	public void newFrame(Image frame, long time) {
		positionAction.setValue((int) (time / 1000));
	}

	@Override
	public void onMovieEvent(MovieEvent event) {
		System.out.println("MovieController.onMovieEvent(" + event.getType()
				+ ")");

		if (event.getType() == MovieEventType.LOAD) {
			int duration = (int) movieManager.getDuration();
			positionAction.setEnabled(true);
			positionAction.setMaximum(duration);
			positionAction.valueChanged(positionAction.getValue());
			playAction.setEnabled(true);
			fastForwardAction.setEnabled(true);
		}
	}

	@Override
	public String getTagName() {
		return "movie";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new MovieXMLEncoder(processor, movieManager);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new MovieXMLDecoder(this);
	}

}
