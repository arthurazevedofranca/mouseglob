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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Indexer {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Indexer.class);
	private Set<Class<?>> classesToInstantiate = new LinkedHashSet<>();
	private Set<Field> fieldsToInject = new LinkedHashSet<>();
	private Set<Method> methodsToInject = new LinkedHashSet<>();

	public static Indexer load(Collection<? extends Class<?>> classes) {
		return new Indexer(classes);
	}

	public static Indexer load(Class<?>... classes) {
		Set<Class<?>> classesToLoad = new LinkedHashSet<>();
		for (Class<?> clazz : classes)
			classesToLoad.add(clazz);
		return new Indexer(classesToLoad);
	}

	public static Indexer load(List<String> classNames) {
		Set<Class<?>> classesToLoad = new LinkedHashSet<>();
		ClassLoader classLoader = Context.class.getClassLoader();
		log.info("Loading classes list ({} entries)...", classNames.size());
		for (String className : classNames) {
			try {
				log.debug("Loading class {}", className);
				Class<?> clazz = classLoader.loadClass(className);
				classesToLoad.add(clazz);
			} catch (ClassNotFoundException e) {
				log.error("Class not found: {}", className);
			}
		}
		return new Indexer(classesToLoad);
	}

	public static Indexer load(String classesFileName) {
		return load(ReadUtils.readResource(classesFileName));
	}

	public static Indexer load(InputStream inputStream) {
		return load(ReadUtils.readResource(inputStream));
	}

	private Indexer(Collection<? extends Class<?>> classes) {
		// Perform a Breadth-First Search on the dependency graph
		Queue<Class<?>> classesToInspect = new ArrayDeque<>(classes);
		classesToInstantiate.addAll(classes);
		while (!classesToInspect.isEmpty()) {
			Class<?> clazz = classesToInspect.poll();
			for (Class<?> dependency : inspectClass(clazz)) {
				if (!classesToInstantiate.contains(dependency)) {
					classesToInstantiate.add(clazz);
					classesToInspect.add(dependency);
				}
			}
		}
	}

	private Set<Class<?>> inspectClass(Class<?> clazz) {
		Set<Class<?>> dependencies = new HashSet<>();
		log.debug("Inspecting class: {}", clazz);

		for (Constructor<?> constructor : InjectionUtils.getConstructors(clazz)) {
			log.debug("Inspecting constructor: {}", constructor);
			for (Class<?> type : constructor.getParameterTypes())
				dependencies.add(type);
		}

		for (Field field : InjectionUtils.getFields(clazz)) {
			log.debug("Inspecting field: {}", field);
			dependencies.add(field.getType());
			fieldsToInject.add(field);
		}

		for (Method method : InjectionUtils.getMethods(clazz)) {
			log.debug("Inspecting method: {}", method);
			for (Class<?> type : method.getParameterTypes())
				dependencies.add(type);
			methodsToInject.add(method);
		}

		return dependencies;
	}

	Set<Class<?>> getClassesToInstantiate() {
		return classesToInstantiate;
	}

	Set<Field> getFieldsToInject() {
		return fieldsToInject;
	}

	Set<Method> getMethodsToInject() {
		return methodsToInject;
	}

}
