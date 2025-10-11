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
import processing.core.PStyle;
import dcc.graphics.Color;

/**
 * Styles to be used to paint the labelable objects.
 * 
 * @author Daniel Coelho de Castro
 */
public enum Style {
	/** Style for selected objects (zones or trackers). */
	SELECTED {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 2;
			s.stroke = true;
			s.strokeColor = Color.rgb(255, 128, 0);
			s.fill = true;
			s.fillColor = Color.argb(64, 255, 128, 0);
			s.rectMode = PStyle.RADIUS;
			s.ellipseMode = PStyle.CENTER;
		}

		@Override
		LabelStyle getLabelStyle() {
			return new LabelStyle(Color.WHITE, Color.rgb(255, 128, 0));
		}
	},

	/** Style for regular zones. */
	ZONE {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.gray(192);
			s.fill = false;
			s.rectMode = PStyle.CORNER;
			s.ellipseMode = PStyle.CENTER;
		}

		@Override
		LabelStyle getLabelStyle() {
			return new LabelStyle(Color.WHITE, Color.BLACK);
		}
	},

	/** Style for boundary zones. */
	BOUNDARY {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 2;
			s.stroke = true;
			s.strokeColor = Color.rgb(128, 128, 255);
			s.fill = false;
			s.rectMode = PStyle.CORNER;
			s.ellipseMode = PStyle.CENTER;
		}
	},

	/** Style for zones with a tracker inside. */
	INSIDE {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.WHITE;
			s.fill = true;
			s.fillColor = Color.argb(64, 0, 255, 0);
			s.rectMode = PStyle.CORNER;
			s.ellipseMode = PStyle.CENTER;
		}
	},

	/** Style for trackers. */
	TRACKER {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.GREEN;
			s.fill = false;
			s.rectMode = PStyle.RADIUS;
			s.ellipseMode = PStyle.CENTER;
		}

		@Override
		LabelStyle getLabelStyle() {
			return new LabelStyle(Color.BLACK, Color.GREEN);
		}
	},

	/**
	 * Style for trackers outside the boundaries or that aren't tracking
	 * anything at the moment.
	 */
	TRACKER_ERROR {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.RED;
			s.fill = false;
			s.rectMode = PStyle.RADIUS;
		}

		@Override
		LabelStyle getLabelStyle() {
			return new LabelStyle(Color.BLACK, Color.RED);
		}
	},

	/** Style for points. */
	POINT {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.RED;
			s.fill = false;
			s.ellipseMode = PStyle.CENTER;
		}
	},

	/** Style for selected points. */
	SELECTED_POINT {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.RED;
			s.fill = true;
			s.fillColor = Color.RED;
			s.ellipseMode = PStyle.CENTER;
		}
	},

	/** Style for trajectories. */
	TRAJECTORY {
		@Override
		void customizeStyle(PStyle s) {
			s.strokeWeight = 1;
			s.stroke = true;
			s.strokeColor = Color.BLACK;
			s.fill = false;
		}
	};

	final PStyle pStyle;
	final LabelStyle labelStyle;

	private Style() {
		PStyle s = new PStyle();
		customizeStyle(s);
		pStyle = s;
		labelStyle = getLabelStyle();
	}

	abstract void customizeStyle(PStyle s);

	LabelStyle getLabelStyle() {
		return LabelStyle.DEFAULT;
	}

	public void apply(PGraphics g) {
		g.style(pStyle);
	}

	static class LabelStyle {
		public static final LabelStyle DEFAULT = new LabelStyle(Color.WHITE,
				Color.BLACK);

		public final int background;
		public final int foreground;

		/**
		 * Constructor for the <code>LabelStyle</code> class.
		 * 
		 * @param b
		 *            the background color (ARGB)
		 * @param f
		 *            the foreground color (ARGB)
		 */
		private LabelStyle(int b, int f) {
			background = b;
			foreground = f;
		}
	}
}
