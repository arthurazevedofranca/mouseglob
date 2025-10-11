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
package dcc.mouseglob.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class InspectorBuilder {

	static Inspector build(InspectableObject object) {
		Inspector inspector = new Inspector(null);
		Class<?> clazz = object.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			Inspectable a = field.getAnnotation(Inspectable.class);
			if (a != null) {
				field.setAccessible(true);
				if (InspectableObject.class.isAssignableFrom(field.getType())) {
					try {
						InspectableObject value = (InspectableObject) field
								.get(object);
						inspector.merge(value.getInspector());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					PropertyInspector<?> p = fromAnnotation(a, field, object);
					inspector.add(p, a.order());
				}
			}
		}

		for (Method method : clazz.getDeclaredMethods()) {
			Inspectable a = method.getAnnotation(Inspectable.class);
			if (a != null) {
				method.setAccessible(true);
				if (InspectableObject.class.isAssignableFrom(method
						.getReturnType())) {
					try {
						InspectableObject value = (InspectableObject) method
								.invoke(object);
						inspector.merge(value.getInspector());
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					PropertyInspector<?> p = fromAnnotation(a, method, object);
					inspector.add(p, a.order());
				}
			}
		}

		return inspector;
	}

	private static <T> PropertyInspector<T> fromAnnotation(Inspectable a,
			Field field, T object) {
		String name = a.value();
		String format = a.format();
		return new FieldInspector<T>(name, format, field, object);
	}

	private static <T> PropertyInspector<T> fromAnnotation(Inspectable a,
			Method method, T object) {
		String name = a.value();
		String format = a.format();
		return new MethodInspector<T>(name, format, method, object);
	}

	private static class FieldInspector<T> extends PropertyInspector<T> {

		private final Field field;
		private final T object;

		protected FieldInspector(String name, String format, Field field,
				T object) {
			super(name, format);
			this.field = field;
			this.object = object;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T getValue() {
			try {
				return (T) field.get(object);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	private static class MethodInspector<T> extends PropertyInspector<T> {

		private final Method method;
		private final T object;

		protected MethodInspector(String name, String format, Method method,
				T object) {
			super(name, format);
			this.method = method;
			this.object = object;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T getValue() {
			try {
				return (T) method.invoke(object);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

}
