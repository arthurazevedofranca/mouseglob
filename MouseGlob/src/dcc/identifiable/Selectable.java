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
package dcc.identifiable;

/**
 * Specifies objects which can be selected and unselected.
 * 
 * @author Daniel Coelho de Castro
 */
public interface Selectable {
	/**
	 * Sets whether the object is selected or not.
	 * 
	 * @param b
	 *            - <code>true</code> if it is, <code>false</code> otherwise
	 */
	void setSelected(boolean b);

	/**
	 * Determines whether the object is selected.
	 * 
	 * @return <code>true</code> if the object is selected, <code>false</code>
	 *         otherwise
	 */
	boolean isSelected();
}
