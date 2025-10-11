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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Injector {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Injector.class);
	private final Context context;

	Injector(Context context) {
		this.context = context;
	}

	void inject(Indexer indexer) {
		log.info("Instantiating classes...");
		for (Class<?> clazz : indexer.getClassesToInstantiate()) {
			log.debug("Instantiating class: {}", clazz.getName());
			context.getIncomplete(clazz);
		}

		log.info("Injecting fields...");
		for (Field field : indexer.getFieldsToInject()) {
			Object object = context.getInstance(field.getDeclaringClass());
			injectField(object, field);
		}

		log.info("Injecting methods...");
		for (Method method : indexer.getMethodsToInject()) {
			Object object = context.getInstance(method.getDeclaringClass());
			injectMethod(object, method);
		}
	}

	<T> T getNewInstance(Class<T> clazz) {
		T instance = instantiate(clazz);
		for (Field field : InjectionUtils.getFields(clazz))
			injectField(instance, field);
		for (Method method : InjectionUtils.getMethods(clazz))
			injectMethod(instance, method);
		return instance;
	}

	@SuppressWarnings("unchecked")
	<T> T instantiate(Class<T> clazz) {
		try {
			for (Constructor<?> constructor : InjectionUtils
					.getConstructors(clazz))
				return injectConstructor((Constructor<T>) constructor);

			Constructor<T> defaultConstructor = clazz
					.getDeclaredConstructor(new Class<?>[] {});
			defaultConstructor.setAccessible(true);
			return defaultConstructor.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| SecurityException | NoSuchMethodException
				| IllegalArgumentException | InvocationTargetException e) {
			log.error("Failed to instantiate {}: {}", clazz, e.toString());
			return null;
		}
	}

	private <T> T injectConstructor(Constructor<T> constructor)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		log.debug("Injecting constructor: {}", constructor);
		Class<?>[] paramTypes = constructor.getParameterTypes();
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < params.length; i++)
			params[i] = context.getIncomplete(paramTypes[i]);
		constructor.setAccessible(true);
		return constructor.newInstance(params);
	}

	private void injectField(Object object, Field field) {
		log.debug("Injecting field: {}", field);
		Class<?> fieldClazz = field.getType();
		field.setAccessible(true);
		try {
			field.set(object, context.getInstance(fieldClazz));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Failed to inject field {} into {}: {}", field, object, e.toString());
		}
	}

	private void injectMethod(Object object, Method method) {
		log.debug("Injecting method: {}", method);
		Class<?>[] paramTypes = method.getParameterTypes();
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < params.length; i++)
			params[i] = context.getInstance(paramTypes[i]);
		method.setAccessible(true);
		try {
			method.invoke(object, params);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("Failed to inject method {} into {}: {}", method, object, e.toString());
		}
	}

}
