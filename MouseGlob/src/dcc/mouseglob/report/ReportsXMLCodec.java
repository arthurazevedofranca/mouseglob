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
package dcc.mouseglob.report;

import org.w3c.dom.Element;

import dcc.xml.XMLCodec;
import dcc.xml.XMLProcessor;

class ReportsXMLCodec extends XMLCodec {

	private final ReportsManager reportsManager;
	private final ReportXMLCodec reportCodec;

	ReportsXMLCodec(XMLProcessor processor, ReportsManager manager) {
		super(processor);
		this.reportsManager = manager;
		reportCodec = new ReportXMLCodec(this);
	}

	@Override
	public void encode(Element reportsElement) {
		for (ReportDescriptor reportDescriptor : reportsManager
				.getSelectedReports())
			reportsElement.appendChild(reportCodec.encode(reportDescriptor));
	}

	@Override
	public void decode(Element element) {
		for (Element reportElement : getChildren(element))
			reportsManager.select(reportCodec.decode(reportElement));
	}

}
