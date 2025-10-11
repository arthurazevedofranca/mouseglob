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
package dcc.mouseglob.labelable;

import processing.core.PGraphics;
import dcc.graphics.Box;
import dcc.graphics.math.Vector;
import dcc.mouseglob.labelable.Style.LabelStyle;

class Label {

	private static final float HEIGHT = 12;

	private String text;
	private LabelStyle style = LabelStyle.DEFAULT;

	public void setText(String text) {
		this.text = text;
	}

	void setStyle(LabelStyle style) {
		this.style = style;
	}

	void paint(PGraphics g, Vector position) {
		if (text != null && position != null) {
			float width = g.textWidth(text) + 2;
			g.pushStyle();
			g.rectMode(PGraphics.CORNER);
			g.textAlign(PGraphics.RIGHT, PGraphics.BASELINE);
			g.noStroke();

			Box area = Box.fromCorners(width, HEIGHT, g.width, g.height);
			position = area.clamp(position);

			Vector.Float t = position.toFloat();
			float tx = t.x;
			float ty = t.y - 2;
			g.fill(style.background);
			g.rect(tx - width, ty + 2 - HEIGHT, width, HEIGHT);
			g.fill(style.foreground);
			g.text(text, tx, ty);

			g.popStyle();
		}
	}

}
