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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class XMLWriter implements XMLProcessor {

	private String schema;
	private String fileName;
	private String rootName;

	Document document;
	Element root;

	private PrintWriter writer;

	/**
	 * Constructor for the <code>XMLWriter</code> class.
	 * 
	 * @param schema
	 *            - the schema file (.xsd) name
	 * @param fileName
	 *            - the output file name
	 * @param rootName
	 *            - the name of the root element
	 */
	public XMLWriter(String schema, String fileName, String rootName) {
		URL url = this.getClass().getResource(schema);
		this.schema = url.getFile();
		this.fileName = fileName;
		this.rootName = rootName;

		initialize();
	}

	/**
	 * Constructor for the <code>XMLWriter</code> class.
	 * 
	 * @param fileName
	 *            - the output file name
	 * @param rootName
	 *            - the name of the root element
	 */
	public XMLWriter(String fileName, String rootName) {
		this.fileName = fileName;
		this.rootName = rootName;

		initialize();
	}

	private void initialize() {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

			document = docBuilder.newDocument();

			/*
			 * <ZonesFile xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 * xsi:noNamespaceSchemaLocation="*.xsd">
			 */
			root = document.createElement(rootName);
			if (schema != null) {
				root.setAttribute("xmlns:xsi",
						"http://www.w3.org/2001/XMLSchema-instance");
				root.setAttribute("xsi:noNamespaceSchemaLocation", schema);
			}
			document.appendChild(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Document getDocument() {
		return document;
	}

	protected Element getRoot() {
		return root;
	}

	/**
	 * Writes the contents to the file.
	 * 
	 * @throws FileNotFoundException
	 *             if the file could not be created or written to
	 */
	public void writeFile() throws FileNotFoundException {
		try {
			System.out.println("XMLWriter.writeFile()");
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(document);
			trans.transform(source, result);
			String xmlString = sw.toString();

			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF8")));
			writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			writer.print(xmlString);
			writer.flush();
			writer.close();
		} catch (TransformerException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new element.
	 * 
	 * @param name
	 *            - the name of the element
	 * @return the new element
	 */
	protected Element createElement(String name) {
		return document.createElement(name);
	}

	/**
	 * Creates a new simple element.
	 * 
	 * @param name
	 *            - the name of the element
	 * @param content
	 *            - the content of the element
	 * @return - the new element
	 */
	protected Element createSimpleElement(String name, String content) {
		Element element = createElement(name);
		element.setTextContent(content);
		return element;
	}

	/**
	 * Convenience method to append a simple child element to another element.
	 * 
	 * @param elem
	 *            - the element to which append the child element
	 * @param name
	 *            - the name of the child element
	 * @param content
	 *            - the <code>String</code> content of the simple child element
	 */
	protected void appendSimpleChild(Element elem, String name, String content) {
		elem.appendChild(createSimpleElement(name, content));
	}

	/**
	 * Convenience method to append a simple child element to another element.
	 * 
	 * @param elem
	 *            - the element to which append the child element
	 * @param name
	 *            - the name of the child element
	 * @param content
	 *            - the <code>int</code> content of the simple child element
	 */
	protected void appendSimpleChild(Element elem, String name, int content) {
		appendSimpleChild(elem, name, String.valueOf(content));
	}

	/**
	 * Convenience method to append a simple child element to another element.
	 * 
	 * @param elem
	 *            - the element to which append the child element
	 * @param name
	 *            - the name of the child element
	 * @param content
	 *            - the <code>float</code> content of the simple child element
	 */
	protected void appendSimpleChild(Element elem, String name, float content) {
		appendSimpleChild(elem, name, String.valueOf(content));
	}

	/**
	 * Convenience method to append a simple child element to another element.
	 * 
	 * @param elem
	 *            - the element to which append the child element
	 * @param name
	 *            - the name of the child element
	 * @param content
	 *            - the <code>double</code> content of the simple child element
	 */
	protected void appendSimpleChild(Element elem, String name, double content) {
		appendSimpleChild(elem, name, String.valueOf(content));
	}

	/**
	 * Convenience method to append a simple child element to another element.
	 * <p>
	 * The output format will be <code>yyyy-MM-dd'T'HH:mm:ss</code>.
	 * 
	 * @param elem
	 *            - the element to which append the child element
	 * @param name
	 *            - the name of the child element
	 * @param content
	 *            - the <code>Date</code> content of the simple child element
	 */
	protected void appendSimpleChild(Element elem, String name, Date content) {
		appendSimpleChild(elem, name, DATE_FORMAT.format(content));
	}

	/**
	 * Adds an element to the XML tree.
	 * 
	 * @param elem
	 *            - the element to be added
	 */
	protected void addElement(Element elem) {
		if (elem != null)
			root.appendChild(elem);
	}

	/**
	 * Sets the <code>String</code> content of a simple element.
	 * 
	 * @param elem
	 *            - the element to be changed
	 * @param content
	 *            - the content of the element
	 */
	protected static void setContent(Element elem, String content) {
		elem.setTextContent(content);
	}

	/**
	 * Sets the <code>int</code> content of a simple element.
	 * 
	 * @param elem
	 *            - the element to be changed
	 * @param content
	 *            - the content of the element
	 */
	protected static void setContent(Element elem, int content) {
		setContent(elem, String.valueOf(content));
	}

	/**
	 * Sets the <code>float</code> content of a simple element.
	 * 
	 * @param elem
	 *            - the element to be changed
	 * @param content
	 *            - the content of the element
	 */
	protected static void setContent(Element elem, float content) {
		setContent(elem, String.valueOf(content));
	}

	/**
	 * Sets the <code>double</code> content of a simple element.
	 * 
	 * @param elem
	 *            - the element to be changed
	 * @param content
	 *            - the content of the element
	 */
	protected static void setContent(Element elem, double content) {
		setContent(elem, String.valueOf(content));
	}

	/**
	 * Sets the <code>String</code> value of an attribute of the given element.
	 * 
	 * @param elem
	 *            - the element
	 * @param name
	 *            - the name of the attribute
	 * @param value
	 *            - the value of the attribute
	 */
	protected static void setAttribute(Element elem, String name, String value) {
		elem.setAttribute(name, value);
	}

	/**
	 * Sets the <code>int</code> value of an attribute of the given element.
	 * 
	 * @param elem
	 *            - the element
	 * @param name
	 *            - the name of the attribute
	 * @param value
	 *            - the value of the attribute
	 */
	protected static void setAttribute(Element elem, String name, int value) {
		setAttribute(elem, name, String.valueOf(value));
	}

	/**
	 * Sets the <code>float</code> value of an attribute of the given element.
	 * 
	 * @param elem
	 *            - the element
	 * @param name
	 *            - the name of the attribute
	 * @param value
	 *            - the value of the attribute
	 */
	protected static void setAttribute(Element elem, String name, float value) {
		setAttribute(elem, name, String.valueOf(value));
	}

	/**
	 * Sets the <code>double</code> value of an attribute of the given element.
	 * 
	 * @param elem
	 *            - the element
	 * @param name
	 *            - the name of the attribute
	 * @param value
	 *            - the value of the attribute
	 */
	protected static void setAttribute(Element elem, String name, double value) {
		setAttribute(elem, name, String.valueOf(value));
	}

	/**
	 * Sets the <code>Date</code> value of an attribute of the given element.
	 * <p>
	 * The output format will be <code>yyyy-MM-dd</code>.
	 * 
	 * @param elem
	 *            - the element
	 * @param name
	 *            - the name of the attribute
	 * @param value
	 *            - the of the attribute
	 */
	protected static void setAttribute(Element elem, String name, Date value) {
		setAttribute(elem, name, DATE_FORMAT.format(value));
	}
}
