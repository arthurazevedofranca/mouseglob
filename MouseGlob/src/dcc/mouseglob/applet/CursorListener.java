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
package dcc.mouseglob.applet;

import processing.core.PConstants;

public interface CursorListener {

	public enum Cursor {
		ARROW(PConstants.ARROW), CROSS(PConstants.CROSS), HAND(PConstants.HAND), MOVE(
				PConstants.MOVE), TEXT(PConstants.TEXT), WAIT(PConstants.WAIT);

		protected final int pCursor;

		private Cursor(int cursor) {
			this.pCursor = cursor;
		}

	}

	void setCursor(Cursor cursor);

}
