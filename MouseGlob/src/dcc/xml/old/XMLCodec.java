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

import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class XMLCodec<T> implements XMLProcessor {

	final Document document;

	protected XMLCodec(Document document) {
		this.document = document;
	}

	protected XMLCodec(XMLProcessor processor) {
		this(processor.getDocument());
	}

	@Override
	public Document getDocument() {
		return document;
	}

	public abstract Element encode(T o);

	public abstract T decode(Element e);

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

	protected Iterable<Element> getChildren(Element element) {
		return new XMLIterator(element).new XMLIterable();
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
	 * The output format will be <code>yyyy-MM-dd'T'HH:mm:ss</code>.
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
