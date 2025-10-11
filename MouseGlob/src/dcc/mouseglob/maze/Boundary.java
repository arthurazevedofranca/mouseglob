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

import processing.core.PGraphics;
import dcc.graphics.math.Vector;
import dcc.mouseglob.inspector.Inspectable;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.labelable.Style;
import dcc.mouseglob.labelable.StyleableObject;
import dcc.mouseglob.shape.Circle;
import dcc.mouseglob.shape.Shape;
import dcc.tree.TreeNode;
import dcc.tree.Treeable;

public class Boundary extends StyleableObject implements Region, Treeable {
	public enum BoundaryType {
		POSITIVE("positive"), NEGATIVE("negative");

		private String value;

		private BoundaryType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public static BoundaryType get(String value) {
			if ("positive".equals(value))
				return POSITIVE;
			if ("negative".equals(value))
				return NEGATIVE;
			return null;
		}
	}

	private String name;
	@Inspectable
	private final Shape shape;
	@Inspectable(value = "Type", order = 0)
	private final BoundaryType type;
	private final TreeNode node;

	private Inspector inspector;

	Boundary(Shape shape, BoundaryType type) {
		this.shape = shape;
		this.type = type;
		node = new TreeNode(this, name, getIconPath());
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	public BoundaryType getType() {
		return type;
	}

	@Override
	public boolean contains(Vector p) {
		return contains(p.x, p.y);
	}

	@Override
	public boolean contains(double x, double y) {
		if (type == BoundaryType.POSITIVE)
			return shape.contains(x, y);
		else
			return !shape.contains(x, y);
	}

	@Override
	public boolean get(int i, int j) {
		return contains(i, j);
	}

	@Override
	protected void doPaint(PGraphics g) {
		shape.paint(g);
	}

	@Override
	protected Style getStyle() {
		return Style.BOUNDARY;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		node.setName(name);
	}

	private String getIconPath() {
		return "/resource/"
				+ ((type == BoundaryType.POSITIVE) ? "positive" : "negative")
				+ ((shape instanceof Circle) ? "Circle" : "Polygon") + "16.png";
	}

	@Override
	public TreeNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Inspector getInspector() {
		if (inspector == null)
			inspector = new Inspector("Boundary", this);
		return inspector;
	}

}
