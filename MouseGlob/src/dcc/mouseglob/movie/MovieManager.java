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

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import processing.core.PApplet;
import processing.core.PImage;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.movie.MovieEvent.MovieEventType;
import dcc.mouseglob.ui.StatusUI;
import dcc.util.AveragingStopwatch;
import dcc.util.Stopwatch;

public final class MovieManager {
	private enum PlaybackStatus {
		NO_MOVIE, LOADING, READY, PLAYING, FAST_FORWARDING, PAUSED, STOPPED, JUMPING;
	}

	private final PApplet parent;

	private String movieName;

	private Movie movie;

	private boolean loadingFinished;
	private boolean loadingStarted;

	private float pausedPosition = 0;
	private float initialPosition = 0;

	private PlaybackStatus status;

	private MovieCommandExecutor commandExecutor;
	private FrameQueue frameQueue;

	private ArrayList<NewFrameListener> newFrameListeners;
	private ArrayList<MovieListener> movieListeners;

	private ExecutorService commandExecService;
	private ExecutorService workerExecService;
	private ScheduledExecutorService scheduler;
	private volatile double lastEffectiveFps = 0.0;
	private dcc.graphics.image.Image scratchImage;

	final Stopwatch stopwatch;

	@Inject
	MovieManager(PApplet parent) {
		this.parent = parent;
		this.movieName = null;
		this.movie = null;
		this.loadingFinished = false;
		this.loadingStarted = false;
		this.status = PlaybackStatus.NO_MOVIE;
		this.commandExecutor = new MovieCommandExecutor();
		this.frameQueue = new FrameQueue(this);
		this.newFrameListeners = new ArrayList<NewFrameListener>();
		this.movieListeners = new ArrayList<MovieListener>();
		this.stopwatch = new AveragingStopwatch(100);
	}

	public boolean hasMovie() {
		return movie != null;
	}

