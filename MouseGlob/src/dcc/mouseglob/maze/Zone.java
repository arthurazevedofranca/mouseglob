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
import dcc.mouseglob.inspector.EditablePropertyInspector;
import dcc.mouseglob.inspector.EditablePropertyInspector.TextEditor;
import dcc.mouseglob.inspector.Inspectable;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.labelable.LabelableObject;
import dcc.mouseglob.labelable.Style;
import dcc.mouseglob.shape.Circle;
import dcc.mouseglob.shape.Shape;
import dcc.tree.EditableTreeable;
import dcc.tree.TreeNode;

public class Zone extends LabelableObject implements Region, EditableTreeable {
	@Inspectable
	private Shape shape;
	private int trackersInside;
	private TreeNode node;
	private Inspector inspector;

	Zone(Shape shape) {
		this.shape = shape;
		node = new TreeNode(this, getName(), getIconPath());
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		node.setName(name);
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public boolean contains(Vector p) {
		return contains(p.x, p.y);
	}

	@Override
	public boolean contains(double x, double y) {
		return shape.contains(x, y);
	}

	@Override
	public boolean get(int i, int j) {
		return contains(i, j);
	}

	@Override
	protected void doPaint(PGraphics g) {
		shape.paint(g);
		paintLabel(g);
	}

	@Override
	protected Style getStyle() {
		if (isSelected())
			return Style.SELECTED;
		else if (trackersInside > 0)
			return Style.INSIDE;
		return Style.ZONE;
	}

	public void enterTracker() {
		trackersInside++;
	}

	public void exitTracker() {
		trackersInside--;
	}

	@Override
	public TreeNode getNode() {
		return node;
	}

	private String getIconPath() {
		if (shape instanceof Circle)
			return "/resource/circle16.png";
		else
			return "/resource/polygon16.png";
	}

	@Override
	protected Vector getLabelPosition() {
		return shape.getLabelPosition();
	}

	@Override
	public Inspector getInspector() {
		if (inspector == null)
			inspector = new ZoneInspector();
		return inspector;
	}

	private class ZoneInspector extends Inspector {
		private ZoneInspector() {
			super("Zone", Zone.this);

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
		}
	}
}
