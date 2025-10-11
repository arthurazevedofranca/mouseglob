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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypedDependencyGraph<T> {

	private final Class<T> type;
	private final Graph<Class<? extends T>> graph;

	public TypedDependencyGraph(Class<T> type) {
		this.type = type;
		graph = new Graph<Class<? extends T>>();
	}

	public void add(Class<? extends T> clazz) {
		graph.addNode(clazz);
		graph.addEdges(clazz, inspectDependencies(clazz));
	}

	public void addAll(Collection<Class<? extends T>> classes) {
		for (Class<? extends T> clazz : classes)
			add(clazz);
	}

	public boolean dependsOn(Class<? extends T> class1,
			Class<? extends T> class2) {
		return graph.hasEdge(class1, class2);
	}

	public Set<Class<? extends T>> getDependencies(Class<? extends T> clazz) {
		return graph.getOutgoing(clazz);
	}

	private Set<Class<? extends T>> inspectDependencies(Class<?> clazz) {
		Set<Class<? extends T>> dependencies = new HashSet<>();
		for (Class<?> dependency : InjectionUtils.getAllDependencies(clazz)) {
			if (type.isAssignableFrom(dependency)) {
				dependencies.add(dependency.asSubclass(type));
				dependencies.addAll(inspectDependencies(dependency));
			}
		}
		return dependencies;
	}

	public Set<Class<? extends T>> getAllThatDependOn(
			Class<? extends T> dependency) {
		return graph.getIncoming(dependency);
	}

	public List<Class<? extends T>> getOrder() {
		return graph.getTopologicalOrder();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Class<? extends T> clazz : graph.getNodes()) {
			sb.append(String.format("%s (%H)\n", clazz.getSimpleName(),
					clazz.hashCode()));
			for (Class<?> dep : graph.getOutgoing(clazz))
				sb.append(String.format("\t%s (%H)\n", dep.getSimpleName(),
						dep.hashCode()));
		}
		return sb.toString();
	}

}