	public void open(String movieName, float position) {
		notifyMovieListeners(new MovieEvent(MovieEventType.OPEN, null, movieName));

		if (movie != null)
			close();

		if (commandExecService == null) commandExecService = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "MovieCommandExecutor");
			t.setDaemon(true);
			return t;
		});
		commandExecService.submit(commandExecutor);
		if (workerExecService == null) workerExecService = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "FrameQueue");
			t.setDaemon(true);
			return t;
		});
		workerExecService.submit(frameQueue);

		if (scheduler == null) scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "MovieMetrics");
			t.setDaemon(true);
			return t;
		});
		scheduler.scheduleAtFixedRate(() -> {
			FrameQueue.QueueMonitor qm = getQueueMonitor();
			double target = 0.0;
			try { if (movie != null && movie.grabber != null) target = Math.max(0.0, movie.grabber.getFrameRate()); } catch (Throwable ignored) {}
			long drops = qm.getDrops();
			double cpu = dcc.util.SystemMetrics.getProcessCpuLoadPercent();
			String cpuStr = (cpu >= 0) ? String.format("CPU: %.0f%%", cpu) : "CPU: n/a";
			StatusUI.setMetricsText(String.format("Movie FPS: %.1f/%.1f | Q: %d | drop: %d | %s", lastEffectiveFps, target, qm.getSize(), drops, cpuStr));
		}, 1, 1, TimeUnit.SECONDS);

		this.movieName = movieName;
		this.initialPosition = position;
		startLoading();
		this.movie = new Movie(movieName);
	}

	private void startLoading() {
		StatusUI.setStatusText("Loading movie...");
		loadingStarted = true;
		loadingFinished = false;
		status = PlaybackStatus.LOADING;
	}

	private void finishLoading() {
		loadingStarted = false;
		loadingFinished = true;
		status = PlaybackStatus.READY;
		if (initialPosition != 0) {
			status = PlaybackStatus.JUMPING;
			commandExecutor.execute(new Runnable() {
				@Override
				public void run() {
					movie.jump(initialPosition);
					status = PlaybackStatus.READY;
					initialPosition = 0;
				}
			});
		}
		StatusUI.setStatusText("Done loading movie.");

		// Provide a size-only image so listeners (e.g., AppletResizer) can resize the applet
		PImage sizeImage = new PImage(parent.width > 0 ? parent.width : 1, parent.height > 0 ? parent.height : 1);
		notifyMovieListeners(new MovieEvent(MovieEventType.LOAD, sizeImage, movieName));
	}

	void broadcastFrame(PImage frame, long time) {
		broadcastFrame(PImageAdapter.pImageToImage(frame), time);
	}
	
	void broadcastFrame(Image image, long time) {
		long dt = stopwatch.toc();
		if (dt > 0) lastEffectiveFps = 1_000_000_000.0 / dt;
		stopwatch.tic();
		for (NewFrameListener listener : newFrameListeners)
			listener.newFrame(image, time);
	}

	public void close() {
		if (movie != null) {
			movie.stop();
			movie.dispose();
			movie = null;
			loadingStarted = false;
			loadingFinished = false;
			status = PlaybackStatus.NO_MOVIE;
		}
		if (scheduler != null) {
			scheduler.shutdownNow();
			scheduler = null;
		}
		if (commandExecService != null) {
			commandExecService.shutdownNow();
			commandExecService = null;
		}
		if (workerExecService != null) {
			workerExecService.shutdownNow();
			workerExecService = null;
		}
	}

	boolean isOpen() {
		return movie != null;
	}

	private final Runnable playCommand = new Runnable() {
		@Override
		public void run() {
			if (status == PlaybackStatus.PAUSED)
				movie.jump(pausedPosition);
			status = PlaybackStatus.PLAYING;
			movie.speed(1);
			movie.play();
			StatusUI.setStatusText("Movie playing.");
			notifyMovieListeners(new MovieEvent(MovieEventType.PLAY, null, movieName));
		}
	};

	void play() {
		if (status == PlaybackStatus.FAST_FORWARDING)
			pause();
		commandExecutor.execute(playCommand);
	}

	private final Runnable pauseCommand = new Runnable() {
		@Override
		public void run() {
			status = PlaybackStatus.PAUSED;
			movie.speed(1);
			movie.pause();
			StatusUI.setStatusText("Movie paused.");
			notifyMovieListeners(new MovieEvent(MovieEventType.PAUSE, null, movieName));
		}
	};

	void pause() {
		pausedPosition = movie.time();
		commandExecutor.execute(pauseCommand);
	}

	void jump(final float pos) {
		status = PlaybackStatus.JUMPING;
		commandExecutor.execute(new Runnable() {
			@Override
			public void run() {
				status = PlaybackStatus.READY;
				movie.jump(pos);
				notifyMovieListeners(new MovieEvent(MovieEventType.JUMP, null, movieName));
				movie.playOnce(); // decode one frame for preview
			}
		});
	}

	void rewind() {
		commandExecutor.execute(new Runnable() {
			@Override
			public void run() {
				status = PlaybackStatus.READY;
				movie.jump(0);
				notifyMovieListeners(new MovieEvent(MovieEventType.REWIND, null, movieName));
				movie.playOnce(); // decode one frame for preview
			}
		});
	}

	void fastForward() {
		commandExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (status == PlaybackStatus.PAUSED)
					movie.jump(pausedPosition);
				status = PlaybackStatus.FAST_FORWARDING;
				movie.play();
				notifyMovieListeners(new MovieEvent(MovieEventType.PLAY, null, movieName));
			}
		});
	}

	void stop() {
		commandExecutor.execute(new Runnable() {
			@Override
			public void run() {
				status = PlaybackStatus.PAUSED;
				movie.speed(1);
				movie.stop();
				notifyMovieListeners(new MovieEvent(MovieEventType.PAUSE, null, movieName));
			}
		});
	}

	void setSpeed(float speed) {
		movie.speed(speed);
	}

	public String getMovieName() {
		return movieName;
	}

	String getStatus() {
		return status.toString();
	}

	float getPosition() {
		return movie.time();
	}

	float getDuration() {
		return movie.duration();
	}

	private long getTime() {
		return (long) (movie.time() * 1000);
	}

	FrameQueue.QueueMonitor getQueueMonitor() {
		return frameQueue.new QueueMonitor();
	}

	public void addNewFrameListener(NewFrameListener listener) {
		newFrameListeners.add(listener);
	}

	public void removeNewFrameListener(NewFrameListener listener) {
		newFrameListeners.remove(listener);
	}

	public void addMovieListener(MovieListener listener) {
		movieListeners.add(listener);
	}

	public void removeMovieListener(MovieListener listener) {
		movieListeners.remove(listener);
	}

	private void notifyMovieListeners(MovieEvent event) {
		for (MovieListener listener : movieListeners)
			listener.onMovieEvent(event);
	}

	private class MovieCommandExecutor implements Runnable {
		private static class Command { final Runnable r; final java.util.concurrent.CompletableFuture<Void> f; Command(Runnable r) { this.r = r; this.f = new java.util.concurrent.CompletableFuture<>(); } }
		private ArrayDeque<Command> commandQueue;

		public MovieCommandExecutor() {
			commandQueue = new ArrayDeque<Command>();
		}

		@Override
		public void run() {
			while (true) {
				synchronized (commandQueue) {
					if (!commandQueue.isEmpty()) {
						Command cmd = commandQueue.poll();
						try { cmd.r.run(); cmd.f.complete(null); } catch (Throwable t) { cmd.f.completeExceptionally(t); }
					} else {
						try {
							commandQueue.wait();
						} catch (InterruptedException e) {
							return; // allow executor shutdown
						}
					}
				}
			}
		}

		private java.util.concurrent.CompletableFuture<Void> executeAsync(Runnable command) {
			Command cmd = new Command(command);
			synchronized (commandQueue) {
				commandQueue.add(cmd);
				commandQueue.notifyAll();
			}
			return cmd.f;
		}

		private void execute(Runnable command) {
			executeAsync(command); // ignore future for legacy calls
		}
	}

	private final class Movie {
		private final FFmpegFrameGrabber grabber;
		private final Java2DFrameConverter converter = new Java2DFrameConverter();
		private Thread decodeThread;
		private volatile boolean running = false;
		private volatile boolean playing = false;
		private volatile float speed = 1f;
		private volatile long lastTimestampUs = 0L;
		private volatile float durationSec = 0f;
		private volatile int width = 0;
		private volatile int height = 0;

		Movie(String fileName) {
			grabber = new FFmpegFrameGrabber(fileName);
			try {
				grabber.start();
				width = grabber.getImageWidth();
				height = grabber.getImageHeight();
				long lenUs = grabber.getLengthInTime();
				if (lenUs > 0) durationSec = lenUs / 1_000_000f;
				startDecoder();
				} catch (Exception e) {
					StatusUI.setErrorText("Error loading movie: " + e.getMessage());
					dcc.mouseglob.ui.ErrorDialog.showError("Erro ao carregar vídeo",
						"Não foi possível abrir o arquivo. Dicas:\n- Verifique se o caminho existe e você tem permissão.\n- Tente um formato suportado (MP4/H.264).\n- Instale codecs do sistema se necessário.", e);
				}
		}

		private void startDecoder() {
			if (decodeThread != null) return;
			running = true;
			decodeThread = new Thread(() -> {
				boolean firstDimsNotified = false;
				stopwatch.tic();
				while (running) {
					try {
						if (!playing && status != PlaybackStatus.FAST_FORWARDING) {
							Thread.sleep(20);
							continue;
						}
						Frame frame = grabber.grabImage();
						if (frame == null) {
							// End of stream
							pause();
							continue;
						}
						BufferedImage bi = converter.convert(frame);
						if (bi == null) continue;
						if (!firstDimsNotified && bi.getWidth() > 0 && bi.getHeight() > 0) {
							firstDimsNotified = true;
							if (loadingStarted && !loadingFinished) finishLoading();
						}
						lastTimestampUs = grabber.getTimestamp();
						long timeMs = lastTimestampUs / 1000L;
						if (status == PlaybackStatus.FAST_FORWARDING && frameQueue != null) {
							PImage pimg = toPImage(bi);
							frameQueue.add(pimg, timeMs);
						} else if (status != PlaybackStatus.JUMPING) {
							Image img = PImageAdapter.bufferedToImage(bi, scratchImage);
							scratchImage = img;
							broadcastFrame(img, timeMs);
						}
						// Basic pacing in PLAYING
						if (status == PlaybackStatus.PLAYING) {
							double frameRate = grabber.getFrameRate() > 0 ? grabber.getFrameRate() : 30.0;
							long sleepMs = (long) Math.max(0, (1000.0 / (frameRate * Math.max(0.1, speed))) );
							Thread.sleep(sleepMs);
						}
					} catch (Exception e) {
						StatusUI.setErrorText("Movie decoding error: " + e.getMessage());
						playing = false;
					}
				}
			}, "MovieDecoder");
			decodeThread.setDaemon(true);
			decodeThread.start();
		}

		void play() {
			playing = true;
		}

		void playOnce() {
			boolean prevPlaying = playing;
			playing = true;
			try {
				Frame frame = grabber.grabImage();
				if (frame != null) {
					BufferedImage bi = converter.convert(frame);
					if (bi != null) {
						lastTimestampUs = grabber.getTimestamp();
						PImage pimg = toPImage(bi);
						broadcastFrame(pimg, lastTimestampUs / 1000L);
					}
				}
			} catch (Exception e) {
				StatusUI.setErrorText("Movie preview error: " + e.getMessage());
			} finally {
				playing = prevPlaying;
			}
		}

		void pause() {
			playing = false;
		}

		void stop() {
			playing = false;
		}

		void dispose() {
			running = false;
			try {
				if (decodeThread != null) decodeThread.join(200);
			} catch (InterruptedException ignored) {}
			try { grabber.stop(); } catch (Exception ignored) {}
			try { grabber.release(); } catch (Exception ignored) {}
		}

		void speed(float s) { this.speed = s; }

		void jump(float posSeconds) {
			try {
				long targetUs = (long) (posSeconds * 1_000_000L);
				grabber.setTimestamp(targetUs);
				lastTimestampUs = targetUs;
			} catch (Exception e) {
				StatusUI.setErrorText("Seek error: " + e.getMessage());
			}
		}

		float time() { return lastTimestampUs / 1_000_000f; }
		float duration() { return durationSec; }
	}

	private static PImage toPImage(BufferedImage src) {
		BufferedImage bi;
		if (src.getType() != BufferedImage.TYPE_INT_ARGB) {
			bi = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().drawImage(src, 0, 0, null);
		} else {
			bi = src;
		}
		PImage p = new PImage(bi.getWidth(), bi.getHeight(), PImage.ARGB);
		p.loadPixels();
		bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), p.pixels, 0, bi.getWidth());
		p.updatePixels();
		return p;
	}
}
