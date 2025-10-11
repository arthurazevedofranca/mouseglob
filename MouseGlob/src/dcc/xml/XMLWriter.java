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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Daniel Coelho de Castro
 */
public final class XMLWriter {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	private final String fileName;
	private final String schema;

	/**
	 * Constructor for the <code>XMLWriter</code> class.
	 * 
	 * @param fileName
	 *            - the output XML file name
	 * @param schema
	 *            - the schema file (.xsd) name
	 */
	public XMLWriter(String fileName, String schema) {
		this.fileName = fileName;
		if (schema != null) {
			URL url = getClass().getResource(schema);
			this.schema = url.getFile();
		} else {
			this.schema = null;
		}
	}

	/**
	 * Constructor for the <code>XMLWriter</code> class.
	 * 
	 * @param fileName
	 *            - the output XML file name
	 */
	public XMLWriter(String fileName) {
		this(fileName, null);
	}

	/**
	 * Writes the contents to the file.
	 * 
	 * @throws FileNotFoundException
	 *             if the file could not be created or written to
	 */
	public void write(Document document) throws FileNotFoundException {
		Element root = document.getDocumentElement();
		if (schema != null) {
			root.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("xsi:noNamespaceSchemaLocation", schema);
		}
		try {
			System.out.println("XMLWriter.write()");
			String xmlString = getXMLString(document);

			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName),
							"UTF8")));
			writer.println(XML_HEADER);
			writer.print(xmlString);
			writer.flush();
			writer.close();
		} catch (TransformerException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the contents to the file.
	 * 
	 * @throws FileNotFoundException
	 *             if the file could not be created or written to
	 */
	public void write(XMLBuilder builder) throws FileNotFoundException {
		write(builder.build());
	}

	private static String getXMLString(Document document)
			throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer trans = factory.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"4");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(document);
		trans.transform(source, result);
		return sw.toString();
	}

}
