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
package dcc.mouseglob.analysis;

import org.w3c.dom.Element;

import dcc.util.ClassCache;
import dcc.xml.XMLCodec;
import dcc.xml.XMLProcessor;

public class AnalysisXMLCodec extends XMLCodec {

	private final AnalysesManager manager;

	protected AnalysisXMLCodec(XMLProcessor processor, AnalysesManager manager) {
		super(processor);
		this.manager = manager;
	}

	@Override
	public void encode(Element element) {
		for (Class<? extends Analysis> analysisClass : manager.getAnalyses()) {
			Element analysisElement = createElement("analysis");
			setAttribute(analysisElement, "class", analysisClass.getName());
			element.appendChild(analysisElement);
		}
	}

	@Override
	public void decode(Element element) {
		ClassCache classCache = ClassCache.getInstance();
		for (Element analysisElement : getChildren(element)) {
			String className = analysisElement.getAttribute("class");
			manager.select(classCache.forName(className, Analysis.class));
		}
	}

}
