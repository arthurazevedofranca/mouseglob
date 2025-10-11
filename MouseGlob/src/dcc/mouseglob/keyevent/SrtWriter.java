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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Queue;

import dcc.event.TimedEvent;

public class SrtWriter {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"HH:mm:ss,SSS");

	private final Writer writer;
	private final Queue<TimedEvent> events;
	private int counter;

	public static void main(String[] args) throws FileNotFoundException {
		SrtWriter writer = new SrtWriter("foo.srt");
		TimedEvent[] events = new TimedEvent[] { new TimedEvent("asd1", 0),
				new TimedEvent("asd2", 1), new TimedEvent("asd3", 2) };
		for (TimedEvent e : events)
			System.out.println(e + " is active: " + e.isActive());
		int i = 0;
		for (TimedEvent e : events)
			e.stop(10 - i++);
		for (TimedEvent e : events)
			writer.write(e);
		writer.close();
	}

	public SrtWriter(String fileName) throws FileNotFoundException {
		counter = 0;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		events = new ArrayDeque<TimedEvent>();
	}

	void write(TimedEvent event) {
		events.add(event);
		flush();
	}

	private void flush() {
		System.out.println("Flushing... " + events.size() + " events in queue");
		while (!events.isEmpty() && !events.peek().isActive()) {
			TimedEvent event = events.poll();
			System.out.println("Writing: " + event);
			doWrite(event);
		}
	}

	private void doWrite(TimedEvent event) {
		counter++;
		try {
			writer.write(format(counter, (int) event.getStartTime(),
					(int) event.getEndTime(), event.getDescription()));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void close() {
		try {
			flush();
			events.clear();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String format(int counter, int start, int end, String label) {
		return String.format("%d\n%s --> %s\n%s\n\n", counter,
				formatTime(start), formatTime(end), label);
	}

	private static String formatTime(int millis) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.MILLISECOND, millis);
		return FORMAT.format(cal.getTime());
	}

}
