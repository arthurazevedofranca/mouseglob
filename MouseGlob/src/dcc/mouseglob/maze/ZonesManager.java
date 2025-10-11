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
package dcc.mouseglob.maze;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.mouseglob.labelable.Style;
import dcc.mouseglob.maze.ZoneEvent.ZoneEventType;
import dcc.mouseglob.shape.Polygon;
import dcc.tree.DefaultTreeable;
import dcc.tree.TreeBranch;
import dcc.tree.TreeNode;

public final class ZonesManager extends DefaultTreeable implements
		TreeBranch<Zone> {
	private ArrayList<Zone> zones;
	private int polygonCount;
	private int circleCount;

	private TreeNode node;

	private ArrayList<ZoneListener> listeners;

	ZonesManager() {
		super("Zones", "/resource/zones16.png");

		zones = new ArrayList<Zone>();
		polygonCount = 0;
		circleCount = 0;

		node = getNode();

		listeners = new ArrayList<ZoneListener>();
	}

	@Override
	public void add(Zone z) {
		synchronized (zones) {
			zones.add(z);
		}

		if (z.getName() == null)
			z.setName("Zone " + zones.size());

		if (z.getShape() instanceof Polygon)
			polygonCount++;
		else
			circleCount++;

		ZoneEvent event = new ZoneEvent(ZoneEventType.ZONE_ADDED, z);
		for (ZoneListener listener : listeners)
			listener.onZoneEvent(event);

		node.add(z);
	}

	@Override
	public void remove(Zone z) {
		node.remove(z);

		ZoneEvent event = new ZoneEvent(ZoneEventType.ZONE_REMOVED, z);
		for (ZoneListener listener : listeners)
			listener.onZoneEvent(event);

		synchronized (zones) {
			zones.remove(z);
		}

		if (z.getShape() instanceof Polygon)
			polygonCount--;
		else
			circleCount--;
	}

	public void clear() {
		synchronized (zones) {
			zones.clear();
		}
		polygonCount = 0;
		circleCount = 0;

		ZoneEvent event = new ZoneEvent(ZoneEventType.ZONE_REMOVED, null);
		for (ZoneListener listener : listeners)
			listener.onZoneEvent(event);

		updateStructure();
	}

	public int getCircleCount() {
		return circleCount;
	}

	public int getPolygonCount() {
		return polygonCount;
	}

	public int getZoneCount() {
		return zones.size();
	}

	public List<Zone> getZones() {
		return zones;
	}

	public boolean isEmpty() {
		return zones.isEmpty();
	}

	public void addZoneListener(ZoneListener listener) {
		listeners.add(listener);
	}

	public void removeZoneListener(ZoneListener listener) {
		listeners.remove(listener);
	}

	private void updateStructure() {
		node.removeAllChildren();
		synchronized (zones) {
			for (Zone zone : zones)
				node.add(zone);
		}
	}

	public Paintable getRenderer() {
		return new ZonesRenderer();
	}

	public Paintable getBasicRenderer() {
		return new BasicZonesRenderer();
	}

	private class ZonesRenderer implements Paintable {
		@Override
		public void paint(PGraphics g) {
			synchronized (zones) {
				for (Zone zone : zones)
					zone.paint(g);
			}
		}
	}

	private class BasicZonesRenderer implements Paintable {
		@Override
		public void paint(PGraphics g) {
			synchronized (zones) {
				g.pushStyle();
				Style.ZONE.apply(g);
				for (Zone zone : zones)
					zone.getShape().paint(g);
				g.popStyle();
			}
		}
	}

}
