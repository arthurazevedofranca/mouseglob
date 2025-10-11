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

import dcc.event.FilteredTimedEventClass;
import dcc.graphics.binary.BinaryMask1D;
import dcc.graphics.binary.BinarySeries;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.inspector.PropertyInspector;
import dcc.mouseglob.maze.Zone;
import dcc.mouseglob.shape.Polygon;
import dcc.mouseglob.tracking.Tracker;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;

class VisitEventClass extends FilteredTimedEventClass implements Treeable,
		InspectableObject {

	private final Zone zone;
	private final Tracker tracker;
	private final BinarySeries mask;

	private final TreeNode node;
	private final VisitEventClassInspector inspector;

	VisitEventClass(Zone z, Tracker t, long threshold) {
		super(threshold);
		zone = z;
		tracker = t;

		mask = new BinarySeries();

		node = new TreeNode(this, getDescription(), getIconPath(z));
		inspector = new VisitEventClassInspector();
	}

	Zone getZone() {
		return zone;
	}

	Tracker getTracker() {
		return tracker;
	}

	void update(long time) {
		boolean isInside = zone.contains(tracker.getPosition());
		boolean wasInside = isActive();
		if (isInside) {
			if (!wasInside) {
				start(time);
				zone.enterTracker();
			}
		} else if (wasInside) {
			stop(time);
			zone.exitTracker();
		}
		mask.add(isInside);
		inspector.time = time;
		inspector.update();
	}

	@Override
	public String getDescription() {
		return tracker.getName() + " - " + zone.getName();
	}

	@Override
	public String getShortDescription() {
		return zone.getName();
	}

	public BinaryMask1D getMask() {
		return mask;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VisitEventClass) {
			VisitEventClass vec = (VisitEventClass) obj;
			if (vec.zone.equals(zone) && vec.tracker.equals(tracker))
				return true;
		}
		return false;
	}

	private static String getIconPath(Zone zone) {
		if (zone.getShape() instanceof Polygon)
			return "/resource/polygonEvent16.png";
		else
			return "/resource/circleEvent16.png";
	}

	@Override
	public TreeNode getNode() {
		return node;
	}

	@Override
	public Inspector getInspector() {
		return inspector;
	}

	private class VisitEventClassInspector extends Inspector {

		private long time;

		protected VisitEventClassInspector() {
			super("Visit Event");

			add(new PropertyInspector<String>("Tracker") {
				@Override
				protected String getValue() {
					return tracker.getName();
				}
			});

			add(new PropertyInspector<String>("Zone") {
				@Override
				protected String getValue() {
					return zone.getName();
				}
			});

			add(new PropertyInspector<Integer>("Visit count") {
				@Override
				protected Integer getValue() {
					return getStartCount();
				}
			});

			add(new PropertyInspector<Double>("Total duration (s)", "%.2f") {
				@Override
				protected Double getValue() {
					return getDurationStatistics(time).getSum() / 1e3;
				}
			});

			add(new PropertyInspector<Double>("Average duration (s)", "%.2f") {
				@Override
				protected Double getValue() {
					return getDurationStatistics(time).getMean() / 1e3;
				}
			});

			update();
		}
	}

}
