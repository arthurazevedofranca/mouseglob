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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ExternalClassLoader {

	private static final FilenameFilter FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return dir.isDirectory() || name.endsWith(".class")
					|| name.endsWith(".jar");
		}
	};

	private final File root;
	private final int rootPathLength;
	private final boolean recursive;
	private ClassLoader classLoader;
	private Set<Class<?>> loadedClasses;

	public ExternalClassLoader(File root) {
		this(root, true);
	}

	public ExternalClassLoader(File root, boolean recursive) {
		this.root = root;
		this.recursive = recursive;
		rootPathLength = root.getAbsolutePath().length() + 1;
	}

	public Set<Class<?>> loadClasses() {
		classLoader = getClassLoader(root);
		loadedClasses = new HashSet<Class<?>>();
		visit(root);
		return loadedClasses;
	}

	private void visit(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles(FILTER)) {
				// If not recursive, visit only the files in the root directory
				if (recursive || !child.isDirectory())
					visit(child);
			}
		} else {
			String path = file.getPath();
			if (path.endsWith(".class")) {
				// Load separate .class file
				loadClass(path.substring(rootPathLength), classLoader);
			} else if (path.endsWith(".jar")) {
				// Scan external JAR
				loadClassesFromJar(file);
			}
		}
	}

	private void loadClassesFromJar(File jarFile) {
		try {
			ClassLoader jarLoader = getClassLoader(jarFile);
			JarInputStream jar = new JarInputStream(new FileInputStream(
					jarFile.getPath()));
			while (true) {
				JarEntry jarEntry = jar.getNextJarEntry();
				if (jarEntry == null)
					break;
				String path = jarEntry.getName();
				if (path.endsWith(".class"))
					loadClass(path, jarLoader);
			}
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadClass(String path, ClassLoader classLoader) {
		String className = pathToClassName(path);
		try {
			Class<?> clazz = classLoader.loadClass(className);
			loadedClasses.add(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}
	}

	private static ClassLoader getClassLoader(File file) {
		try {
			return new URLClassLoader(new URL[] { file.toURI().toURL() });
		} catch (MalformedURLException e) {
			throw new RuntimeException(e); // Shouldn't happen
		}
	}

	private static String pathToClassName(String path) {
		int to = path.lastIndexOf(".class");
		String classPath = path.substring(0, to);
		String className = classPath.replace('\\', '.').replace('/', '.');
		return className;
	}

}
