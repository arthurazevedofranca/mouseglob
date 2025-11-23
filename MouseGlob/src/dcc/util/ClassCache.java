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
package dcc.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassCache {

	private static ClassCache instance;

	public static ClassCache getInstance() {
		if (instance == null)
			instance = new ClassCache();
		return instance;
	}

	private final Set<Class<?>> loadedClasses;
	private final Map<String, Class<?>> forName;

	private ClassCache() {
		loadedClasses = new CacheLoader().load();

		forName = new HashMap<String, Class<?>>();
		for (Class<?> clazz : loadedClasses)
			forName.put(clazz.getName(), clazz);
	}

	public Set<Class<?>> getAll() {
		return new HashSet<>(loadedClasses);
	}

	public Class<?> forName(String className) {
		return forName.get(className);
	}

	public <T> Class<? extends T> forName(String className, Class<T> superclass) {
		return forName(className).asSubclass(superclass);
	}

	public <T> Set<Class<? extends T>> subclasses(Class<T> superclass) {
		Set<Class<? extends T>> subclasses = new HashSet<>();
		for (Class<?> clazz : loadedClasses)
			if (superclass.isAssignableFrom(clazz))
				subclasses.add(clazz.asSubclass(superclass));
		return subclasses;
	}

	public Set<Class<?>> withAnnotation(Class<? extends Annotation> annotation) {
		Set<Class<?>> subclasses = new HashSet<>();
		for (Class<?> clazz : loadedClasses)
			if (clazz.isAnnotationPresent(annotation))
				subclasses.add(clazz);
		return subclasses;
	}

	public <T> Set<Class<? extends T>> subclassesWithAnnotation(
			Class<T> superclass, Class<? extends Annotation> annotation) {
		Set<Class<? extends T>> subclasses = new HashSet<>();
		for (Class<?> clazz : loadedClasses)
			if (superclass.isAssignableFrom(clazz)
					&& clazz.isAnnotationPresent(annotation))
				subclasses.add(clazz.asSubclass(superclass));
		return subclasses;
	}

	private class CacheLoader {

		private final Set<Class<?>> loadedClasses = new HashSet<Class<?>>();

		private Set<Class<?>> load() {
			String projectRoot = System.getProperty("user.dir");
			File bin = new File(projectRoot, "bin");
			File ext = new File(projectRoot, "ext");

			if (bin.exists())
				loadFrom(bin, true);
			else
				loadFrom(new File(projectRoot), false);

			if (ext.exists())
				loadFrom(ext, true);

			return loadedClasses;
		}

		private void loadFrom(File path, boolean recursive) {
			ExternalClassLoader loader = new ExternalClassLoader(path,
					recursive);
			loadedClasses.addAll(loader.loadClasses());
		}

	}

}
