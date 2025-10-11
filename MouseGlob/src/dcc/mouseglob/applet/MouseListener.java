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


public interface MouseListener {

	/**
	 * Called when a mouse event happens (move, press, release, click or drag).
	 * 
	 * @param event
	 * @return <code>true</code> if the drag was intercepted, <code>false</code>
	 *         if it was ignored or if it should be treated by the other
	 *         listeners
	 */
	boolean onMouseEvent(MouseEvent event);

}
