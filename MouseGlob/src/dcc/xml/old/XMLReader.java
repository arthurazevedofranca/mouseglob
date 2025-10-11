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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Daniel Coelho de Castro
 */
public abstract class XMLReader implements XMLProcessor {
	protected String fileName;

	final Document document;
	final Element root;

	/**
	 * Constructor for the <code>XMLReader</code> class.
	 * 
	 * @param fileName
	 *            - the name of the XML file to be parsed
	 * @throws IOException
	 *             if there were problems reading the file
	 */
	public XMLReader(String fileName) throws FileNotFoundException, IOException {
		this.fileName = fileName;
		InputStream is = new FileInputStream(fileName);
		try {
			document = initialize(is);
		} catch (SAXException e) {
			throw new IOException("Failed to parse: " + e.getMessage(), e);
		}
		root = document.getDocumentElement();
	}

	/**
	 * Parses the XML file.
	 * 
	 * @return the parsed <code>Object</code>
	 */
	protected abstract Object parse();

	/**
	 * Gets and parses the given XML file.
	 * 
	 * @param inputStream
	 *            - the <code>InputStream</code> to read from
	 * @return the parsed <code>Document</code>
	 * @throws FileNotFoundException
	 *             if the file could not be found or read from
	 */
	private static Document initialize(InputStream inputStream)
			throws FileNotFoundException, SAXException, IOException {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			return document;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	public Element getRoot() {
		return root;
	}

	/**
	 * Gets the first occurrence of a child <code>Element</code> with the given
	 * tag name.
	 * 
	 * @param element
	 *            - the parent <code>Element</code>
	 * @param name
	 *            - the tag name to search for
	 * @return the <code>Element</code> or <code>null</code> if it wasn't found
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static Element getChild(Element element, String name)
			throws XMLNotFoundException {
		try {
			NodeList children = element.getElementsByTagName(name);
			if (children.getLength() == 0)
				throw new XMLNotFoundException(element, name);
			return (Element) children.item(0);
		} catch (DOMException e) {
			throw new XMLNotFoundException(element, name);
		}
	}

	/**
	 * Gets the value of an <code>integer</code> child of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a simple <code>integer</code>
	 *            -valued child
	 * @param name
	 *            - the name of the child <code>Element</code>
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static int getIntChild(Element element, String name)
			throws XMLNotFoundException, XMLParseException {
		return getInt(getChild(element, name).getTextContent());
	}

	/**
	 * Gets the value of a <code>float</code> child of an <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a simple <code>float</code>
	 *            -valued child
	 * @param name
	 *            - the name of the child <code>Element</code>
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static float getFloatChild(Element element, String name)
			throws XMLNotFoundException, XMLParseException {
		return getFloat(getChild(element, name).getTextContent());
	}

	/**
	 * Gets the value of a <code>double</code> child of an <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a simple <code>double</code>
	 *            -valued child
	 * @param name
	 *            - the name of the child <code>Element</code>
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static double getDoubleChild(Element element, String name)
			throws XMLNotFoundException, XMLParseException {
		return getDouble(getChild(element, name).getTextContent());
	}

	/**
	 * Gets the value of a <code>Date</code> child of an <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a simple <code>Date</code>
	 *            -valued ( <code>yyyy-MM-dd'T'HH:mm:ss</code> ) child
	 * @param name
	 *            - the name of the child <code>Element</code>
	 * @return the parsed value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static Date getDateChild(Element element, String name)
			throws XMLParseException, XMLNotFoundException {
		return getDate(getChild(element, name).getTextContent());
	}

	/**
	 * Gets the content of a <code>String</code> child of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a simple <code>String</code>
	 *            -valued child
	 * @param name
	 *            - the name of the child <code>Element</code>
	 * @return the content
	 * @throws XMLNotFoundException
	 *             if the child <code>Element</code> was not found
	 */
	protected static String getStringChild(Element element, String name)
			throws XMLNotFoundException {
		return getChild(element, name).getTextContent();
	}

	/**
	 * Gets the value of an <code>integer</code> attribute of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with an <code>integer</code>
	 *            attribute
	 * @param attribute
	 *            - the attribute to be retrieved
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the attribute was not found
	 */
	protected static int getIntAttribute(Element element, String attribute)
			throws XMLParseException, XMLNotFoundException {
		return getInt(getAttribute(element, attribute));
	}

	/**
	 * Gets the value of a <code>float</code> attribute of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a <code>float</code> attribute
	 * @param attribute
	 *            - the attribute to be retrieved
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the attribute was not found
	 */
	protected static float getFloatAttribute(Element element, String attribute)
			throws XMLParseException, XMLNotFoundException {
		return getFloat(getAttribute(element, attribute));
	}

	/**
	 * Gets the value of a <code>double</code> attribute of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a <code>double</code> attribute
	 * @param attribute
	 *            - the attribute to be retrieved
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the attribute was not found
	 */
	protected static double getDoubleAttribute(Element element, String attribute)
			throws XMLParseException, XMLNotFoundException {
		return getDouble(getAttribute(element, attribute));
	}

	/**
	 * Gets the value of a <code>Date</code> attribute of an
	 * <code>Element</code>.
	 * 
	 * @param element
	 *            - an <code>Element</code> with a <code>Date</code> (
	 *            <code>yyyy-MM-dd'T'HH:mm:ss</code> ) attribute
	 * @param attribute
	 *            - the attribute to be retrieved
	 * @return the value
	 * @throws XMLParseException
	 *             if the value is not properly formatted
	 * @throws XMLNotFoundException
	 *             if the attribute was not found
	 */
	protected static Date getDateAttribute(Element element, String attribute)
			throws XMLParseException, XMLNotFoundException {
		return getDate(getAttribute(element, attribute));
	}

	protected static String getAttribute(Element element, String attribute)
			throws XMLNotFoundException {
		String string = element.getAttribute(attribute);
		if (string.isEmpty())
			throw new XMLNotFoundException(element, attribute);
		return string;
	}

	private static int getInt(String string) throws XMLParseException {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new XMLParseException(string);
		}
	}

	private static float getFloat(String string) throws XMLParseException {
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			throw new XMLParseException(string);
		}
	}

	private static double getDouble(String string) throws XMLParseException {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			throw new XMLParseException(string);
		}
	}

	private static Date getDate(String string) throws XMLParseException {
		try {
			return DATE_FORMAT.parse(string);
		} catch (ParseException e) {
			throw new XMLParseException(string);
		}
	}
}
