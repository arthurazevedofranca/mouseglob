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
package dcc.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Daniel Coelho de Castro
 */
public final class XMLReader {

	private final String fileName;

	/**
	 * Constructor for the <code>XMLReader</code> class.
	 * 
	 * @param fileName
	 *            - the name of the XML file to be parsed
	 * @throws IOException
	 *             if there were problems reading the file
	 */
	public XMLReader(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Opens and parses the given XML file.
	 * 
	 * @param fileName
	 *            - the name of the file to open
	 * @return the parsed <code>Document</code>
	 * @throws FileNotFoundException
	 *             if the file could not be found or read from
	 */
	public void read(XMLDecoder evaluator) throws FileNotFoundException,
			IOException {
		try (InputStream inputStream = new FileInputStream(fileName)) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Element root = document.getDocumentElement();
			evaluator.decode(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			throw new IOException("Failed to parse: " + e.getMessage(), e);
		}
	}

}
