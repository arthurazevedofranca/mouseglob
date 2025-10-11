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

import dcc.mouseglob.report.ReportDescriptor.ScalarReportDescriptor;
import dcc.xml.AtomicXMLCodec;
import dcc.xml.XMLProcessor;

class ReportXMLCodec extends AtomicXMLCodec<ReportDescriptor> {

	private final DefaultReportXMLCodec defaultCodec;
	private final ScalarReportXMLCodec scalarCodec;

	ReportXMLCodec(XMLProcessor processor) {
		super(processor);
		defaultCodec = new DefaultReportXMLCodec(processor);
		scalarCodec = new ScalarReportXMLCodec(processor);
	}

	@Override
	public Element encode(ReportDescriptor descriptor) {
		if (descriptor instanceof ScalarReportDescriptor)
			return scalarCodec.encode((ScalarReportDescriptor) descriptor);
		return defaultCodec.encode(descriptor);
	}

	@Override
	public ReportDescriptor decode(Element element) {
		String tagName = element.getTagName();
		if ("scalarreport".equals(tagName))
			return scalarCodec.decode(element);
		return defaultCodec.decode(element);
	}

	private static class DefaultReportXMLCodec extends
			AtomicXMLCodec<ReportDescriptor> {

		private DefaultReportXMLCodec(XMLProcessor processor) {
			super(processor);
		}

		@Override
		public Element encode(ReportDescriptor descriptor) {
			Element element = createElement("report");
			element.setAttribute("class", descriptor.getReportClass().getName());
			return element;
		}

		@Override
		public ReportDescriptor decode(Element element) {
			String className = element.getAttribute("class");
			ReportDescriptor descriptor = ReportDescriptor
					.fromClassName(className);
			return descriptor;
		}

	}

	private static class ScalarReportXMLCodec extends
			AtomicXMLCodec<ScalarReportDescriptor> {

		private ScalarReportXMLCodec(XMLProcessor processor) {
			super(processor);
		}

		@Override
		public Element encode(ScalarReportDescriptor descriptor) {
			Element element = createElement("scalarreport");
			element.setAttribute("analysis", descriptor.getAnalysisClass()
					.getName());
			return element;
		}

		@Override
		public ScalarReportDescriptor decode(Element element) {
			String analysisClassName = element.getAttribute("analysis");
			ScalarReportDescriptor descriptor = ScalarReportDescriptor
					.fromAnalysisClassName(analysisClassName);
			return descriptor;
		}

	}

}
