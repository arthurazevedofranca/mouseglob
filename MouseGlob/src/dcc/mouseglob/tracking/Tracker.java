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
package dcc.mouseglob.tracking;

import processing.core.PGraphics;
import dcc.graphics.Box;
import dcc.graphics.Color;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.StructuringElementFactory;
import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.Vector;
import dcc.graphics.math.async.Blur;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Dataset;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.analysis.analyses.MomentsAnalysis;
import dcc.mouseglob.analysis.analyses.MouseModel;
import dcc.mouseglob.analysis.analyses.PositionAnalysis;
import dcc.mouseglob.inspector.EditablePropertyInspector;
import dcc.mouseglob.inspector.EditablePropertyInspector.TextEditor;
import dcc.mouseglob.inspector.Inspectable;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.inspector.PropertyInspector;
import dcc.mouseglob.labelable.LabelableObject;
import dcc.mouseglob.labelable.Style;
import dcc.mouseglob.maze.BoundaryMask;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;

/**
 * A tracking device which follows globs in a binary image.
 * 
 * @author Daniel Coelho de Castro
 */
public class Tracker extends LabelableObject implements Treeable,
		InspectableObject {
	private static final int[][] DISK = StructuringElementFactory.disk(2);

	@Inspectable(value = "Tracker X", format = "%.1f px", order = 1)
	private double x;
	@Inspectable(value = "Tracker Y", format = "%.1f px", order = 2)
	private double y;
	private Vector center;
	private boolean isTracking;
	private int size;
	private boolean hasData;

	private BinaryImage buffer;
	private ScalarMap cleanMap;
	private ScalarMap blurredMap;

	private Blur blur;

	private Dataset dataset;
	private MomentsAnalysis moments;
	private PositionAnalysis position;
	private MouseModel mouseModel;

	private BoundaryMask boundaryMask;

	private TreeNode node;
	private Inspector inspector;

	/**
	 * Constructor for the <code>Tracker</code> class.
	 * 
	 * @param x
	 *            - initial horizontal coordinate.
	 * @param y
	 *            - initial vertical coordinate.
	 * @param size
	 *            - tracker size
	 */
	public Tracker(double x, double y, int size, BoundaryMask boundaryMask) {
		this.x = x;
		this.y = y;
		this.size = size;
		center = new Vector(x, y);

		int windowSize = 2 * size + 1;
		cleanMap = new ScalarMap(windowSize, windowSize);
		blurredMap = new ScalarMap(windowSize, windowSize);
		blur = new Blur(2);

		this.boundaryMask = boundaryMask;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
		moments = dataset.require(MomentsAnalysis.class);
		position = dataset.require(PositionAnalysis.class);
		mouseModel = dataset.require(MouseModel.class);
	}

	/**
	 * Calculates the new position of this tracker, based on the given image.
	 * 
	 * @param image
	 *            - image to track
	 */
	void update(BinaryImage image, long time) {
		Box imageBox = image.getBox();
		Box.Int trackerBox = getBox().clamp(imageBox).toInt();
		image = image.get(trackerBox);

		boundaryMask.apply(image, trackerBox);
		buffer = image.open(DISK, buffer);
		cleanMap.set(buffer);
		blurredMap = blur.calculate(cleanMap, true);

		position.setBounds(imageBox.width, imageBox.height);
		position.setCornerPosition(trackerBox.left, trackerBox.top);
		dataset.update(time);
		hasData = true;

		if (moments.isEmpty()) {
			isTracking = false;
			return;
		}
		isTracking = true;

		center = position.getPx(-1);
		x = center.x;
		y = center.y;

		if (inspector != null)
			inspector.update();
	}

	public boolean hasData() {
		return hasData;
	}

	public ScalarMap getMap() {
		return blurredMap;// cleanMap;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + "), [" + size + "]";
	}

	public Vector getPosition() {
		return center;
	}

	public int getSize() {
		return size;
	}

	void setSize(int value) {
		size = value;
	}

	@Override
	protected Vector getLabelPosition() {
		return new Vector(x - size, y - size);
	}

	@Override
	protected void doPaint(PGraphics g) {
		g.pushMatrix();
		float fx = (float) x;
		float fy = (float) y;
		g.translate(fx, fy);

		g.rect(0, 0, size, size);
		g.line(-3, 0, 3, 0);
		g.line(0, -3, 0, 3);

		g.stroke(Color.MAGENTA);
		Vector head = mouseModel.getHead();
		if (head != null) {
			Vector.Float fhead = head.toFloat();
			g.ellipse(fhead.x - Math.min(fx, size),
					fhead.y - Math.min(fy, size), 3, 3);
		}
		g.popMatrix();
		paintLabel(g);
	}

	@Override
	protected Style getStyle() {
		if (isSelected())
			return Style.SELECTED;
		else if (!isTracking)
			return Style.TRACKER_ERROR;
		else
			return Style.TRACKER;
	}

	public Box getBox() {
		return Box.fromRadius(x, y, size);
	}

	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public TreeNode getNode() {
		if (node == null)
			node = new TreeNode(this, getName(), "/resource/tracker16.png");
		return node;
	}

	@Override
	public Inspector getInspector() {
		if (inspector == null)
			inspector = new TrackerInspector();
		return inspector;
	}

	TrackerDetailReport getDetail() {
		return new TrackerDetailReport(this, mouseModel);
	}

	private class TrackerInspector extends Inspector {

		private TrackerInspector() {
			super("Tracker", Tracker.this);

			add(new EditablePropertyInspector<String>("Name", new TextEditor()) {
				@Override
				protected String getValue() {
					return getName();
				}

				@Override
				protected void setValue(String value) {
					setName(value);
				}
			}, 0);

			add(new PropertyInspector<String>("Is tracking") {
				@Override
				protected String getValue() {
					return isTracking ? "yes" : "no";
				}
			}, 6);

			add(boundaryMask.getInspector());

			for (Analysis analysis : dataset) {
				if (analysis instanceof ScalarAnalysis)
					add(((ScalarAnalysis) analysis).getInspector());
			}
		}

	}

}
