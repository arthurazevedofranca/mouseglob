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

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Daniel Coelho de Castro
 */
public class XMLIterator implements Iterator<Element> {
	private Element parent;
	private NodeList children;
	private int index;
	private int number;
	private Element next;

	/**
	 * Constructor for the <code>XMLIterator</code> class.
	 * 
	 * @param e
	 *            - the parent <code>Element</code> over whose children it will
	 *            iterate
	 */
	public XMLIterator(Element e) {
		parent = e;
		children = parent.getChildNodes();
		index = 0;
		number = children.getLength();
		next = findNext();
	}

	/**
	 * Determines whether there is still another child over which to iterate.
	 * 
	 * @return <code>true</code> if there is, <code>false</code> otherwise
	 */
	@Override
	public boolean hasNext() {
		if (next != null)
			return true;
		else
			return false;
	}

	private Element findNext() {
		Node child = children.item(index++);
		while (!(child instanceof Element) && index < number)
			child = children.item(index++);

		if (child instanceof Element)
			return (Element) child;

		return null;
	}

	/**
	 * Gets the next child.
	 * 
	 * @return the child <code>Element</code>
	 */
	@Override
	public Element next() {
		Element temp = next;
		next = findNext();
		return temp;
	}

	/**
	 * Removes the current child <code>Element</code>.
	 */
	@Override
	public void remove() {
		parent.removeChild(next);
	}

	class XMLIterable implements Iterable<Element> {

		@Override
		public Iterator<Element> iterator() {
			return XMLIterator.this;
		}

	}

}
