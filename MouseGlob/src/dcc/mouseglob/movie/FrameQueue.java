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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import processing.core.PImage;
import dcc.mouseglob.movie.speed.SpeedBalancer;

class FrameQueue implements Runnable {

	private class Frame {
		private PImage frame;
		private long time;

		private Frame(PImage frame, long time) {
			this.frame = frame;
			this.time = time;
		}
	}

	private static final int QUEUE_CAPACITY = 20;

	private final MovieManager movieManager;
	private BlockingQueue<Frame> queue;
	private long prevTime;
	private Frame[] buffer;
	private int index = 0;
	private float currentSpeed;
	private SpeedBalancer speedBalancer;
	private final AtomicLong drops = new AtomicLong(0);

	FrameQueue(MovieManager movieManager) {
		this.movieManager = movieManager;
		queue = new LinkedBlockingQueue<Frame>(QUEUE_CAPACITY);
		buffer = new Frame[QUEUE_CAPACITY];
		speedBalancer = SpeedBalancer.ADAPTIVE;
	}

	void add(PImage frame, long time) {
		if (time == prevTime)
			return;
		// System.out.println(movieManager.stopwatch.toc() + "\tFrame added ("
		// + (queue.size() + 1) + "): " + time + " ms");
		speedBalancer.update(currentSpeed, queue.size());
		int w = frame.width;
		int h = frame.height;
		if (buffer[index] == null)
			buffer[index] = new Frame(new PImage(w, h), time);
		buffer[index].frame.copy(frame, 0, 0, w, h, 0, 0, w, h);
		buffer[index].time = time;
		if (!queue.offer(buffer[index])) {
			Frame removed = queue.poll();
			if (removed != null) drops.incrementAndGet();
			queue.offer(buffer[index]);
		}
		index = (index + 1) % buffer.length;
		prevTime = time;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (speedBalancer.hasSpeedChanged()) {
					currentSpeed = speedBalancer.getSpeed();
					// System.out
					// .println("Setting speed to " + currentSpeed + "x");
					movieManager.setSpeed(currentSpeed);
				}
				Frame frame = queue.poll(100, TimeUnit.MILLISECONDS);
				if (frame != null) {
					movieManager.broadcastFrame(frame.frame, frame.time);
				}
			} catch (InterruptedException e) {
				return; // allow shutdown
			}
		}
	}

	@SuppressWarnings("unused")
	private void flush() {
		Frame frame;
		while ((frame = queue.poll()) != null) {
			this.movieManager.broadcastFrame(frame.frame, frame.time);
		}
	}

	class QueueMonitor {
		int getSize() {
			return queue.size();
		}

		float getSpeed() {
			return currentSpeed;
		}

		long getDrops() {
			return drops.get();
		}
	}
}
