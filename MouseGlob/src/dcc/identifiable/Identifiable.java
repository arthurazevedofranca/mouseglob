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
 * Specifies objects which should be identified by means of a name.
 * 
 * @author Daniel Coelho de Castro
 */
public interface Identifiable {
	/**
	 * Gets the text which identifies the object.
	 * 
	 * @return the name
	 */
	String getName();

}
