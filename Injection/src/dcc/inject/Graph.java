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
package dcc.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph<T> {

	private Map<T, Set<T>> edges;
	private Graph<T> reverse;

	public Graph() {
		edges = new LinkedHashMap<T, Set<T>>();
	}

	public final void addNode(T node) {
		edges.put(node, new LinkedHashSet<T>());
	}

	public final void addNodes(Collection<T> nodes) {
		for (T node : nodes)
			addNode(node);
	}

	public final void remove(T node) {
		edges.remove(node);
		for (T other : getNodes())
			getOutgoing(other).remove(node);
	}

	public final void addEdge(T from, T to) {
		if (!edges.containsKey(from))
			addNode(from);
		edges.get(from).add(to);
	}

	public final void addEdges(T from, Collection<T> tos) {
		for (T to : tos)
			addEdge(from, to);
	}

	public final void removeEdge(T from, T to) {
		edges.get(from).remove(to);
	}

	public final boolean isEmpty() {
		return edges.isEmpty();
	}

	public final boolean hasNode(T node) {
		return edges.containsKey(node);
	}

	public final boolean hasEdge(T from, T to) {
		return edges.get(from).contains(to);
	}

	public final Set<T> getOutgoing(T node) {
		return edges.get(node);
	}

	public final Set<T> getIncoming(T node) {
		if (reverse == null)
			reverse = reverse();
		return reverse.getOutgoing(node);
	}

	public final int getDegreeOut(T node) {
		return getOutgoing(node).size();
	}

	public final int getDegreeIn(T node) {
		return getIncoming(node).size();
	}

	@Override
	public final Graph<T> clone() {
		Graph<T> clone = new Graph<T>();
		clone.addNodes(getNodes());
		for (T node : getNodes())
			clone.addEdges(node, getOutgoing(node));
		return clone;
	}

	public final Graph<T> reverse() {
		Graph<T> reverse = new Graph<T>();
		reverse.addNodes(getNodes());
		for (T node : getNodes()) {
			for (T out : getOutgoing(node))
				reverse.addEdge(out, node);
		}
		return reverse;
	}

	public final Set<T> getNodes() {
		return edges.keySet();
	}

	public final Collection<Set<T>> getEdges() {
		return edges.values();
	}

	public final List<T> getTopologicalOrder() {
		List<T> ordered = new ArrayList<>();
		Graph<T> copy = clone();

		// Assumes an acyclic graph, will return a partial ordering otherwise
		while (!copy.isEmpty()) {
			boolean sourceFound = false;
			for (T node : getNodes()) {
				// Add each class with no dependencies to the list and remove
				// from all others' dependencies
				if (copy.hasNode(node) && copy.getDegreeOut(node) == 0) {
					sourceFound = true;
					ordered.add(node);
					copy.remove(node);
				}
			}
			if (!sourceFound)
				break;
		}
		return ordered;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T node : getNodes()) {
			sb.append(String.format("%s (%H)\n", node, node.hashCode()));
			for (T edge : getOutgoing(node))
				sb.append(String.format("\t%s (%H)\n", edge, edge.hashCode()));
		}
		return sb.toString();
	}

}
