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
package dcc.mouseglob.camera;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import processing.core.PImage;
import dcc.graphics.PImageAdapter;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.ui.StatusUI;
import dcc.util.AveragingStopwatch;

public class CameraManager {
	public static class DeviceInfo {
		public final int index;
		public final String name;
		public DeviceInfo(int index, String name) { this.index = index; this.name = name; }
		@Override public String toString() { return index + (name != null ? (" - " + name) : ""); }
	}

	private final ArrayList<NewFrameListener> newFrameListeners = new ArrayList<>();
	private final Java2DFrameConverter converter = new Java2DFrameConverter();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AveragingStopwatch fpsWatch = new AveragingStopwatch(60);

 private OpenCVFrameGrabber grabber;
	private ExecutorService executor;
	private ScheduledExecutorService scheduler;
	private final BlockingQueue<QueuedFrame> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
	private volatile double lastEffectiveFps = 0.0;
	private dcc.graphics.image.Image scratchImage;
	private final AtomicLong droppedFrames = new AtomicLong(0);
	private int currentDeviceIndex = -1;

	private static final int DEFAULT_DEVICE = 0;
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	private static final int QUEUE_CAPACITY = 5; // small to keep latency low

	private static final class QueuedFrame {
		final Image img; final long t;
		QueuedFrame(Image img, long t) { this.img = img; this.t = t; }
	}

	@Inject
	CameraManager(MouseGlobApplet applet) {
		// lazy start
	}

	public synchronized void start(final MouseGlobApplet applet) {
		start(applet, DEFAULT_DEVICE, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

 public synchronized void start(final MouseGlobApplet applet, int deviceIndex, int width, int height) {
		if (running.get()) {
			StatusUI.setStatusText("Camera already running (device " + deviceIndex + ")");
			return;
		}
		StatusUI.setStatusText("Starting camera (device=" + deviceIndex + ", " + width + "x" + height + ")...");
		try {
			grabber = new OpenCVFrameGrabber(deviceIndex);
			grabber.setImageWidth(width);
			grabber.setImageHeight(height);
			grabber.start();
			running.set(true);
			currentDeviceIndex = deviceIndex;
			droppedFrames.set(0);
			startThreads(applet);
			StatusUI.setStatusText("Camera started: device=" + deviceIndex + ", " + width + "x" + height);
		} catch (Throwable e) {
			StatusUI.setErrorText("Camera start failed: " + e.getMessage());
			dcc.mouseglob.ui.ErrorDialog.showError("Camera error",
				"Falha ao iniciar a câmera. Dicas:\n- Selecione outro dispositivo (menu ou preferências).\n- Verifique permissões de câmera do sistema.\n- Feche outros apps que usam a câmera.", e);
			running.set(false);
			cleanup();
		}
	}

	public synchronized void stop() {
		if (!running.get()) return;
		running.set(false);
		StatusUI.setStatusText("Stopping camera...");
		if (scheduler != null) {
			scheduler.shutdownNow();
			scheduler = null;
		}
		if (executor != null) {
			executor.shutdownNow();
			executor = null;
		}
		cleanup();
		StatusUI.setStatusText("Camera stopped.");
	}

	private void cleanup() {
		try { if (grabber != null) grabber.stop(); } catch (Throwable ignored) {}
		try { if (grabber != null) grabber.release(); } catch (Throwable ignored) {}
		grabber = null;
		queue.clear();
		scratchImage = null;
	}

	private void startThreads(MouseGlobApplet applet) {
		if (executor == null) executor = Executors.newFixedThreadPool(2, r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});
		executor.submit(() -> captureLoop(applet));
		executor.submit(this::dispatchLoop);
		fpsWatch.tic();
		if (scheduler == null) scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "CameraMetrics");
			t.setDaemon(true);
			return t;
		});
		scheduler.scheduleAtFixedRate(() -> {
			double fps = lastEffectiveFps;
			int q = queue.size();
			long drops = droppedFrames.get();
			double cpu = dcc.util.SystemMetrics.getProcessCpuLoadPercent();
			String cpuStr = (cpu >= 0) ? String.format("CPU: %.0f%%", cpu) : "CPU: n/a";
			StatusUI.setMetricsText(String.format("Cam[%d] ON | FPS: %.1f | Q: %d/%d | drop: %d | %s", currentDeviceIndex, fps, q, QUEUE_CAPACITY, drops, cpuStr));
		}, 1, 1, TimeUnit.SECONDS);
	}

	private void captureLoop(MouseGlobApplet applet) {
		boolean dimsNotified = false;
		while (running.get()) {
			try {
				Frame f = grabber.grab();
				if (f == null) { Thread.sleep(5); continue; }
				BufferedImage bi = converter.convert(f);
				if (bi == null) continue;
				if (!dimsNotified) {
					applet.setAppletSize(bi.getWidth(), bi.getHeight());
					dimsNotified = true;
				}
				Image img = PImageAdapter.bufferedToImage(bi, scratchImage);
				scratchImage = img;
				long t = System.currentTimeMillis();
				enqueue(new QueuedFrame(img, t));
			} catch (Throwable e) {
				StatusUI.setErrorText("Camera grab error: " + e.getMessage());
				try { Thread.sleep(100); } catch (InterruptedException ignored) {}
			}
		}
	}

 private void enqueue(QueuedFrame qf) {
		if (!queue.offer(qf)) {
			QueuedFrame removed = queue.poll(); // drop oldest to keep latency small
			if (removed != null) droppedFrames.incrementAndGet();
			queue.offer(qf);
		}
	}

	private void dispatchLoop() {
		while (running.get() || !queue.isEmpty()) {
			try {
				QueuedFrame qf = queue.poll(50, TimeUnit.MILLISECONDS);
				if (qf != null) broadcastFrame(qf.img, qf.t);
			} catch (InterruptedException ignored) {
				break;
			}
		}
	}

	private void broadcastFrame(Image image, long timeMs) {
		long dt = fpsWatch.toc();
		if (dt > 0) lastEffectiveFps = 1_000_000_000.0 / dt;
		fpsWatch.tic();
		for (NewFrameListener l : newFrameListeners) l.newFrame(image, timeMs);
	}

	public List<DeviceInfo> probeDevices(int maxDevices) {
		List<DeviceInfo> list = new ArrayList<>();
		int max = Math.max(1, maxDevices);
		for (int i = 0; i < max; i++) {
			OpenCVFrameGrabber g = new OpenCVFrameGrabber(i);
			try {
				g.setTimeout(500);
				g.start();
				Frame f = g.grab();
				if (f != null) list.add(new DeviceInfo(i, null));
			} catch (Throwable ignored) {
			} finally {
				try { g.stop(); } catch (Throwable ignored2) {}
				try { g.release(); } catch (Throwable ignored3) {}
			}
		}
		if (list.isEmpty()) {
			StatusUI.setErrorText("No camera devices detected (OpenCV)");
			dcc.mouseglob.ui.ErrorDialog.showError("Câmera não detectada",
				"Nenhum dispositivo de câmera foi encontrado. Dicas:\n- Conecte uma câmera e tente novamente.\n- Verifique permissões do sistema.\n- Em laptops, habilite a webcam.");
		}
		return list;
	}

	public void addNewFrameListener(NewFrameListener listener) {
		newFrameListeners.add(listener);
	}

	public void removeNewFrameListener(NewFrameListener listener) {
		newFrameListeners.remove(listener);
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
