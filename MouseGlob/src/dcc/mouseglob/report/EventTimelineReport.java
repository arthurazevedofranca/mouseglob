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

import java.util.List;

import dcc.event.EventClass;
import dcc.event.EventManager;
import dcc.event.EventManager.TimedEventManager;
import dcc.event.EventTimeline;
import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.util.TextTransfer;

@ReportIcon("/resource/timelineReport16.png")
public class EventTimelineReport extends PlotReport<EventTimeline> {

	private static final int DEFAULT_WIDTH = 600, DEFAULT_HEIGHT = 48 /* 200 */,
			DEFAULT_BAR_HEIGHT = 24;

	private final EventManager<?> listener;

	public EventTimelineReport(TimedEventManager<?> listener) {
		super(new EventTimeline(listener, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT
				+ listener.getEventClasses().size() * DEFAULT_BAR_HEIGHT));
		this.listener = listener;
		PopupMenu popupMenu = getPopupMenu();
		popupMenu.add(copySequence);
	}

	@SuppressWarnings("serial")
	private Action copySequence = new Action("Copy event sequence to clipboard") {
		@Override
		public void actionPerformed() {
			List<? extends EventClass<?>> sequence = listener
					.getEventClassSequence();
			int length = sequence.size();
			StringBuilder sb = new StringBuilder();
			sb.append(sequence.get(0).getShortDescription());
			for (int i = 1; i < length; i++)
				sb.append('-').append(sequence.get(i).getShortDescription());
			TextTransfer.copy(sb.toString());
		}
	};

	public void setMinTime(long minTime) {
		getPlot().setMinTime(minTime);
		getApplet().redraw();
	}

	public void setMaxTime(long maxTime) {
		getPlot().setMaxTime(maxTime);
		getApplet().redraw();
	}

}
