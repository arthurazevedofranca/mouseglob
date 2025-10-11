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
package dcc.mouseglob.visit;

import static dcc.graphics.binary.BinaryMask1DUtils.and;
import static dcc.graphics.binary.BinaryMask1DUtils.not;
import static dcc.graphics.binary.BinaryMask1DUtils.or;

import java.util.Random;

import dcc.event.TimedEvent;
import dcc.event.TimedEventClass;
import dcc.graphics.binary.BinaryBlockSeries;
import dcc.graphics.binary.BinaryMask1D;
import dcc.graphics.binary.BinarySeries;
import dcc.mouseglob.analysis.Dataset.Time;

public class TimedEventSeriesFactory {

	public static BinarySeries bsFrom(TimedEventClass eventClass, Time time) {
		int n = time.size();
		BinarySeries series = new BinarySeries(n);
		int i = 0;
		for (TimedEvent event : eventClass.getEvents()) {
			double start = event.getStartTime() / 1000.0;
			double end = event.getEndTime() / 1000.0;
			while (time.get(i) < start && i < n) {
				series.add(false);
				i++;
			}
			if (!event.isActive()) {
				while (time.get(i) < end && i < n) {
					series.add(true);
					i++;
				}
			} else {
				while (i < n) {
					series.add(true);
					i++;
				}
			}
		}
		while (i < n) {
			series.add(false);
			i++;
		}
		return series;
	}

	public static BinaryBlockSeries bbsFrom(TimedEventClass eventClass,
			Time time) {
		int n = time.size();
		BinaryBlockSeries series = new BinaryBlockSeries(n);
		for (TimedEvent event : eventClass.getEvents()) {
			double start = event.getStartTime() / 1000.0;
			double end = event.getEndTime() / 1000.0;
			int startIndex = time.find(start);
			int endIndex = n;
			if (!event.isActive())
				endIndex = time.find(end);
			series.add(startIndex, endIndex);
		}
		return series;
	}

	public static void main(String[] args) {
		Time time = new Time();
		TimedEventClass eventClass = new TimedEventClass() {
			@Override
			public String getDescription() {
				return "Foo";
			}
		};
		int n = 100;

		Random rand = new Random();
		double p = 0.9;
		for (int i = 0; i < n; i++) {
			time.add(i);
			double s = rand.nextDouble();
			if (!eventClass.isActive() && s < p)
				eventClass.start(1000 * i);
			else if (eventClass.isActive() && s < p)
				eventClass.stop(1000 * i);
		}

		for (TimedEvent event : eventClass.getEvents()) {
			System.out.println(event);
		}

		BinarySeries bs = bsFrom(eventClass, time);
		System.out.println(bs.count() + ": " + bs);
		BinaryBlockSeries bbs = bbsFrom(eventClass, time);
		System.out.println(bbs.count() + ": " + bbs);
		BinaryMask1D bbsBs = BinaryBlockSeries.from(bs, n);
		BinaryMask1D bbsXor = and(or(bbs, bbsBs), not(and(bbs, bbsBs)));
		System.out.println(bbsXor);
		BinaryMask1D bsXor = and(or(bs, bbs), not(and(bs, bbs)));
		System.out.println(bsXor);
	}

}
