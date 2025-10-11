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
package dcc.xml.old;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;

public interface XMLProcessor {

	static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	Document getDocument();

}
