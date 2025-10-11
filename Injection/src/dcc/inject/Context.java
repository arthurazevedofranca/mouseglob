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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Context.class);
	private static Context global;

	public static Context getGlobal() {
		if (global == null)
			global = new Context();
		return global;
	}

	private final Map<Class<?>, Object> instances;
	private final Injector injector;

	/**
	 * Default constructor for the {@code Context} class.
	 */
	public Context() {
		instances = new HashMap<Class<?>, Object>();
		injector = new Injector(this);
	}

	/**
	 * Copy constructor for the {@code Context} class.
	 * 
	 * @param other
	 *            - the {@code Context} with which to merge the new empty one
	 */
	public Context(Context other) {
		this();
		merge(other);
	}

	/**
	 * Instantiates and injects all classes determined by the given
	 * {@code Indexer} .
	 * 
	 * @param indexer
	 *            - the {@code Indexer} onto which the desired classes have been
	 *            loaded
	 */
	public void inject(Indexer indexer) {
		injector.inject(indexer);
	}

	public <T> T getNewInstance(Class<T> clazz) {
		return injector.getNewInstance(clazz);
	}

	/**
	 * Gets the existing instance of the given class or instantiates and injects
	 * a new one if not available.
	 * 
	 * @param clazz
	 *            - the class whose instance to retrieve
	 * @return the instance of {@code clazz} contained in this {@code Context}
	 */
	public <T> T getInstance(Class<T> clazz) {
		T instance = get(clazz);
		if (instance == null) {
			instance = getNewInstance(clazz);
			if (instance != null) {
				putInstance(clazz, instance);
				log.debug("New instance for {}: {}", clazz, instance);
			} else {
				log.warn("Could not instantiate {}", clazz);
			}
		} else {
			// log.debug("Got instance for {}: {}", clazz, instance);
		}
		return instance;
	}

	<T> T getIncomplete(Class<T> clazz) {
		T instance = get(clazz);
		if (instance == null) {
			instance = injector.instantiate(clazz);
			if (instance != null) {
				putInstance(clazz, instance);
				log.debug("New instance for {}: {}", clazz, instance);
			} else {
				log.warn("Could not instantiate {}", clazz);
			}
		} else {
			// log.debug("Got instance for {}: {}", clazz, instance);
		}
		return instance;
	}

	/**
	 * Gets the existing instance of the given class or instantiates and injects
	 * a new one if not available.
	 * <p>
	 * An <i>ad hoc</i> copy of this {@code Context} is created and any existing
	 * instances of {@code args}' classes are overridden with {@code args}.
	 * 
	 * @param clazz
	 *            - the class whose instance to retrieve
	 * @param args
	 *            - instances to inject
	 * @return the instance of {@code clazz} contained in this {@code Context}
	 */
	public <T> T getInstance(Class<T> clazz, Object... args) {
		Context context = new Context(this);
		for (Object arg : args)
			context.putInstance(arg);
		return context.getInstance(clazz);
	}

	private <T> T get(Class<T> clazz) {
		Object instance = instances.get(clazz);
		if (instance != null)
			return clazz.cast(instance);
		for (Class<?> key : instances.keySet())
			if (clazz.isAssignableFrom(key))
				return clazz.cast(instances.get(key));
		return null;
	}

	public <T> void putInstance(Class<T> clazz, T instance) {
		instances.put(clazz, instance);
	}

	public void putInstance(Object instance) {
		log.debug("Putting instance: {}", instance);
		instances.put(instance.getClass(), instance);
	}

	public void merge(Context context) {
		instances.putAll(context.instances);
	}

	public boolean contains(Class<?> clazz) {
		return instances.containsKey(clazz);
	}

	public boolean containsAll(Collection<Class<?>> classes) {
		for (Class<?> clazz : classes)
			if (!contains(clazz))
				return false;
		return true;
	}

	public GraphValidator.ValidationResult validate(Indexer indexer) {
		GraphValidator.ValidationResult result = GraphValidator.validate(indexer, this);
		if (result.ok) {
			log.debug("\n{}", result.report);
		} else {
			log.warn("\n{}", result.report);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Instances:\n");
		List<Class<?>> classes = new ArrayList<>(instances.keySet());
		Collections.sort(classes, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Class<?> clazz : classes)
			sb.append(clazz).append(": ").append(instances.get(clazz))
					.append('\n');
		return sb.toString();
	}

}
